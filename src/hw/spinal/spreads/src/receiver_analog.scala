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
  io.syncd := False

  val lfsr_tracking = UnrollLFSR(poly.toArray, poly.max + 1, 1, 1)
  lfsr_tracking.io.enable := True
  lfsr_tracking.io.skip := False
  lfsr_tracking.io.load := False
  lfsr_tracking.io.i_parallel := (default -> false)

  io.data := False
  // three parallel correlators
  var acc = Vec.fill(3)(SInt((n_adc + m_lfsr) bits))
  var accReg = RegNextWhen(acc, io.enable)
  // var accReg = Vec.fill(3)(Reg(SInt((n_adc + m_lfsr) bits)))
  accReg.foreach(_ init(0))
  acc := Vec.fill(3)(0)

  var inputRegVec = Vec.fill(2)(Reg(SInt (n_adc bits)))
  inputRegVec.foreach(_ init(0))

  inputRegVec(0) := io.signal
  inputRegVec(1) := inputRegVec(0)

  var dll = DLL(accReg(0).getWidth, 20)

  dll.io.early := accReg(0)
  dll.io.prompt := accReg(1)
  dll.io.late := accReg(2)
  
  // val acq = Code_Acquisition(poly, m_lfsr, n_adc, n_integrator)
  // acq.io.enable := io.enable
  // acq.io.signal := io.signal
  // lfsr_tracking.io.i_parallel := acq.io.lfsr_state
  // lfsr_tracking.io.load := acq.io.found



  val foundReg = Reg(False)

  dll.io.enable := io.enable

  val maxReg = Reg(UInt((n_adc + m_lfsr + n_integrator) bits)) init(0)
  val seekerCount = 32
  val initCounter = Counter(m_lfsr bits)
  val stepSize = 32//initCounter.maxValue / seekerCount

  val acqList = List.fill(seekerCount)(Code_Acquisition(poly, m_lfsr, n_adc, n_integrator, stepSize+1))
  for (acq <- acqList) {
    acq.io.enable := io.enable
    acq.io.signal := io.signal
    val foundReg0 = RegNext(acq.io.found)
    when(foundReg0 & (acq.io.max > maxReg)) {
      maxReg := acq.io.max
      lfsr_tracking.io.load := True
      lfsr_tracking.io.i_parallel := acq.io.lfsr_state
      acc := Vec.fill(3)(0)
      foundReg := True
    }
  }

  when(io.enable) {
    // Initialization of acquisition blocks
    when(initCounter<initCounter.maxValue) {
      initCounter.increment()
      for((acq,i) <- acqList.view.zipWithIndex)
      {
        when(initCounter < (i*stepSize)) {
          acq.io.enable := False
        }
      }
    }

    // TODO general method using scala fold or similar
      when(lfsr_tracking.io.rnd_o(0) === False) {
        acc(0) := accReg(0) +| io.signal
        acc(1) := accReg(1) +| inputRegVec(0)
        acc(2) := accReg(2) +| inputRegVec(1)
      } otherwise {
        acc(0) := accReg(0) -| io.signal
        acc(1) := accReg(1) -| inputRegVec(0)
        acc(2) := accReg(2) -| inputRegVec(1)
      }

      // when(acq.io.found) {
      //   acc := Vec.fill(3)(0)
      //   foundReg := True
      // }

      io.syncd := foundReg & lfsr_tracking.io.flag

      when(lfsr_tracking.io.flag)
      {
        when(dll.io.advance) {
          lfsr_tracking.io.enable := False
          io.data := (accReg(0) > 0)
        } elsewhen(dll.io.delay) {
          lfsr_tracking.io.skip := True
          io.data := (accReg(2) > 0)
        } otherwise {
          io.data := (accReg(1) > 0)
        }
        
        acc := Vec.fill(3)(0)
      }
    }
  }

case class Code_Acquisition(poly: Array[Int], m_lfsr: Int, n_adc: Int, n_integrator: Int, offsets: BigInt)
  extends Component {
    val io = new Bundle {
      val enable = in Bool ()
      val signal = in SInt (n_adc bits)
      val found = out Bool ()
      val lfsr_state = out Bits(m_lfsr bits)
      val max = out UInt((n_adc + m_lfsr + n_integrator) bits)
    }

    io.found := False

    val lfsr = UnrollLFSR(poly.toArray, poly.max + 1, 1, 1)
    lfsr.io.skip := False
    lfsr.io.enable := io.enable
    lfsr.io.load := False
    lfsr.io.i_parallel := (default -> false)
    
    io.lfsr_state := lfsr.io.state

    var acc = SInt((n_adc + m_lfsr) bits)
    var accReg = RegNextWhen(acc, io.enable) init(0)
    // var accReg = Vec.fill(3)(Reg(SInt((n_adc + m_lfsr) bits)))
    acc := 0

    var maxReg = Reg(io.max) init (0) 

    io.max := maxReg
    var offsetReg = Reg(UInt(m_lfsr bits)) init(0) 

    var offsetCount = Counter((offsets) bits) init(0)
    var symbolCount = Counter(0 to n_integrator) init(0)

    var multiCorr = cloneOf(io.max)
    var multiCorrReg = RegNext(multiCorr) init (0)
    multiCorr := multiCorrReg

    object TrackState extends SpinalEnum {
      val sSearch, sTracking = newElement()
    }
    val state = Reg(TrackState()) init (TrackState.sSearch)
    state := TrackState.sSearch
    state.setName("ReceiverState")

    when(io.enable) {
      // TODO general method using scala fold or similar
      when(lfsr.io.rnd_o(0) === False) {
        acc := accReg +| io.signal
      } otherwise {
        acc := accReg -| io.signal
      }

      switch(state) {
        is(TrackState.sSearch) {
          // store result of iteration
          when(lfsr.io.flag) {
            symbolCount.increment()
            acc := 0
            multiCorr := multiCorrReg +| accReg.abs
            when(symbolCount.willOverflow) {
              multiCorrReg := 0
              offsetCount.increment()
              when(multiCorr > maxReg) {
                maxReg := multiCorr
                io.found := True
              }
              lfsr.io.skip := True
            }
          }

          // tried all offsets
          // when(offsetCount === offsetCount.maxValue) {
          //   state := TrackState.sTracking
          // }
        }
        is(TrackState.sTracking) {
          
        }
      }
    }
  }

case class DLL(acc_size: Int, thresh: Int)
  extends Component {
    val io = new Bundle {
      val enable = in Bool()
      val early = in SInt(acc_size bits)
      val prompt = in SInt(acc_size bits)
      val late = in SInt(acc_size bits)
      val advance = out Bool()
      val delay = out Bool()
    }

    io.advance := False
    io.delay := False
    
    var error = SInt(acc_size+1 bits)
    // var errorReg = Reg(SInt(io.accumulator(0).getWidth bits)) init(0) simPublic;

    error := 0
    // Integrate error
    // errorReg := errorReg +| error

    var errorReg = Reg(SInt(16 bits)) init(0)

    when(io.enable){
      // Generate error signal
      // error := (io.accumulator(0)-io.accumulator(2))/(io.accumulator(1))
      when(io.early.abs > io.prompt.abs && io.early.abs > io.late.abs) {
        errorReg := errorReg +| 2
      } elsewhen(io.late.abs > io.prompt.abs && io.late.abs > io.early.abs) {
        errorReg := errorReg -| 2
      } elsewhen(errorReg >0) {
        errorReg := errorReg -1
      } elsewhen(errorReg <0) {
        errorReg := errorReg +1
      }

      // Advance or delay code based on error signal
      when(errorReg > thresh) {
        io.advance := True
        errorReg := 0
      } elsewhen(errorReg < -thresh) {
        io.delay := True
        errorReg := 0
      }
    }
  }
