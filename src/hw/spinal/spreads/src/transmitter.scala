import spinal.core._

case class Transmitter(length: Int, taps: Seq[Int]) extends Component {
  val io = new Bundle {
    val enable = in Bool()
    val data   = in Bool()
    val pnBit  = out Bool()
  }
  val shiftReg = Reg(Bits(length bits)) init(1) // can't have fuill 0 lsfr

  var feedback = False
  for (tap <- taps) {
    feedback = feedback ^ shiftReg(tap - 1)
  }

  when(io.enable) {
    for (i <- length - 1 downto 1) {
      shiftReg(i) := shiftReg(i - 1)
    }
    shiftReg(0) := feedback
  }
  
  io.pnBit := feedback ^ io.data 
}
