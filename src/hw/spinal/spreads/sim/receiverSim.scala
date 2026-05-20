import spinal.core._
import spinal.core.sim._

object SpreadSpectrumSim extends App {
  val CHIPS  = 8
  val TAPS   = Seq(1, 2, 3, 4)
  val txBits = Seq(true, false, true, true, false, false, true, false, true, true, false, true)

  SimConfig.withVcdWave.workspacePath("sim_output").compile(new SpreadSpectrumTop(CHIPS, TAPS)).doSim { dut =>
    dut.clockDomain.forkStimulus(period = 10)

    dut.io.txEnable #= false; dut.io.txData #= false
    dut.io.rxEnable #= false; dut.io.rxValid #= false
    dut.clockDomain.waitSampling()

    // begin the lsfrs
    dut.io.txEnable #= true; dut.io.rxEnable #= true
    dut.clockDomain.waitSampling(10)
    dut.io.rxValid #= true

    // Begin sending bit sequence
    val results = for ((bit, idx) <- txBits.zipWithIndex) yield {
      dut.io.txData #= bit
      dut.clockDomain.waitSampling(CHIPS)

      val decoded = dut.io.decoded.toBoolean
      val valid   = dut.io.valid.toBoolean
      val ok      = valid && decoded == bit
      println(f"Bit[$idx%2d]  expected=$bit%-5s  decoded=$decoded%-5s  ${if (ok) "PASS" else "FAIL"}")
      (bit, decoded, valid)
    }

    dut.io.rxValid #= false; dut.io.txEnable #= false; dut.io.rxEnable #= false
    dut.clockDomain.waitSampling(5)

    val passed = results.count { case (exp, dec, v) => v && dec == exp }
    println(s"\n=== $passed / ${txBits.length} bits recovered ===")
    println("Expected: " + results.map { case (e, _, _) => if (e) " " else "▄" }.mkString)
    println("Decoded:  " + results.map { case (_, d, _) => if (d) " " else "▄" }.mkString)
    println("Match:    " + results.map { case (e, d, v) => if (v && d == e) "·" else "✗" }.mkString)
  }
}
