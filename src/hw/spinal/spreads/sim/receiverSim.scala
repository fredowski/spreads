import spinal.core._
import spinal.core.sim._

object ReceiverSim extends App {

  val CHIPS = 8
  val CODE  = Seq(1, -1, 1, 1, -1, -1, 1, -1)

  SimConfig.withVcdWave.workspacePath("sim_output").compile(Receiver(CHIPS, CODE)).doSim { dut =>

    dut.clockDomain.forkStimulus(period = 10)
    dut.io.rxChip  #= false
    dut.io.rxValid #= false

    for (c <- CODE) {
      dut.io.rxChip  #= (c == 1)
      dut.io.rxValid #= true
      dut.clockDomain.waitSampling()
    }

    dut.io.rxValid #= false
    dut.clockDomain.waitSampling(CHIPS * 2)
  }
}
