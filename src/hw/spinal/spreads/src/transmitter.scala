import spinal.core._

case class Transmitter(length: Int, taps: Seq[Int]) extends Component {
  val io = new Bundle {
    val enable = in Bool()
    val pnBit  = out Bool()
  }

  val shiftReg = Vec.fill(length)(RegInit(False))

  shiftReg(0).init(True) // Reg 0 is '1' to avoid full zero LFSR

}
