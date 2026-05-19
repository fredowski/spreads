import spinal.core._
import spinal.lib._

case class Receiver(chips: Int, taps: Seq[Int]) extends Component {
  val io = new Bundle {
    val enable     = in Bool()
    val rxChip    = in  Bool()
    val rxValid    = in  Bool()
    val decodedBit = out Bool()
    val bitValid   = out Bool()
  }

  
  val lfsrReg = Reg(Bits(chips bits)) init(1)
  val buffer  = Reg(Bits(chips bits)) init(0)

  // LFSR runs independent of buffer
  var feedback = False
  for (tap <- taps) { feedback = feedback ^ lfsrReg(tap - 1) }
  when(io.enable) {
    for (i <- chips - 1 downto 1) { lfsrReg(i) := lfsrReg(i - 1) }
    lfsrReg(0) := feedback
  }

  // put input chips into buffer
  when(io.rxValid) {
    for (i <- chips - 1 downto 1) { 
      buffer(i) := buffer(i - 1) 
    }
    buffer(0) := io.rxChip
  }

  val agreements = Bits(chips bits)
  for (i <- 0 until chips) {
    agreements(i) := (buffer(i) ^ lfsrReg(i))  // xor all INDIVIDUAL chips
  }

  val score      = CountOne(agreements)

  val isOne  = score > (chips / 2)
  val isZero = score < (chips / 2)

  io.decodedBit := isOne
  io.bitValid   := io.rxValid && (isOne || isZero)
}
