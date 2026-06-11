package spreads

import spinal.core._

case class SpreadSpectrumTopAnalog(poly: List[Int], symbols_to_integrate: Int, signal_attenuation_shifts: Int) extends Component {
  ClockDomain.current.clock.setName("CLOCK_50")
  val io = new Bundle {
    val txEnable = in Bool () setName("SW[0]")
    val txData = in Bool () setName("SW[1]")
    val coded = out SInt (14 bits)
   }

  val tx = Transmitter_Analog(poly)

  tx.io.enable := io.txEnable
  tx.io.data := io.txData
  io.coded := tx.io.coded
}
