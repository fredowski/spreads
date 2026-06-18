package spreads

import spinal.core._

case class tx_top(poly: List[Int]) extends Component {

  ClockDomain.current.clock.setName("CLOCK_50")

  val io = new Bundle {

    val SW        = in Bits (10 bits) setName ("SW")
    val LEDR      = out Bits (10 bits) setName ("LEDR")
    val LEDG      = out Bits (8 bits) setName ("LEDG")
    val DAC_MODE  = out Bool () setName ("DAC_MODE")  // 1=dual port, 0=interleaved
    val DAC_WRT_A = out Bool () setName ("DAC_WRT_A")
    val DAC_WRT_B = out Bool () setName ("DAC_WRT_B")
    val DAC_CLK_A = out Bool () setName ("DAC_CLK_A")
    val DAC_CLK_B = out Bool () setName ("DAC_CLK_B") // PLL_OUT_DAC1 in User Manual
    val DAC_DA    = out Bits (14 bits) setName ("DAC_DA")
    val DAC_DB    = out Bits (14 bits) setName ("DAC_DB")
    val POWER_ON  = out Bool () setName ("POWER_ON")
    val KEY0      = in Bool () setName ("KEY0")
    val ADC_CLK_A = out Bool () setName ("ADC_CLK_A")
    val ADC_CLK_B = out Bool () setName ("ADC_CLK_B")
    val ADC_OEB_A = out Bool () setName ("ADC_OEB_A")
    val ADC_OEB_B = out Bool () setName ("ADC_OEB_B")
}
  //ClockDomain.current.reset := !io.KEY0


  val myClockDomain = ClockDomain(
    clock  = ClockDomain.current.clock,
    reset  = io.KEY0,
    config = ClockDomainConfig(
      clockEdge        = RISING,
      resetKind        = ASYNC,
      resetActiveLevel = LOW
    )
  )
  val myArea = new ClockingArea(myClockDomain) {

    val coded_reg = Reg(Bits(1 bits)) init (0) 
    val tx = Transmitter_Analog(poly)

    tx.io.enable := io.SW(0)
    tx.io.data   := io.SW(1)
    val codedBits = tx.io.coded.asBits

    io.DAC_MODE  := True
    io.DAC_CLK_A := ClockDomain.current.readClockWire
    io.DAC_CLK_B := ClockDomain.current.readClockWire
    io.DAC_WRT_A := ClockDomain.current.readClockWire
    io.DAC_WRT_B := ClockDomain.current.readClockWire
    io.POWER_ON  := True
    io.ADC_CLK_A := False
    io.ADC_CLK_B := False
    io.ADC_OEB_A := True
    io.ADC_OEB_B := True
    
    io.LEDR := codedBits(9 downto 0)
    io.LEDG := B"0000" ## codedBits(13 downto 10)

    io.DAC_DA := codedBits
    io.DAC_DB := codedBits
  }
}
