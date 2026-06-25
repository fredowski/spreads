package spreads

import spinal.core._

case class tx_top(poly: List[Int]) extends Component {
  val io = new Bundle {
    val clk       = in Bool () setName("CLOCK_50")
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
  
  val negEdgeClockDomain = ClockDomain(
    clock  = io.clk,
    reset  = io.KEY0,
    config = ClockDomainConfig(
      clockEdge        = FALLING,
      resetKind        = ASYNC,
      resetActiveLevel = LOW
    )
  )

  val clockDomainWithKey0Reset = ClockDomain(
    clock  = io.clk,
    reset  = io.KEY0,
    config = ClockDomainConfig(
      clockEdge        = RISING,
      resetKind        = ASYNC,
      resetActiveLevel = LOW
    )
  )
  val clockingArea = new ClockingArea(clockDomainWithKey0Reset) {

    val tx = Transmitter_Analog(poly)

    tx.io.enable := io.SW(0)
    tx.io.data   := io.SW(1)
    //Convert SINT to UINT
    val codedBits = ~tx.io.coded.asBits(13) ## tx.io.coded.asBits(12 downto 0)
  }
  
  val negEdgeArea = new ClockingArea(negEdgeClockDomain) {    
    val buff0 = RegNext(clockingArea.codedBits) init(0) addTag(crossClockDomain) 
    val buff1 = RegNext(buff0) init(0) addTag(crossClockDomain)
  }

  io.DAC_CLK_A := io.clk
  io.DAC_CLK_B := io.clk
  io.DAC_WRT_A := io.clk
  io.DAC_WRT_B := io.clk

  io.DAC_MODE  := True
  io.POWER_ON  := True
  io.ADC_CLK_A := False
  io.ADC_CLK_B := False
  io.ADC_OEB_A := True
  io.ADC_OEB_B := True

  io.LEDR := negEdgeArea.buff1(9 downto 0)
  io.LEDG := B"0000" ## negEdgeArea.buff1(13 downto 10)
  io.DAC_DA := negEdgeArea.buff1
  io.DAC_DB := negEdgeArea.buff1  
}
