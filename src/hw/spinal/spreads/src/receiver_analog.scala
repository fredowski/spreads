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

  io.syncd := False
  val lfsr = UnrollLFSR(poly.toArray, poly.max + 1, 1, 1)
  lfsr.io.skip := False
  lfsr.io.enable := io.enable

  // var data = False
  // val dataReg = RegNextWhen(data, io.syncd)
  io.data := False
  // three parallel correlators
  var acc = SInt((n_adc + m_lfsr) bits)
  var accReg = RegNextWhen(acc, io.enable) init (0)
  acc := 0


  var maxReg = Reg(UInt((n_adc + m_lfsr + n_integrator) bits)) init (0) simPublic
  var offsetReg = Reg(UInt(m_lfsr bits)) init(0) simPublic

  var accCount = Counter(1 to chips) init(1)
  var offsetCount = Counter((m_lfsr) bits) init(0)
  var symbolCount = Counter(0 to n_integrator) init(0)

  var multiCorr = UInt((n_adc + m_lfsr + n_integrator) bits)
  var multiCorrReg = RegNext(multiCorr) init (0)
  multiCorr := multiCorrReg

    object TrackState extends SpinalEnum {
    val sSearch, sLocking, sLocked = newElement()
  }

  val state = Reg(TrackState()) init (TrackState.sSearch)
  state.setName("ReceiverState")

  when(io.enable) {
    accCount.increment()

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
          acc := 0
          when((accCount === (offsetReg)) && lfsr.io.flag) {
            state := TrackState.sLocked
          } elsewhen (lfsr.io.flag) {
            lfsr.io.enable := False
          }
        }
        is(TrackState.sLocked) {
          when(lfsr.io.flag) {
            io.syncd := True
            acc := 0
            when(accReg > 0) {
              io.data := True
            } otherwise {
              io.data := False
            }

          }
        }
      }
    }
  }



