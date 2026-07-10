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
