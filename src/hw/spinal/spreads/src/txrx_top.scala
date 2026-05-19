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

  val tx = Transmitter(chips, taps)
  val rx = Receiver(chips, taps)

  tx.io.enable  := io.txEnable
  tx.io.data    := io.txData
  
  rx.io.enable  := io.rxEnable
  rx.io.rxValid := io.rxValid
  
  rx.io.rxChip  := tx.io.pnBit

  io.decoded := rx.io.decodedBit
  io.valid   := rx.io.bitValid
}
