import spinal.core._
import spinal.lib._
import spreads.src.UnrollLFSR

case class Transmitter_Analog(poly: List[Int]) extends Component {
  val io = new Bundle {
    val enable = in Bool()
    val data   = in Bool()
    val coded  = out SInt(14 bits)
  }

  val lfsr = UnrollLFSR(poly.toArray, poly.max+1, 1, 1)
  lfsr.io.enable := io.enable
  lfsr.io.skip := False

  val latchedData = RegNextWhen(io.data, lfsr.io.flag)

  val chip = Bool()
  chip := (lfsr.io.rnd_o.asBits(0) ^ latchedData)
  when(chip) {
    io.coded := io.coded.maxValue
  } otherwise {
    io.coded := io.coded.minValue
  }
}
