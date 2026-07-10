#import "@preview/charged-ieee:0.1.4": ieee

#let inwriting = false
#let todo(it) = [
  #if inwriting [
    #text(size: 0.8em)[#emoji.pencil]  #text(it, fill: red, weight: 600)
  ]
]


#show: ieee.with(
  title: [],
  abstract: [
    This project covers the design and hardware implementation of a baseband Direct-Sequence Spread Spectrum transmitter, receiver, as well as a AWGN channel emulator. Each of these three components is implemented on an Altera Cyclone II FPGA equipped with external 14-Bit DAC/ADCs. The real world performance of the system is compared to Monte Carlo simulations.
  ],
  authors: (
    (
      name: "Daniel Pedder",
      email: "Daniel.Pedder@tha.de"
    ),
    (
      name : "Mahalakshmi Krishnan",
      email : "Mahalakshmi.Krishnan@tha.de"
    ),
    (
      name: "Thomas Homm",
      email: "Thomas.Homm@tha.de"
    ),
  ),
  // index-terms: ("Direct-Sequence Spread Spectrum", "GPS", "GNSS"),
  bibliography: bibliography("refs.bib"),
  figure-supplement: [Fig.],
)

= Introduction

Direct-Sequence spread spectrum (DSSS) modulation is the basis of 

= Theoretical Background

== Cross Correlation

== Signals and Noise

@shannon_noise

== Direct-Sequence Spread Spectrum

= System Design

The used FPGA development boards provide a $50 "MHz"$ clock, 
which is selected as both the clock rate and the chip rate of the system. 

== Code Acquisition

=== Exhaustive Search with linear Correlation

Chosen in cases of limited hardware resources

=== Matched Filter

Fast, relatively compact

=== Fast Fourier Transform

Cross-correlation theorem

Used by every software implementation, also common in hardware.

== Code Tracking

Delay-Locked loop

Early, Prompt, and Late correlator

= Simulations

== Bit-Error Rate of an Ideal Receiver in the Presence of Noise

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    BER of ideal receiver with decreasing signal-to-noise-ratio
  ]
)

== Required Integration Period for successful Code Acquisition 

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Code Acquisition success rate within N signal periods with decreasing signal-to-noise-ratio
  ]
)

The integration time required for code acquisition is in a linear relationship with the SNR: 

$ "SNR" = (2nu T C \/N_0) /  sqrt(4nu)  = T sqrt(nu) C/N_0$

Where $nu$ is the number of noncoherent integrations, $C/N_0$ is the Carrier-To-Noise ratio, and $T$ is the integration time.

As the SNR decreases by 3dB, the required integration time doubles. @springer_sig_proc TODO: INSERT THEORETICAL ARGUMENT
As the integration period is increased, the maximum frequency offset at which code acquisition is successful decreases 

== Pseudo-AWGN generation

#figure(
  square(size: 10em, stroke: 2pt),
  caption: [
    Approximated PDF of generated noise
  ]
)

== Impact of Quantization and Truncation

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

= Performance Analysis

== Comparison of RTL Simulation and Real-World Measurement

= Outlook

Further optimizations 
== ADC bit depth

It is possible to reduce the number of quantization levels of the analog-to-digital converter drastically or fewer bits without significant loss of performance, while drastically reducing the hardware complexity @receiver_loss.

= Conclusion