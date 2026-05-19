import spinal.core._

case class Receiver(chips: Int, taps: Seq[Int]) extends Component {
  val io = new Bundle {
    val enable     = in Bool()
    val rxChip    = in  Bool()
    val rxValid    = in  Bool()
    val decodedBit = out Bool()
    val bitValid   = out Bool()
  }

  
  val lfsrReg = Vec.fill(chips)(RegInit(False))
  lfsrReg(0).init(True)
  val buffer  = Vec.fill(chips)(RegInit(False))

  // LFSr
  var feedback = False
  for (tap <- taps) { feedback = feedback ^ lfsrReg(tap - 1) }
  when(io.enable) {
    for (i <- chips - 1 downto 1) { lfsrReg(i) := lfsrReg(i - 1) }
    lfsrReg(0) := feedback
  }

  // buffer(chips-1) is NEWEST 
  when(io.rxValid) {
    for (i <- chips - 1 downto 1) { 
      buffer(i) := buffer(i - 1) 
    }
    buffer(0) := io.rxChip
  }

  val agreements = for (i <- 0 until chips) yield {
    buffer(i) === lfsrReg(i)
  }

  var score = U(0, 8 bits)
  for (a <- agreements) score = score + a.asUInt(8 bits)

  io.decodedBit := score > U(chips / 2)
  io.bitValid := io.rxValid && (score >= U(6) || score <= U(2))
}
