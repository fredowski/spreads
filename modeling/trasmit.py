package projectname

import spinal.core._
import spinal.core.sim._

class Transmitter extends Component {
  val io = new Bundle {
    val dataIn  = in  Bool()  // one data bit at a time
    val chipOut = out Bool()  // spread output chip
    val prnsOut = out Bool()  // PRNS for debug
  }

  // instantiate our LFSR
  val lfsr = new LFSR(8)
  lfsr.io.enable := True

  // spread: dataIn XNOR prnsOut = multiply in +1/-1 world
  io.prnsOut := lfsr.io.output
  io.chipOut := (io.dataIn ^ lfsr.io.output) ^ True
}

object TransmitterSim extends App {
  SimConfig.withWave.compile(new Transmitter).doSim { dut =>
    dut.clockDomain.forkStimulus(period = 10)

    // send 4 data bits: 1, 0, 1, 1
    val dataBits = List(true, false, true, true)

    for (bit <- dataBits) {
      dut.io.dataIn #= bit
      // each bit lasts 8 chips
      for (chip <- 0 until 8) {
        dut.clockDomain.waitSampling()
        println(s"data=${if(dut.io.dataIn.toBoolean) 1 else 0}  " +
          s"prns=${if(dut.io.prnsOut.toBoolean) 1 else 0}  " +
          s"chip=${if(dut.io.chipOut.toBoolean) 1 else 0}")
      }
    }
  }
}
