import spinal.core._

case class SpreadSpectrumTop(chips: Int, taps: Seq[Int]) extends Component {
  val io = new Bundle {
    val txEnable = in Bool()
    val txData   = in Bool()
    val rxEnable = in Bool()
    val rxValid  = in Bool()
    val decoded  = out Bool()
    val valid    = out Bool()
  }

  val ngen = NoiseGenerator()
  val tx = Transmitter(chips, taps)
  val rx = Receiver(chips, taps)

  ngen.io.enable:= io.txEnable
  ngen.io.noisePercent := U(10)
  
  tx.io.enable  := io.txEnable
  tx.io.data    := io.txData
  
  rx.io.enable  := io.rxEnable
  rx.io.rxValid := io.rxValid

  ngen.io.originalChip := tx.io.pnBit
  rx.io.rxChip := ngen.io.noisedChip
  

  io.decoded := rx.io.decodedBit
  io.valid   := rx.io.bitValid
}
