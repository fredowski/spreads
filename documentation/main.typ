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
    This project covers the design, implementation and performance analysis of a Direct-Sequence Spread Spectrum receiver. 
  ],
  authors: (
    (
      name: "Daniel Pedder",
      email: "Daniel.Pedder@tha.de"
    ),
    (
      name: "Thomas Homm",
      email: "Thomas.Homm@tha.de"
    ),
    (
      name : "Mahalakshmi Krishnan",
      email : "Mahalakshmi.Krishnan@tha.de"
    ),
  ),
  // index-terms: ("Direct-Sequence Spread Spectrum", "GPS", "GNSS"),
  bibliography: bibliography("refs.bib"),
  figure-supplement: [Fig.],
)

= Introduction

= Theory

= Implementation

= Performance Analysis

= Outlook

= Conclusion