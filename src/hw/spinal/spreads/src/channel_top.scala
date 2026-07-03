package spreads

import spinal.core._

case class channel_top() extends Component {

  ClockDomain.current.clock.setName("CLOCK_50")
  ClockDomain.current.reset.setName("reset")

  val io = new Bundle {
    val SW   = in Bits(10 bits) setName("SW")
    val LEDR = out Bits(10 bits) setName("LEDR")
    val LEDG = out Bits(8 bits) setName("LEDG")
  }

  val channel = Channel()
  
  channel.io.attenutation := U(io.SW(9) ## io.SW(8))
  channel.io.noise := U(io.SW(7) ## io.SW(6) )

  channel.io.enable := io.SW(0)
  channel.io.i.assignFromBits(B(0, 5 bits) ## io.SW(9 downto 1))

  val outBits = channel.io.o.asBits
  
  io.LEDR := outBits(9 downto 0)
  io.LEDG := B"0000" ## outBits(13 downto 10)
}
