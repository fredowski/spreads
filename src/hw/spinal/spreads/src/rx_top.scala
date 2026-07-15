package spreads

import spinal.core._


case class rx_top(poly: List[Int], symbols_to_integrate: Int) extends Component {
  
  ClockDomain.current.clock.setName("CLOCK_50")
  ClockDomain.current.reset.setName("reset")
  
  val io = new Bundle {
    val clk       = in Bool () setName("CLOCK_50")
    val SW        = in Bits (10 bits) setName ("SW")
    val LEDR      = out Bits (1 bits) setName ("LEDR") 
    val LEDG      = out Bits (2 bits) setName ("LEDG")
    val DAC_MODE  = out Bool () setName ("DAC_MODE")  // 1=dual port, 0=interleaved
    val DAC_WRT_A = out Bool () setName ("DAC_WRT_A")
    val DAC_WRT_B = out Bool () setName ("DAC_WRT_B")
    val DAC_CLK_A = out Bool () setName ("DAC_CLK_A")
    val DAC_CLK_B = out Bool () setName ("DAC_CLK_B") // PLL_OUT_DAC1 in User Manual
    val ADC_DA    = in Bits (14 bits) setName ("ADC_DA")
    val ADC_DB    = in Bits (14 bits) setName ("ADC_DB")
    val POWER_ON  = out Bool () setName ("POWER_ON")
    val KEY0      = in Bool () setName ("KEY0")
    val ADC_CLK_A = out Bool () setName ("ADC_CLK_A")
    val ADC_CLK_B = out Bool () setName ("ADC_CLK_B")
    val ADC_OEB_A = out Bool () setName ("ADC_OEB_A")
    val ADC_OEB_B = out Bool () setName ("ADC_OEB_B")
    val ADC_OTR_A = in Bool () setName ("ADC_OTR_A")
    val ADC_OTR_B = in Bool () setName ("ADC_OTR_B")
    // val DAC_DA = out Bits (14 bits) setName ("DAC_DA")
  }

  val key0ResetClockDomain = ClockDomain(
    clock  = io.clk,
    reset  = io.KEY0,
    config = ClockDomainConfig(
      clockEdge        = RISING,
      resetKind        = ASYNC,
      resetActiveLevel = LOW
    )
  )

  val clockArea = new ClockingArea(key0ResetClockDomain) {  
    val rx = Receiver_Analog(poly.toArray, 10, 14, symbols_to_integrate)
    val adc_reg = RegNext(io.ADC_DA)
    val decodedBits = RegNext(adc_reg.asUInt(13) ## adc_reg.asUInt(12 downto 0)) init(0) 
    rx.io.enable := io.SW(0)
    rx.io.signal := decodedBits.asSInt
    val dataReg = RegNextWhen(rx.io.data, rx.io.syncd) init(False)
  }
  
  io.DAC_CLK_A := io.clk
  io.DAC_CLK_B := io.clk
  io.DAC_WRT_A := io.clk
  io.DAC_WRT_B := io.clk

  io.DAC_MODE  := True
  io.POWER_ON  := True
  io.ADC_CLK_A := io.clk
  io.ADC_CLK_B := io.clk
  io.ADC_OEB_A := False
  io.ADC_OEB_B := False

  // io.DAC_DA := (default -> false)
  // io.DAC_DA(4) := clockArea.dataReg
  io.LEDG(1) := clockArea.dataReg  
  io.LEDG(0) := clockArea.rx.io.syncd

  io.LEDR(0) := io.SW(0)
}
