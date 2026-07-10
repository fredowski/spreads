= Implementation

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    High level overview of the system comprised of transmitter, channel model, and receiver
  ]
)

== Transmitter

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Block Diagram of the transmitter
  ]
)

== Channel Emulator

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Block Diagram of the channel emulator
  ]
)


== Receiver

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Block Diagram of the receiver
  ]
)

=== Code Acquisition

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Block Diagram of the code acquisition block
  ]
)

=== Code Tracking

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Block Diagram of the code tracking block
  ]
)

=== Limitations of the chosen hardware

Typical receivers rely on a finely adjustable PLL or numerically controlled oscillator (NCO) to match the receiver frequency to the transmitted signal frequency @springer_sig_proc.
For this, either control signals emitted by the code acquisition and tracking modules, or a carrier tracking loop are used.
The Cyclone II FPGA does not contain such fixed function blocks. While a fully digital implementation is possible on an FPGA without specialized hardware @adpll, it is considered out of scope for this project.
Instead, for frequency tracking, the receiver reference code generator is periodically skipped forward or delayed by one cycle,
 whenever the accumulated error signal of the Code Tracking Block exceeds a chosen threshold.
