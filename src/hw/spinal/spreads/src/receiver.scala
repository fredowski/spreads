import spinal.core._

case class Receiver(chips: Int, code: Seq[Int]) extends Component {
  val io = new Bundle {
    val rxChip     = in  Bool()
    val rxValid    = in  Bool()
    val decodedBit = out Bool()
    val bitValid   = out Bool()
  }

  val shiftReg = Vec.fill(chips)(Reg(Bool()) init(False))

  when(io.rxValid) {
    shiftReg(0) := io.rxChip
    for (i <- 1 until chips)
      shiftReg(i) := shiftReg(i - 1)
  }

  val agreements = for (i <- code.indices) yield
    if (code(i) == 1) shiftReg(i) else !shiftReg(i)

  var score = U(0, 8 bits)
  for (a <- agreements) score = score + a.asUInt(8 bits)

  io.decodedBit := score > U(chips / 2)
  io.bitValid   := io.rxValid && (score > U(chips * 3 / 4) || score < U(chips / 4))
}
 
