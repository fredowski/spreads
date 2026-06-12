package spreads

import spinal.core._

case class rx_top(poly: List[Int], symbols_to_integrate: Int) extends Component {

  ClockDomain.current.clock.setName("CLOCK_50")
  ClockDomain.current.reset.setName("reset")

  val io = new Bundle {
    val SW   = in Bits(10 bits) setName("SW")
    val LEDR = out Bits(10 bits) setName("LEDR")
    val LEDG = out Bits(8 bits) setName("LEDG")
  }

  val rx = Receiver_Analog(poly.toArray, 10, 14, symbols_to_integrate)

  rx.io.enable := io.SW(0)
  rx.io.signal.assignFromBits(B(0, 5 bits) ## io.SW(9 downto 1))

  io.LEDR := B(0, 8 bits) ## rx.io.syncd ## rx.io.data
  io.LEDG := B(0, 8 bits)
}
