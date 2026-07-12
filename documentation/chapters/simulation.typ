= Simulations

== Bit-Error Rate of an Ideal Receiver in the Presence of Noise

A test sequence of random bits is generated and encoded. Then, random gaussian noise is added so that
#v(0.2em)
$ "SNR" = S^2/N^2 = 1/sigma^2 $ 
#v(0.2em)
where $S^2 = 1$ is the power of the signal and $sigma^2$ is the power of the noise.

To simulate and evaluate an ideal DLSS receiver with zero frequency error $delta f$ and time error $delta tau$, the resulting noisy signal is split into code length sized bins, and the correlation of the code with each of the bins is computed. The bit decision is made at a decision threshold of $0$.

The result is shown in @ber_vs_snr. Up to a SNR of $-20"dB"$, the BER is insignificant.


#figure(
  image("../images/ber_vs_snr.svg"),
  caption: [
    BER of ideal receiver with decreasing signal-to-noise-ratio, assuming successful code acquisition.
  ]
) <ber_vs_snr>

== Required Integration Period for successful Code Acquisition 

#figure(
  image("../images/acq_success_rate.svg"),
  caption: [
    Code acquisition success rate with decreasing signal-to-noise-ratio, for different integration times $T$ given as multiples of the code length.
  ]
) <success_rate>

The integration time required for code acquisition is in a linear relationship with the SNR@springer_sig_proc: 
#v(0.2em)
$ "SNR" = T sqrt(nu) C/N_0 $

where $C/N_0$ is the Carrier-To-Noise ratio, $T$ is the integration time, and $nu$ is the number of averaged noncoherent integrations.
As the SNR decreases by 3dB, the required integration time doubles.

@success_rate shows the increasing success rate with larger integration time $T$ as the result of a Monte-Carlo simulation.

Increasing the coherent integration time $T$ comes with the following downside: As $T$ increases, the residual frequency offset $delta f$ between transmitter and receiver gets accumulated, eventually leading to a decreased correlator result. To avoid this, 
#v(0.2em)
$ T < (T_C - delta tau_"initial")/T_C dot delta f $

should be observed, where $T_C$ is the chip period and $delta tau_"initial"$ is the time offset at the beginning of the correlator integration period, with $delta tau_"initial" << T_C$ for a correlator that successfully "finds" the transmitted signal.


== Pseudo-AWGN generation

A single LFSR produces a quasi-sinc shaped line spectrum @orth_maximum_2024.
To emulate an additive white gaussian noise channel, approximately gaussian noise can be generated using the central limit theorem, by summing the outputs of multiple LFSRs @sim_comm.

To optimize the choice of parameters quickly, a simulation was performed in python.

#figure(
    image("../images/channel_fft_initial.svg"),
    caption: [
    Fourier transform of $1times 10^5$ generated noise samples using the first configuration of LFSRs
  ]
) <fft_guess>

An initial estimate was made that five LFSRs of $30$ to $32$ bit length would produce noise which is sufficiently uncorrelated to the used code, produced by a $10$ bit LFSR.

A 12-bit output is taken from each of the LFSRs and summed using a saturating addition function, preventing overflow of the 14-bit output value.

Each of the shift registers uses a different polynomial, and also is shifted a different number of steps each cycle.

@fft_guess shows the initial configuration with relatively short LFSR step lengths per cycle. Clearly visible is relatively low power at lower frequencies.
By experimentally adjusting the parameters, the much more "white" spectrum shown in @fft_optimized was achieved.

#figure(
    image("../images/channel_fft_optimized.svg"),
    caption: [
    Fourier transform of $1times 10^5$ generated noise samples using the optimized configuration.
  ]
) <fft_optimized>

#figure(
    image("../images/PDF_Of_Channel.svg"),
    caption: [
    Approximated PDF of generated noise. The extreme bins, containing the maximum and minimum of the 14-bit signed integer value, respectively, show a slightly inflated ocurrence due to the applied saturating addition function.
  ]
)

Alternatively, the Box-Muller method can be used @sim_comm, however this requires the use of more complex mathematical operations, which would have to be specially implemented on the FPGA. 

== Impact of Quantization and Truncation

Initial simulations in python were performed using floating-point numbers. To verify the applicability of simulation results to the hardware implementation, the generated noise is scaled and clipped to fit a 14-bit integer or fixed point value. 

The level of the signal is scaled to [-64,+64] to allow for dynamic range to fit a SNR of up to $-40"dB"$. Clipping values outside the range [$-2^13, 2^13-1$] does not change the simulation results, confirming the validity of the approach.