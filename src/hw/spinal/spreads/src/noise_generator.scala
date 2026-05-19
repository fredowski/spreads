import spinal.core._
case class NoiseGenerator() extends Component {
  val io = new Bundle {
    val enable       = in Bool()
    val noisePercent = in UInt(7 bits) // 0 to 100% noise, in uint
    val originalChip = in Bool()
    val noisedChip   = out Bool()
  }

  val counter = Reg(UInt(7 bits)) init(0)
  when(io.enable) {
    counter := counter + 1
    when(counter >= 99) { counter := 0 }
  }

  io.noisedChip := io.originalChip ^ (io.enable && counter < io.noisePercent)
}
