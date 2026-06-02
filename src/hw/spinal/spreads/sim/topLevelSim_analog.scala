import spinal.core._
import spinal.core.sim._

object TopLevelSimAnalog extends App {
  val poly = List(9,2)
  val CHIPS = math.pow(2,10).toInt -1
  val txBits = Seq(true, false, true, true, false, false, true, false, true, true, false, true)

  SimConfig.withGhdl.withVcdWave.workspacePath("sim_output").compile(new SpreadSpectrumTopAnalog(poly)).doSim { dut =>
    dut.clockDomain.forkStimulus(period = 10)

    dut.io.txEnable #= false; dut.io.txData #= false
    dut.io.rxEnable #= false;
    dut.clockDomain.waitSampling()

    // begin the lsfrs
    dut.io.txEnable #= true; dut.io.rxEnable #= true
    dut.clockDomain.waitSampling(CHIPS*CHIPS*3)

    // Begin sending bit sequence
    val results = for ((bit, idx) <- txBits.zipWithIndex) yield {
      dut.io.txData #= bit
      dut.clockDomain.waitSampling(CHIPS)

      val decoded = dut.io.decoded.toBoolean
      val valid   = dut.io.syncd.toBoolean
      val ok      = valid && decoded == bit
      println(f"Bit[$idx%2d]  expected=$bit%-5s  decoded=$decoded%-5s  ${if (ok) "PASS" else "FAIL"}")
      (bit, decoded, valid)
    }

    dut.io.txEnable #= false; dut.io.rxEnable #= false
    dut.clockDomain.waitSampling(5)

    val passed = results.count { case (exp, dec, v) => v && dec == exp }
    println(s"\n=== $passed / ${txBits.length} bits recovered ===")
    println("Expected: " + results.map { case (e, _, _) => if (e) " " else "▄" }.mkString)
    println("Decoded:  " + results.map { case (_, d, _) => if (d) " " else "▄" }.mkString)
    println("Match:    " + results.map { case (e, d, v) => if (v && d == e) "·" else "✗" }.mkString)
  }
}
