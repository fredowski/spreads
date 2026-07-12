package spreads

import spinal.core._
import spinal.lib._

case class Channel() extends Component {
  val io = new Bundle {
    val enable = in Bool ()
    val i = in SInt (14 bits)
    val attenuation = in UInt (4 bits)
    val noise = in UInt (4 bits)
    val o = out SInt (14 bits)
  }

  // x^30 + x^23 + x^2 + x^1 + 1
  val poly0 = List(29, 22, 1, 0)
  val lfsr0 = UnrollLFSR(poly0.toArray, poly0.max + 1, 12, 12)
  lfsr0.io.skip := False
  lfsr0.io.enable := io.enable
  lfsr0.io.load := False
  lfsr0.io.i_parallel := (default -> false)
  // x^31 + x^27 + x^23 + x^19 + x^15 + x^11 + x^10 + x^9 + x^7 + x^6 + x^5 + x^3 + x^2 + x^1 + 1
  val poly1 = List(30,26,22,18,14,10,9,8,6,5,4,2,1,0)
  val lfsr1 = UnrollLFSR(poly1.toArray, poly1.max+1, 1, 12)
  lfsr1.io.skip := False
  lfsr1.io.enable := io.enable
  lfsr1.io.load := False
  lfsr1.io.i_parallel := (default -> false)
  // x^32 + x^22 + x^2 + x^1 + 1
  val poly2 = List(31,21,1,0)
  val lfsr2 = UnrollLFSR(poly2.toArray, poly2.max+1, 2, 12)
  lfsr2.io.skip := False
  lfsr2.io.enable := io.enable
  lfsr2.io.load := False
  lfsr2.io.i_parallel := (default -> false)
  // x^32 + x^22 + x^21 + x^20 + x^18 + x^17 + x^15 + x^13 + x^12 + x^10 + x^8 + x^6 + x^4 + x^1 + 1
  val poly3 = List(31,21,20,19,17,16,14,12,11,9,7,5,3,0)
  val lfsr3 = UnrollLFSR(poly3.toArray, poly3.max+1, 3, 12)
  lfsr3.io.skip := False
  lfsr3.io.enable := io.enable
  lfsr3.io.load := False
  lfsr3.io.i_parallel := (default -> false)
  // x^32 + x^28 + x^19 + x^18 + x^16 + x^14 + x^11 + x^10 + x^9 + x^6 + x^5 + x^1 + 1
  val poly4 = List(31,27,18,17,15,13,10,9,8,5,4,0)
  val lfsr4 = UnrollLFSR(poly4.toArray, poly4.max+1, 1, 12)
  lfsr4.io.skip := False
  lfsr4.io.enable := io.enable
  lfsr4.io.load := False
  lfsr4.io.i_parallel := (default -> false)

  var N = SInt(14 bits)
  N =
    (lfsr0.io.rnd_o.asSInt +^ lfsr1.io.rnd_o.asSInt +^ lfsr2.io.rnd_o.asSInt) +| lfsr3.io.rnd_o.asSInt +| lfsr4.io.rnd_o.asSInt

  io.o := (io.i >> io.attenuation) +| (N >> io.noise)

  // this is where I would put my math... IF I HAD ANY
  // val X = Math.sqrt(-2 * Math.log(lfsr1.io.rnd_o)) * Math.cos(2*Math.PI*lfsr2.io.rnd_o)
}
