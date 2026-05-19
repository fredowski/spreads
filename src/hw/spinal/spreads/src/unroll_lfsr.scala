package spreads.src

import spinal.core._

// example polynomials:
//  x^10 + x^3 + 1  
//  x^10 + x^4 + x^3 + x^1 + 1
//  x^32 + x^22 + x^2 + x^1 + 1
//  x^16 + x^15 + x^11 + x^10 + x^9 + x^8 + x^6 + x^4 + x^2 + x^1 + 1

//  UnrollLFSR(Array(10,3), 10)

// Hardware definition
case class UnrollLFSR(poly: Array[Int], m: Int) extends Component {
  val io = new Bundle {
    val cond0 = in  Bool()
    val flag  = out Bool()
    val state = out UInt(m bits)
  }

  val stateReg = Reg(UInt(m bits)) init((scala.math.pow(2,m)-1).toInt)
  val next = UInt(m bits).noCombLoopCheck

  val toXor = Vec.fill(m)(Bits(poly.length bits)).noCombLoopCheck

  val hasChanged = new Array[Boolean](poly.length)

  var i = 0
  while (i < m){
    for (j <- poly.indices){
      //calculate indices for taps
      poly(j) -= 1
      if (poly(j) < 0) {
        hasChanged(j) = true
        poly(j) = java.lang.Math.floorMod(poly(j), m)
      }

      if (!hasChanged(j)) {
        toXor(i)(j) := stateReg(poly(j))
      }
      else {
        toXor(i)(j) := next(poly(j))
      }
    }
    next(m - i -1) := toXor(i).xorR
    i += 1
  }

  when(io.cond0) {
    stateReg := next
  }

  io.state := stateReg
  io.flag := stateReg.andR
}
