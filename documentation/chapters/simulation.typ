= Simulations

== Bit-Error Rate of an Ideal Receiver in the Presence of Noise

#figure(
  square(size: 10em, stroke: 2pt, image("../images/BER_vs_SNR__Binary_Noise.svg"),),
  caption: [
    BER of ideal receiver with decreasing signal-to-noise-ratio
  ]
)

#figure(
  square(size: 10em, stroke: 2pt, image("../images/BER_vs_SNR__Binary_Noise.svg"),),
  caption: [
    Simulated BER vs. SNR performance under AWGN and binary noise
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
    image("../images/PDF_Of_Channel.svg"),
    caption: [
    Approximated PDF of generated noise. The extreme bins, containing the maximum and minimum of the 14-bit signed integer value, respectively, show a slightly inflated ocurrence due to the applied clipping function.
  ]
)

== Impact of Quantization and Truncation
