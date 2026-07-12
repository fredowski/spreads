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
  bibliography: bibliography("chapters/refs.bib"),
  figure-supplement: [Fig.],
)

#include "chapters/introduction.typ"

#include "chapters/system_design.typ"

#include "chapters/simulation.typ"

#include "chapters/performance_analysis.typ"

#include "chapters/outlook.typ"

#include "chapters/conclusion.typ"





