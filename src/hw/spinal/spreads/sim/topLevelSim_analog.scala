import spinal.core._
import spinal.core.sim._

object TopLevelSimAnalog extends App {
  val poly = List(9,2)
  val offset = 154
  val CHIPS = math.pow(2,10).toInt -1
  val txBits = Seq(true, false, true, true, false, false, true, false, true, true, false, true)

  SimConfig.withVerilator.withVcdWave.workspacePath("sim_output").compile(new SpreadSpectrumTopAnalog(poly)).doSim { dut =>
    val period = 10
    dut.clockDomain.forkStimulus(period = period)
    // disableSimWave()
    dut.io.txEnable #= false; dut.io.txData #= false
    dut.io.rxEnable #= false;
    dut.clockDomain.waitSampling()
    dut.io.txEnable #= true;
    dut.clockDomain.waitSampling(offset)
    // begin the lsfrs
    dut.io.rxEnable #= true
    // dut.clockDomain.waitSampling(CHIPS*CHIPS*3)
    var timeout = dut.clockDomain.waitSamplingWhere(CHIPS*CHIPS*11*period)(dut.io.syncd.toBoolean)
    assert(timeout == false, "No synchronization acquired!")
    dut.clockDomain.waitSampling(100)
    // enableSimWave()
    // Begin sending bit sequence
    var results = new Array[Boolean](txBits.length)
    for ((b,i) <- txBits.view.zipWithIndex) {
      dut.io.txData #= b
      // dut.clockDomain.waitSampling(CHIPS)
      dut.clockDomain.waitRisingEdgeWhere(dut.io.syncd.toBoolean)
      results(i) = dut.io.decoded.toBoolean
    }

    dut.clockDomain.waitRisingEdgeWhere(dut.io.syncd.toBoolean)
    val fullResults = results.appended(dut.io.decoded.toBoolean).drop(1).zip(txBits)
    dut.io.txEnable #= false; dut.io.rxEnable #= false
    dut.clockDomain.waitSampling(5)

    val passed = fullResults.count { case (rx, tx) => rx == tx }
    println(s"\n=== $passed / ${txBits.length} bits recovered ===")
    println("Expected: " + fullResults.map { case (_, tx) => if (tx) " " else "▄" }.mkString)
    println("Decoded:  " + fullResults.map { case (rx, _) => if (rx) " " else "▄" }.mkString)
    println("Match:    " + fullResults.map { case (rx, tx) => if (rx == tx) "·" else "✗" }.mkString)
  }
}
