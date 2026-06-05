package spreads.src

import spinal.core._

// example polynomials:
//  x^10 + x^3 + 1  
//  x^10 + x^4 + x^3 + x^1 + 1
//  x^32 + x^22 + x^2 + x^1 + 1
//  x^16 + x^15 + x^11 + x^10 + x^9 + x^8 + x^6 + x^4 + x^2 + x^1 + 1

//  For  x^10 + x^3 + 1  with 10 bit output and 10 steps per cycle:
//  UnrollLFSR(Array(9,2), 10, 10, 10)

// Hardware definition
case class UnrollLFSR(poly: Array[Int], m_lfsr: Int, steps: Int, n_out :Int) extends Component {
  val io = new Bundle {
    val enable = in  Bool()
    val flag  = out Bool()
    val rnd_o = out UInt(n_out bits)
    val skip = in Bool()
    val state = out Bits(m_lfsr bits)
  }

  def step(state: Bits): Bits = {
    val fb = poly.foldLeft(False)((a,b) => a ^ state(b))
    state(state.getWidth -2 downto 0) ## fb
  }

  def advance(state: Bits, n:Int): Bits = {
    (0 to n-1).foldLeft(state)((s,_) => step(s))
  }
  
  val sr = Reg(Bits(m_lfsr bits)) init(B(m_lfsr bits, default -> True))
  
  when(io.enable) {
    when(io.skip) {
      sr := advance(sr, steps+1)
    } otherwise {
      sr := advance(sr, steps)
    }
  }
  
  io.state := sr
  io.rnd_o := sr(n_out-1 downto 0).asUInt
  io.flag := sr.andR
}
