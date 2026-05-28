package spreads.src

import spinal.core._
import spinal.lib._
import spreads.src.UnrollLFSR

case class Receiver_Analog(poly: Array[Int], m_lfsr: Int, n_adc: Int) extends Component {
  val io = new Bundle {
    val enable = in Bool()
    val signal   = in SInt(n_adc bits)
    val data  = out Bool()
  }

  val lfsr = UnrollLFSR(poly.toArray, poly.max+1, 1, 1)
  lfsr.io.skip := False

  var acc = SInt((n_adc + m_lfsr) bits)
  var accReg = RegNextWhen(acc, io.enable)

  var maxReg = Reg(UInt((n_adc + m_lfsr) bits))
  var offsetReg = UInt(m_lfsr bits)

  when (lfsr.io.rnd_o === 0) {
    acc = accReg - io.signal
  } otherwise {
    acc = accReg + io.signal
  }

  var accCount = Counter(m_lfsr bits)
  var offsetCount = Counter(m_lfsr bits)

  // store result of iteration
  when (accCount.value.andR) {
    when (acc.abs > maxReg) {
      maxReg := acc.abs
      offsetReg := offsetCount
    }
    lfsr.io.skip := True
  }

  // reset accumulator
  when (accCount === 0) {
    accReg := 0
    offsetCount.increment()
  }

  // tried all offsets
  when (offsetCount.value.andR) {

  }


  when (io.enable) {
    accCount.increment()
  }
}

