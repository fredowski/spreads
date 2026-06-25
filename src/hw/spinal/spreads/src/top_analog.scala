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
  
  val rx = Receiver_Analog(poly.toArray, 10, 14, symbols_to_integrate)

  val txClockDomain = ClockDomain.external("clk_tx")
  val txArea = new ClockingArea(txClockDomain) {
    val tx = Transmitter_Analog(poly)
    tx.io.enable := io.txEnable addTag(crossClockDomain)
    tx.io.data := io.txData addTag(crossClockDomain)
  }

  ngen.io.enable := io.txEnable

  rx.io.enable := io.rxEnable

  ngen.io.i := txArea.tx.io.coded addTag(crossClockDomain)
  rx.io.signal := ngen.io.o

  io.decoded := rx.io.data
  io.syncd := rx.io.syncd
}


object genverilog extends App {
  val target_name = args.headOption.getOrElse("top")
  val target = sys.props.getOrElse("spinalTargetDir", "sim_output")
  if (target_name == "top" || target_name == "SpreadSpectrumTopAnalog"){
    SpinalConfig(
      targetDirectory = target
    ).generateVerilog(SpreadSpectrumTopAnalog(List(9, 2), 0, 3))
  } else if (target_name == "tx_top") {
    SpinalConfig(
      targetDirectory = target
    ).generateVerilog(tx_top(List(9, 2)))    
  } else if (target_name == "rx_top") {
    SpinalConfig(
      targetDirectory = target
    ).generateVerilog(rx_top(List(9, 2), 0))
  } else if (target_name == "channel") {
    SpinalConfig(
      targetDirectory = target
    ).generateVerilog(channel_top(4,0))

  // } else if (target_name == "de1_adc") {
  //   SpinalConfig(
  //     targetDirectory = target
  //   ).generateVerilog(de1_adc())
  }
}

object genvhdl extends App {
  val target = sys.props.getOrElse("spinalTargetDir", "sim_output")

  SpinalConfig(
    targetDirectory = target
  ).generateVhdl(SpreadSpectrumTopAnalog(List(9, 2), 0, 3))
}
