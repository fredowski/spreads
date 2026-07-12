= Introduction
Direct-Sequence spread spectrum (DSSS) modulation is the basis of many modern wireless systems, a famously known example is modern GPS. A DSSS transmitter spreads a single data bit into many chips. Chips are creating using a pseudorandom sequence generated from a Linear Feedback Shift Register (LFSR), expanding the single data bit into a much longer sequence. This results in transmitting over a considerably wider bandwidth than the original data alone would need. A receiver that knows the same chip sequence can despread the signal and recover the original data. Other receivers that do not know the exact code sequence only sees noise and can't descramble the incoming chips. This property gives DSSS systems a natural resistance to interference and also allows different users to share the same frequency band simultaneously. // TODO:  cite any bloody DSSS survey (Pickholtz)

In this Master Project, the project group is building a working DSSS transmitter and receiver in hardware on a DE2 FPGA. A receiver does not know when a transmission starts or what phase of the spreading code it will see first, and must search for the correct code phase before it can track it. Once it found the correct code, it considers it self `synchronized`. Small clock differences between transmitter and receiver mean the receiver must constantly adjust its own code phase to stay locked onto the transmitter. These problems become harder under the constraints of a FPGA system running from a 50 MHz clock and no floating-point hardware.

This project addresses these problems by designing and implementing a complete DSSS communication system in hardware. The project splits the system across three separate FPGA boards. A transmitter which spreads and outputs the individual chips over an ADC add-on board, a channel which injects attenuation and noise, and a receiver that must acquire and track the spreading code.

The whole project is implemented using the SpinalHDL language, a Scala based hardware description language, which compiles into Verilog or VHDL. This project also considers the impacts of SpinalHDL on the whole design and implementation process. It serves as a test of how well SpinalHDLs abstractions hold up on a decently complex design. The receiver is the central focus of this work, since it has to solve both the code acquisition and code tracking problems.

This projects documentation begins with the theoretical background needed to follow the rest of the thesis, covering cross-correlation, the behaviour of signals in noise, and the principles of Direct-Sequence Spread Spectrum. This is followed by a description of the system design and FPGA implementation, starting with a short introduction to SpinalHDL itself, before covering the transmitter, the emulated channel, and the receiver's acquisition and tracking logic in detail. The final chapters present the simualtion of the system, the results and conclude the projects documentation. 

= Theoretical Background

== Cross Correlation

== Signals and Noise

@shannon_noise

== Direct-Sequence Spread Spectrum
