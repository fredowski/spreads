package spreads

import spinal.core._

case class tx_top(poly: List[Int]) extends Component {
  

  ClockDomain.current.clock.setName("CLOCK_50")
  ClockDomain.current.reset.setName("reset")

  val io = new Bundle {

    val SW   = in Bits(10 bits) setName("SW")
    val LEDR = out Bits(10 bits) setName("LEDR")
    val LEDG = out Bits(8 bits) setName("LEDG")
  }


  val tx = Transmitter_Analog(poly)

  tx.io.enable := io.SW(0)
  tx.io.data   := io.SW(1)

  val codedBits = tx.io.coded.asBits
  
  io.LEDR := codedBits(9 downto 0)
  io.LEDG := B"0000" ## codedBits(13 downto 10)
}
