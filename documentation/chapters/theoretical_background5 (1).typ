== Cross Correlation

Cross-correlation quantifies the similarity between two signals as a function of a relative time shift $tau$. For two real, discrete-time sequences $x[n]$ and $y[n]$, the cross-correlation is defined as

$ R_{x y}[k] = sum_{n=-infinity}^{infinity} x[n] med y[n+k] $

When $x = y$, this reduces to the autocorrelation $R_{x x}[k]$, which measures how similar a signal is to a time-shifted copy of itself. Autocorrelation is central to DSSS because the spreading sequence's own correlation properties determine how sharply a receiver can localize the correct code phase.

=== Properties relevant to spreading codes

For a pseudorandom noise (PN) sequence $c[n] in {+1,-1}$ of period $N$, two properties matter for this project:

- *Autocorrelation*: an ideal spreading code has a sharply peaked autocorrelation function — a single high value at zero lag ($k=0$) and low, ideally near-zero, values ("sidelobes") at all other lags:

$ R_{c c}[k] = cases(
  N & "if" k equiv 0 mod N,
  -1 " (or small)" & "otherwise"
) $ #cite(<understanding_gps>)

  For example, the GPS C/A code has an off-peak autocorrelation value of exactly $-1\/1023$, normalized to a unity peak #cite(<understanding_gps>). This is what allows a receiver to identify the correct code phase unambiguously: any misalignment collapses the correlation output toward zero, while perfect alignment produces a strong peak. Maximal-length LFSR sequences (m-sequences) of length $N = 2^n - 1$ achieve exactly this two-valued autocorrelation, with off-peak value $-1$, a property this project relies on directly for its 10-bit LFSR chip generator, which produces the same 1023-chip code length used by GPS.

- *Cross-correlation between distinct codes*: in a multi-user (CDMA-style) system, low cross-correlation between different users' codes is what allows them to share the same band with limited mutual interference, which is a receiver correlating against its own code should see near-zero contribution from another user's spread signal #cite(<orth_maximum_2024>).

=== The sliding correlator

Practically, a receiver does not know the incoming code phase in advance. It computes $R_{r c}[k]$ between the received chip stream $r[n]$ and a locally generated, phase-shifted replica of the code $c[n+k]$ for a range of trial shifts $k$. This is the sliding correlator, and the shift $k$ that maximizes $|R_{r c}[k]|$ is the acquired code phase #cite(<understanding_gps>). This operation is the theoretical basis for the acquisition stage implemented in the receiver's correlator logic, discussed in the system design chapter.

== Signals and Noise

=== Channel model

The channel between transmitter and receiver is modeled as an Additive White Gaussian Noise (AWGN) channel:

$ r(t) = s(t) + n(t) $ #cite(<sim_comm>)

where $s(t)$ is the transmitted (spread) signal and $n(t)$ is zero-mean Gaussian noise with power spectral density $N_0 slash 2$ (two-sided). "White" means the noise power is uniformly distributed across frequency, so it corrupts every chip independently which is a reasonable first-order model for the emulated channel board in this project, which injects controllable attenuation and noise onto the chip stream.

=== SNR and $E_b slash N_0$

Two related figures of merit are used throughout:

#table(
  columns: 2,
  [*Quantity*], [*Definition*],
  [SNR], [$P_"signal" slash P_"noise"$, ratio of signal to noise power (often per chip in DSSS)],
  [$E_b slash N_0$], [Energy per information bit relative to noise power spectral density; the standard measure for comparing digital modulation schemes independent of bandwidth],
)
#cite(<sim_comm>)

Because DSSS trades bandwidth for robustness, the chip-level SNR seen by the receiver's front end can be considerably worse than the bit-level SNR recovered after despreading. This gap is exactly the processing gain discussed below.

=== Matched filtering and the correlation receiver

For a known pulse shape in AWGN, the linear filter that maximizes the output SNR at the sampling instant is the matched filter, whose impulse response is the time-reversed, conjugated transmit pulse:

$ h(t) = s(T_b - t) $ #cite(<sim_comm>)

For rectangular chip/bit pulses (as used in this project's PAM-style baseband signalling), the matched filter is equivalent to an integrate-and-dump correlator, which is, multiplying the received signal by a local replica of the expected waveform and integrating over the symbol period. This is precisely what the DSSS despreading correlator does: it is a matched filter matched to the known chip sequence, which is why coherent code alignment is essential for correct data recovery.

=== Bit error rate

For binary antipodal signalling (BPSK-equivalent, as used for the chip values $in {+1,-1}$) in AWGN, the theoretical bit error probability after matched filtering is

$ P_b = Q!(sqrt(2 E_b slash N_0)) $ #cite(<sim_comm>)

where $Q(x) = frac(1, sqrt(2 pi)) integral_x^infinity e^{-u^2 slash 2} d u$ is the Gaussian tail function #cite(<sim_comm>). This closed-form expression is the benchmark against which the project's simulated BER-vs-SNR curves (produced by the Python modeling suite) are compared, and against which the measured coding/processing gain of the spreading system is quantified.

=== The Shannon-Hartley theorem and spreading

The Shannon-Hartley theorem #cite(<shannon_noise>)

$ C = W log (1 + P/(N_0 W)) $

states that for a given power $P$ and noise spectral density $N_0$, the channel capacity $C$ asymptotically approaches a certain limit with increasing channel bandwidth $W$, as shown in @shannon_graph. From this follows that a signal can be "spread" across the spectrum arbitrarily, without increasing transmit power, even though the total noise power in the channel increases.

#figure(
  image("../images/shannon.png"),
  caption : [Channel capacity in relationship with the noise-equivalent bandwidth $W_0$ and actual bandwidth $W$, from @shannon_noise.]
) <shannon_graph>

To interfere with such a signal using a broadband jammer, the required power to reduce the received signal quality significantly can be arbitrarily large with increasing spreading factor, also referred to as processing gain

$ G = W_s/W $

where $W$ is the signal bandwidth before, and $W_s$ the signal bandwidth after the spreading operation.

== Direct-Sequence Spread Spectrum

=== Spreading operation

A DSSS transmitter multiplies each data bit $b in {+1,-1}$, held constant for one bit period $T_b$, by a PN chip sequence $c[n] in {+1,-1}$ running at a much higher rate:

$ s[n] = b dot c[n], quad n = 0, dots, N_c - 1 $ #cite(<10498616>)

where $N_c$ chips are transmitted per data bit. In hardware this multiplication is implemented as an XNOR between the data bit and the LFSR-generated chip, which is logically equivalent to $plus.minus 1$ multiplication on bipolar values, which is the basis of the Transmitter module in this project. In this project, $N_c = 1023$, matching the GPS C/A code length discussed below.

=== Processing gain

The ratio of chip rate to bit rate defines the processing gain:

$ G_p = frac(R_c, R_b) = frac(T_b, T_c) = N_c $ #cite(<10498616>)

expressed in decibels as $G_p ["dB"] = 10 log_10 (N_c)$. For GPS, this definition gives a C/A-code processing gain of approximately 43 dB (chip rate 1.023 Mchip/s against a 50 bit/s navigation data rate), which combines with the required post-correlation $E_b slash N_0$ and an implementation loss term to give the system's jamming margin,

$ M_j ["dB"] = G_p ["dB"] - (E_b slash N_0)_"req" ["dB"] - L_"impl" ["dB"] $ #cite(<understanding_gps>) #cite(<receiver_loss>)

Processing gain is the theoretical source of the despreading correlator's noise-averaging benefit: narrowband interference and noise, uncorrelated with the code, are spread out (and thus attenuated) by the despreading multiplication, while the wanted signal, correlated with the local code replica, is coherently reconstructed. This matches the project's empirical finding that increasing chip rate improves the effective coding gain (9 dB coding gain observed at $"chip_rate"=8$, sufficient to drive BER to 0% at 3 dB SNR); the choice of chip/sampling rate relative to the code rate is well known to affect DLL tracking performance in exactly this way #cite(<sampling_freq_effects>) #cite(<mitigate_samp_rate>).

=== Despreading and code synchronization

At the receiver, despreading is the inverse operation: the incoming chip stream is correlated against a locally generated, correctly phase-aligned replica of the same PN code and integrated over one bit period:

$ hat(b) = "sign"( sum_{n=0}^{N_c-1} r[n] med c[n] ) $ #cite(<10498616>)

Correct operation depends entirely on the local code being phase-aligned with the incoming code, this is the code synchronization problem, which splits into two sub-problems:

- *Acquisition*: coarse alignment, found via the sliding correlator search described above, declaring "synchronized" once the correlation peak exceeds a detection threshold.
- *Tracking*: fine, continuous alignment maintained after acquisition. Since transmitter and receiver clocks are never perfectly matched, small frequency offsets (measured in ppm) cause the code phases to slowly drift apart over time. The correlation peak would walk away from the sample instant if left uncorrected. A Delay-Locked Loop (DLL) correlates the incoming signal against an *early* ($c[n+delta]$) and a *late* ($c[n-delta]$) replica of the local code, offset by a fraction of a chip $delta$, and forms the normalized early-late discriminator

$ D = frac(|R_E| - |R_L|, |R_E| + |R_L|) $ #cite(<delay_locked_loop>) #cite(<understanding_gps>) #cite(<springer_sig_proc>)

  whose sign and magnitude drive the local code clock back toward alignment, keeping the correlation peak centered, the same early-late DLL architecture used for code tracking in GPS receivers. This directly motivates the DLL-based tracking logic implemented for the receiver in this project, needed once uncorrected offsets in the 5–10 ppm range were shown to cause synchronization failure.

=== LFSR-generated PN sequences

The chip sequences are generated by Linear Feedback Shift Registers implementing primitive polynomials over $"GF"(2)$. An $n$-bit LFSR with a primitive feedback polynomial produces a maximal-length sequence (m-sequence) with period

$ N = 2^n - 1 $ #cite(<orth_maximum_2024>)

M-sequences have three properties (the "balance", "run", and "correlation" properties) that make them suitable spreading codes #cite(<orth_maximum_2024>):

+ *Balance*: in one period, the number of 1s exceeds the number of 0s by exactly one.
+ *Run distribution*: runs of consecutive equal chips occur with geometrically decreasing frequency, mimicking a random binary sequence.
+ *Two-valued autocorrelation*: as given above, $R_{c c}[k] = N$ at $k=0$ and $-1$ elsewhere — the property directly exploited for acquisition.

This project uses a 10-bit LFSR, yielding $N = 1023$ chips per period, the basis of the PRNS generator used both in the FPGA transmitter/receiver and in the Python reference model for offline BER and tracking analysis. This is deliberately the same code length used by the GPS C/A code, which is also generated from 10-bit shift registers at a chip rate of 1.023 Mchip/s #cite(<understanding_gps>).

=== Multiple access and the near–far problem

Because distinct users' codes have low mutual cross-correlation, several DSSS transmissions can share the same frequency band simultaneously (Code-Division Multiple Access) with each receiver's correlator suppressing the others as pseudo-noise #cite(<orth_maximum_2024>). This robustness is not unconditional, however: if an interfering transmitter's received power is much larger than the desired signal's, residual cross-correlation sidelobes can still swamp the wanted correlation peak, which is why power control or attenuation matching matters in the channel emulation stage of this project's three-board test setup.

