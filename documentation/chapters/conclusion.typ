= Conclusion

This project successfully designed and implemented a complete DSSS communication system in hardware. By splitting the project into three parts, across three DE1 FPGA boards, the project covered a working transmitter, an adjustable noise generator, and a receiver that handles both code acquisition and tracking.

== Direct-Sequence Spread Spectrum Conclusion
The implementation of the DSSS system proved that the theoretical concepts translate well into a working hardware design. Despite the limitations of a strict 50 MHz clock and no floating-point math, the main logic worked as expected and the receiver managed to correctly receive data from the transmitter. Even when connection the channel between transmitter and receiver, the receiver was still able to find the correct code phase. Once locked, the Delay-Locked Loop was able to successfully track the signal and recover the original data bits. In conclusion, while there is room for future optimization, such as implementing a formal loop filter or automatic re-acquisition, the DSSS system still works in its current state.

== SpinalHDL Conclusion
Using SpinalHDL as the hardware description language proved to be a advantage for this project. It made implementing the designs, like the 32 parallel correlators and the unrolled LFSRs, easier than writing standard VHDL or Verilog by hand. This was also due to the benefit that SpinalHDL was deemed as more accessible to beginners. Some of the group members were relatively new to hardware design and had limited experience with VHDL or Verilog. Still, because SpinalHDL offers high-level software abstractions, the design process was manageable for them and the ability to use standard software paradigms saved a significant amount of development time.

In summary, the project met its main goals. The final hardware successfully transmits and recovers data. This proves that the theoretical DSSS concepts and the SpinalHDL design approach work well.

#pagebreak()

#figure(
  rotate(-90deg, reflow: true, image("../drawio/receiver.svg", width: 100%)),
  caption: [Receiver block diagramm],
    placement: bottom,
  scope: "parent",
) <receiver-block>
