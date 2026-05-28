package spreads.sim


import spinal.core._
import spinal.core.sim._
import spreads.src.UnrollLFSR

//x^10 + x^4 + x^3 + x^1 + 1
//x^32 + x^22 + x^2 + x^1 + 1
//x^16 + x^15 + x^11 + x^10 + x^9 + x^8 + x^6 + x^4 + x^2 + x^1 + 1
object lfsrSim extends App {
  val poly = List(15,14,10,9,8,7,5,3,1,0)
  SimConfig.withGhdl.withVcdWave.workspacePath("sim_output").compile(UnrollLFSR(poly.toArray,poly.max+1, 2, 14)).doSim { dut =>
    // Fork a process to generate the reset and the clock on the dut
    dut.clockDomain.forkStimulus(period = 10)

    // var modelState = 0
    dut.io.enable #= true
    dut.clockDomain.waitRisingEdge()
    for (idx <- 0 to scala.math.pow(2,poly.max+1).toInt-3) {
      // Drive the dut inputs with random values
      // dut.io.cond0.randomize()
      // dut.io.cond1.randomize()

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()
      assert(dut.io.flag.toBoolean == false)

      // Check that the dut values match with the reference model ones
      // val modelFlag = modelState == 0 || dut.io.cond1.toBoolean
      // assert(dut.io.state.toInt == modelState)
      // assert(dut.io.flag.toBoolean == modelFlag)
      // if (dut.io.flag)
      // Update the reference model value
      // if (dut.io.cond0.toBoolean) {
      //  modelState = (modelState + 1) & 0xff
      // }
    }
    dut.clockDomain.waitRisingEdge()
    assert(dut.io.flag.toBoolean == true)
  }
}
