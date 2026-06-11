package spreads

import spinal.core._

case class channel_top(signal_attenuation_shifts: Int, noise_level: Int) extends Component {

  ClockDomain.current.clock.setName("CLOCK_50")
  ClockDomain.current.reset.setName("reset")

  val io = new Bundle {
    val SW   = in Bits(10 bits) setName("SW")
    val LEDR = out Bits(10 bits) setName("LEDR")
    val LEDG = out Bits(8 bits) setName("LEDG")
  }

  val channel = Channel(signal_attenuation_shifts, noise_level)

  channel.io.enable := io.SW(0)
  channel.io.i.assignFromBits(B(0, 5 bits) ## io.SW(9 downto 1))

  val outBits = channel.io.o.asBits
  
  io.LEDR := outBits(9 downto 0)
  io.LEDG := B"0000" ## outBits(13 downto 10)
}
