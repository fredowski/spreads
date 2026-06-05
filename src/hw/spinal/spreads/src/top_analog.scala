import spinal.core._

case class SpreadSpectrumTopAnalog(poly: List[Int]) extends Component {
  ClockDomain.current.clock.setName("CLOCK_50")
  val io = new Bundle {
    val txEnable = in Bool ()
    val txData = in Bool ()
    val rxEnable = in Bool ()
    val decoded = out Bool ()
    val syncd = out Bool ()
  }

  val ngen = Channel(8, 5)
  val tx = Transmitter_Analog(poly)
  val rx = Receiver_Analog(poly.toArray, 10, 14)

  ngen.io.enable := io.txEnable

  tx.io.enable := io.txEnable
  tx.io.data := io.txData

  rx.io.enable := io.rxEnable

  ngen.io.i := tx.io.coded
  rx.io.signal := ngen.io.o

  io.decoded := rx.io.data
  io.syncd := rx.io.syncd
}
