package spreads

import spinal.core._
import spinal.lib._

case class Receiver_Analog(poly: Array[Int], m_lfsr: Int, n_adc: Int)
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

  // TODO general method using scala fold or similar
  when(lfsr.io.rnd_o(0) === False) {
    acc := accReg +| io.signal
  } otherwise {
    acc := accReg -| io.signal
  }

  var maxReg = Reg(UInt((n_adc + m_lfsr) bits)) init (0)
  var offsetReg = Reg(UInt(m_lfsr bits)) init (0)

  var accCount = Counter(1 to chips)
  var offsetCount = Counter((m_lfsr) bits)

  when(io.enable) {
    accCount.increment()
  }

  object TrackState extends SpinalEnum {
    val sSearch, sLocking, sLocked = newElement()
  }

  val state = Reg(TrackState()) init (TrackState.sSearch)
  state.setName("ReceiverState")
  switch(state) {
    is(TrackState.sSearch) {
      // store result of iteration
      when(lfsr.io.flag) {
        acc := 0
        offsetCount.increment()
        when(accReg.abs > maxReg) {
          maxReg := accReg.abs
          offsetReg := accCount
        }
        lfsr.io.skip := True
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
