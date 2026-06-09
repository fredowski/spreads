package spreads

import spinal.core._

case class SpreadSpectrumTopAnalog(poly: List[Int], symbols_to_integrate: Int, signal_attenuation_shifts: Int) extends Component {
  ClockDomain.current.clock.setName("CLOCK_50")
  val io = new Bundle {
    val txEnable = in Bool ()
    val txData = in Bool ()
    val rxEnable = in Bool ()
    val decoded = out Bool ()
    val syncd = out Bool ()
  }

  val ngen = Channel(signal_attenuation_shifts, 0)
  val tx = Transmitter_Analog(poly)
  val rx = Receiver_Analog(poly.toArray, 10, 14, symbols_to_integrate)

  ngen.io.enable := io.txEnable

  tx.io.enable := io.txEnable
  tx.io.data := io.txData

  rx.io.enable := io.rxEnable

  ngen.io.i := tx.io.coded
  rx.io.signal := ngen.io.o

  io.decoded := rx.io.data
  io.syncd := rx.io.syncd
}


object genverilog extends App {
  val target = sys.props.getOrElse("spinalTargetDir", "sim_output")

  SpinalConfig(
    targetDirectory = target
  ).generateVerilog(SpreadSpectrumTopAnalog(List(9, 2), 0, 3))
}

object genvhdl extends App {
  val target = sys.props.getOrElse("spinalTargetDir", "sim_output")

  SpinalConfig(
    targetDirectory = target
  ).generateVhdl(SpreadSpectrumTopAnalog(List(9, 2), 0, 3))
}
