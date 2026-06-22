package spreads

import spinal.core._
import spinal.core.sim._
import spinal.lib._

case class Receiver_Analog(poly: Array[Int], m_lfsr: Int, n_adc: Int, n_integrator: Int)
    extends Component {
  val io = new Bundle {
    val enable = in Bool ()
    val signal = in SInt (n_adc bits)
    val data = out Bool ()
    val syncd = out Bool ()
  }
  val chips = scala.math.pow(2, m_lfsr).toInt - 1
  val THRESH = 20
  io.syncd := False
  val lfsr = UnrollLFSR(poly.toArray, poly.max + 1, 1, 1)
  lfsr.io.skip := False
  lfsr.io.enable := io.enable

  // var data = False
  // val dataReg = RegNextWhen(data, io.syncd)
  io.data := False
  // three parallel correlators
  var acc = Vec.fill(3)(SInt((n_adc + m_lfsr) bits))
  var accReg = RegNextWhen(acc, io.enable)
  // var accReg = Vec.fill(3)(Reg(SInt((n_adc + m_lfsr) bits)))
  accReg.foreach(_ init(0))
  acc := Vec.fill(3)(0)

  var inputRegVec = Vec.fill(3)(Reg(SInt (n_adc bits)))
  inputRegVec.foreach(_ init(0))

  inputRegVec(0) := io.signal
  inputRegVec(1) := inputRegVec(0)
  inputRegVec(2) := inputRegVec(1)

  var maxReg = Reg(UInt((n_adc + m_lfsr + n_integrator) bits)) init (0) simPublic
  var offsetReg = Reg(UInt(m_lfsr bits)) init(0) simPublic

  var accCount = Counter(1 to chips) init(1)
  var offsetCount = Counter((m_lfsr) bits) init(0)
  var symbolCount = Counter(0 to n_integrator) init(0)

  var multiCorr = UInt((n_adc + m_lfsr + n_integrator) bits)
  var multiCorrReg = RegNext(multiCorr) init (0)
  multiCorr := multiCorrReg

    object TrackState extends SpinalEnum {
    val sSearch, sLocking, sTracking = newElement()
  }

  var dll = DLL(accReg(0).getWidth, THRESH)

  dll.io.accumulator := accReg
  dll.io.enable := False
  
  val state = Reg(TrackState()) init (TrackState.sSearch)
  state.setName("ReceiverState")

  when(io.enable) {
    accCount.increment()

    // TODO general method using scala fold or similar
    for (v <- 0 to 2)
    {
      when(lfsr.io.rnd_o(0) === False) {
        acc(v) := accReg(v) +| inputRegVec(v)
      } otherwise {
        acc(v) := accReg(v) -| inputRegVec(v)
      }
    }

    switch(state) {
      is(TrackState.sSearch) {
        // store result of iteration
        when(lfsr.io.flag) {
          symbolCount.increment()
          acc := Vec.fill(3)(0)
          multiCorr := multiCorrReg +| accReg(1).abs
          when(symbolCount.willOverflow) {
            multiCorrReg := 0
            offsetCount.increment()
            when(multiCorr > maxReg) {
              maxReg := multiCorr
              offsetReg := accCount
            }
            lfsr.io.skip := True
          }
        }

        // tried all offsets
        when(offsetCount === offsetCount.maxValue) {
          state := TrackState.sLocking
        }

      }
      is(TrackState.sLocking) {
        acc := Vec.fill(3)(0)
        when((accCount === (offsetReg)) && lfsr.io.flag) {
          state := TrackState.sTracking
        } elsewhen (lfsr.io.flag) {
          lfsr.io.enable := False
        }
      }
      is(TrackState.sTracking) {
        when(lfsr.io.flag) {
          dll.io.enable := True
          io.syncd := True
          acc := Vec.fill(3)(0)
          when(accReg(1) > 0) {
            io.data := True
          } otherwise {
            io.data := False
          }
          
          lfsr.io.skip := dll.io.advance
          lfsr.io.enable := ~dll.io.delay
          }
        }
      }
    }
  }

case class DLL(acc_size: Int, thresh: Int)
  extends Component {
    val io = new Bundle {
      val enable = in Bool()
      val accumulator = in(Vec.fill(3)(SInt(acc_size bits)))
      val advance = out Bool()
      val delay = out Bool()
    }
    io.advance := False
    io.delay := False
    
    var error = SInt(io.accumulator(0).getWidth +1 bits)
    // var errorReg = Reg(SInt(io.accumulator(0).getWidth bits)) init(0) simPublic;

    error := 0
    // Integrate error
    // errorReg := errorReg +| error
    when(io.enable){
      // Generate error signal
      // error := (io.accumulator(0)-io.accumulator(2))/(io.accumulator(1))
      when(io.accumulator(0).abs > io.accumulator(1).abs && io.accumulator(0).abs > io.accumulator(2).abs) {
        error := (io.accumulator(0).abs - io.accumulator(1).abs).intoSInt 
      } elsewhen(io.accumulator(2).abs > io.accumulator(1).abs && io.accumulator(2).abs > io.accumulator(0).abs) {
        error := -(io.accumulator(2).abs.intoSInt - io.accumulator(1).abs.intoSInt)
      } 

      // Advance or delay code based on error signal
      when(error > 5) {
        io.delay := True
        // errorReg := 0
      } elsewhen(error < -5) {
        io.advance := True
        // errorReg := 0
      }
    }
  }
