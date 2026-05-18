import spinal.core._

case class Transmitter(length: Int, taps: Seq[Int]) extends Component {
  val io = new Bundle {
    val enable = in Bool()
    val pnBit  = out Bool()
  }

  val shiftReg = Vec.fill(length)(RegInit(False))

  shiftReg(0).init(True) // Reg 0 is '1' to avoid full zero LFSR


  var feedback = False
  for (tap <- taps) {
    val tapIndex = tap - 1
    feedback = feedback ^ shiftReg(tapIndex)
  }

  when(io.enable) {
    for (i <- 1 until length) {
      shiftReg(i) := shiftReg(i - 1)
    }
    shiftReg(0) := feedback
  }

    //  This is our pseudo random result chip
  io.pnBit := shiftReg(length - 1)
}
