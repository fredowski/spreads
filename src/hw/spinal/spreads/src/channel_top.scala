package spreads

import spinal.core._

case class channel_top() extends Component {

  ClockDomain.current.clock.setName("CLOCK_50")
  ClockDomain.current.reset.setName("reset")

  val io = new Bundle {
    val clk       = in Bool () setName("CLOCK_50")
    val SW        = in Bits (10 bits) setName ("SW")
    val LEDR      = out Bits (10 bits) setName ("LEDR") 
    val DAC_MODE  = out Bool () setName ("DAC_MODE")  // 1=dual port, 0=interleaved
    val DAC_WRT_A = out Bool () setName ("DAC_WRT_A")
    val DAC_WRT_B = out Bool () setName ("DAC_WRT_B")
    val DAC_CLK_A = out Bool () setName ("DAC_CLK_A")
    val DAC_CLK_B = out Bool () setName ("DAC_CLK_B") // PLL_OUT_DAC1 in User Manual
    val ADC_DA    = in Bits (14 bits) setName ("ADC_DA")
    val ADC_DB    = in Bits (14 bits) setName ("ADC_DB")
    val DAC_DA    = out Bits (14 bits) setName ("DAC_DA")
    val DAC_DB    = out Bits (14 bits) setName ("DAC_DB")    
    val POWER_ON  = out Bool () setName ("POWER_ON")
    val KEY0      = in Bool () setName ("KEY0")
    val ADC_CLK_A = out Bool () setName ("ADC_CLK_A")
    val ADC_CLK_B = out Bool () setName ("ADC_CLK_B")
    val ADC_OEB_A = out Bool () setName ("ADC_OEB_A")
    val ADC_OEB_B = out Bool () setName ("ADC_OEB_B")
    val ADC_OTR_A = in Bool () setName ("ADC_OTR_A")
    val ADC_OTR_B = in Bool () setName ("ADC_OTR_B")
  }

  io.LEDR := (default -> false)

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
    val channel = Channel()
    val decodedBits = ~io.ADC_DA.asUInt(13) ## io.ADC_DA.asUInt(12 downto 0)


    io.LEDR(6) := io.SW(6)
    io.LEDR(7) := io.SW(7)
    io.LEDR(8) := io.SW(8)
    io.LEDR(9) := io.SW(9)
    channel.io.attenuation := U(io.SW(9) ## io.SW(8))
    channel.io.noise := U(~io.SW(7) ## ~io.SW(6) )

    channel.io.enable := io.SW(0)
    channel.io.i := S(decodedBits);
    
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
  io.DAC_DA := ~clockArea.channel.io.o.asBits(13) ## clockArea.channel.io.o.asBits(12 downto 0)
  io.DAC_DB := ~clockArea.channel.io.o.asBits(13) ## clockArea.channel.io.o.asBits(12 downto 0)
  
  //io.LEDG(1) := U(clockArea.channel.io.o)

  io.LEDR(0) := io.SW(0)
}
