= Outlook

The system developed in this project could be extended to 

Several parts of this design were built with a working system as the first priority, not resource efficiency or performance. This chapter collects a few ideas and directions for further optimization that based on the choices made in the current implementation, several of which map onto standard GNSS receiver design trade-offs described in the literature @springer_sig_proc.

== ADC Sampling Rate

In our implementation, the signal is sampled at the chip rate. Sampling at integer multiples of the chip rate can introduce distortions that significantlyreduce tracking performance @mitigate_samp_rate. To reduce this effect, jitter can be introduced to the correlator, with a relatively small amount of additional hardware resources @mitigate_samp_rate.

== ADC bit depth
It is possible to reduce the number of quantization levels of the analog-to-digital converter, down to as little as one or two bits, without a significant loss in correlation performance, while drastically reducing the hardware complexity of the receiver @receiver_loss. \
In our implementation, the ADC bit depth `n_adc` is used as a parameter through the entire signal path rather than hardcoded. All 32 parallel `Code_Acquisition` correlators, the three tracking correlators (Early, Prompt, Late), and the `DLL`'s input ports sizes are defined as `n_adc + m_lfsr` bits wide. Reducing `n_adc` does not only shrink the ADC itself, it shrinks every accumulator and adder in all 35 correlators at once. \ // TODO: replace with  exact dB figures from @receiver_loss maybe
For the acquisition stage, 32 parallel correlators were chosen as a tradeoff between search time and FPGA logic usage, the logic freed up by a smaller `n_adc` could be reinvested into more parallel search engines instead. This would improve acquisition time without a larger FPGA.

== DLL loop filter design
The current `DLL` increments or decrements `errorReg` by a fixed step (`+|2`/`-1`) each cycle the early or late accumulator dominates. Then, an `advance` or `delay` command is set when `errorReg` crosses a hardcoded threshold of 20. \
Comparing this to a standard approach in GNSS receiver design specifies a DLL loop filter directly in terms of a target noise bandwidth $B_n$, with the filter order and $B_n$ together determining the trade-off between residual tracking-noise jitter and how quickly the loop can respond to genuine code-phase drift @springer_sig_proc. Since code tracking loops are usually implemented with a first-order filter, replacing the current threshold based implementation with a filter parameterized directly by a chosen $B_n$ would make this an adjustable design parameter rather than somewhat empirically-chosen constants. This would also make the loop's expected steady-state tracking error predictable rather than something to be found only through simulation.

== Correlator spacing
The code tracking correlators are spaced one chip apart. Prerequisites have been made to easily allow reducing the chip rate to 25 MHz while keeping the clock speed of 50 MHz, thus reducing the correlator spacing to half-chip. Additionally, more complex code tracking implementations with more correlators can be studied.

== Channel emulator

The channel emulator can be extended to emulate multipath channels in addition to AWGN channels. For this, shift registers that produce delayed copies of the signal at the input could be used. Additionally, the spectrum of the channel can be shaped by implementing various digital filters.