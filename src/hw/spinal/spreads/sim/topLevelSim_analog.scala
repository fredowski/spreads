package spreads

import spinal.core._
import spinal.core.sim._

object TopLevelSimAnalog extends App {
  val poly = List(9,2)
  val symbols_to_integrate = 3
  // val offset = 456
  val CHIPS = math.pow(2,10).toInt -1
  val txBits = Seq(true, false, true, true, false, false, true, false, true, true, false, true)
  var successes = 0
  var iterations = 0
  val rng = new scala.util.Random(0)
  // val offsets = Seq.fill(100)(rng.nextInt(1023))
  val compiled = SimConfig.withVerilator.withVcdWave.allOptimisation.workspacePath("sim_output").compile(new SpreadSpectrumTopAnalog(poly, symbols_to_integrate-1))
  // 
  
  compiled.doSim { dut =>
    // SUPER IMPORTANT, else this run will produce an absolutely gigantic vcd file
    disableSimWave()
    dut.clockDomain.forkSimSpeedPrinter(1)

    val period = 10
    dut.clockDomain.forkStimulus(period = period)

    for (offset <- 1 to 1023) {
      //perform reset without spawning new thread
      dut.clockDomain.assertReset()
      dut.clockDomain.fallingEdge()
      sleep(25)
      dut.clockDomain.deassertReset()
      sleep(25)
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
      var detected_offset = (CHIPS - dut.rx.offsetReg.toInt + 1)
      println("Transmitted signal phase offset: " + offset)
      println("Signal detected at phase offset: " + detected_offset)
      println("MaxReg value: " + dut.rx.maxReg.toInt)
      if (offset == detected_offset) successes+=1
      // assert(offset == detected_offset, "Code acquisition not successful")
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

      if (offset == detected_offset) {
        val passed = fullResults.count { case (rx, tx) => rx == tx }
        println(s"\n=== $passed / ${txBits.length} bits recovered ===")
        println("Expected: " + fullResults.map { case (_, tx) => if (tx) " " else "▄" }.mkString)
        println("Decoded:  " + fullResults.map { case (rx, _) => if (rx) " " else "▄" }.mkString)
        println("Match:    " + fullResults.map { case (rx, tx) => if (rx == tx) "·" else "✗" }.mkString)
      }
      iterations+=1
      println("Code acquisition rate: " + successes.toDouble/iterations)
    }
    
  }
}
