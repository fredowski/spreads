= Physical Verification

To verify the function of each module, first, the transmitter is directly connected to an oscilloscope. The capture is shown in @tx_sig. Clearly visible are very sharp signal edges, showing spectral content at high frequencies.

#figure(
  image("../images/tx_signal.png"),
  caption: [
    Oscilloscope capture of the transmitter output signal.
  ]
) <tx_sig>



Next, a $11"MHz"$ low-pass filter (LPF) is connected between the transmitter and the oscilloscope. The capture in @tx_sig_11 shows that the signal cannot reach the full level at every chip transition anymore.

#figure(
  image("../images/tx_signal_11MHz.png"),
  caption: [
    Oscilloscope capture of the $11"MHz"$ low-pass filtered transmitter output signal.
  ]
) <tx_sig_11>

@tx_sig_fft_1 shows the transmit signal at a wider time span, as well as a spectrum of the trace computed by the oscilloscope. The shape of the LPF can be seen as the outline of the spectrum, while the transmit signal generates a comb-like spectrum.

#figure(
  image("../images/tx_signal_wide_11MHz.png"),
  caption: [
    FFT of the $11"MHz"$ low-pass filtered signal, showing the filter characteristic as well as the comb-like structure of the transmit signal, as well as the repetitive nature of the code.
  ]
) <tx_sig_fft_1>

To investigate the comb structure in more detail, a zoomed-in spectrum is shown in @tx_sig_fft_2. The distance between the peaks is measured at roughly $48 "kHz"$, matching the code repetition rate of $50 "MHz" / (1023 "chips") approx 49 "kHz"$.

#figure(
  image("../images/tx_signal_fft.png"),
  caption: [
    FFT of the $11"MHz"$ low-pass filtered signal, showing the $48 "kHz"$ spacing of the comb-like transmit signal.
  ]
) <tx_sig_fft_2>

To verify the function of the receiver, the transmitter and receiver are first connected directly, without channel emulator.
\
The data input of the transmitter is fixed to either "1" or "0", so that the data output of the receiver outputs a pulse whenever a bit error occurs, and otherwise stays at a constant value.

When the receiver fails to track the code or repeatedly loses the lock, it outputs a rapid series of pulses as shown in @rx_data_bad.

#figure(
  image("../images/broken_no_sync.png"),
  caption: [
    Capture of the receiver data output in a lost-lock state.
  ]
) <rx_data_bad> 

Once we tuned the control loops of the receiver to a degree that they were able to reliably lock onto and track a noise-free signal, the channel emulator was inserted between transmitter and receiver. Basic function could be verified, however, a detailed bit-error-rate analysis was not performed, as the performance of the implemented code tracking loop was not sufficient and required more tuning. Once the receiver fully loses lock to the signal, the BER is 50%, at significantly higher SNR levels than shown in @ber_vs_snr.

@sync_wide shows the code acquisition process. As no threshold to determine whether a signal has been found has been implemented, the receiver outputs data as soon as it is enabled. Whenever a new maximum correlation offset is found, the receiver shifts to this offset. Thus, until the actual signal is found, the value of the output is random. Once the signal has been acquired, the output settles to the correct value.

#figure(
  image("../images/sync_zoomedout.png"),
  caption: [
    Capture of the receiver data output during initial code acquisition. The transmitted data bit is fixed to "1". During code acquisition, the receiver attempts to decode data from every candidate code offset as they are found, so random data is output until finally the correct code offset is acquired and locked onto.
  ]
) <sync_wide> 

@sync_narrow shows a more detailed view of this process. The time to acquire the signal using a single correlator is measured at $16"ms"$, which means the receiver computed roughly 750 correlations until the signal was acquired.

#figure(
  image("../images/sync_zoomedin.png"),
  caption: [
    Shorter time base capture of the receiver data output during initial code acquisition. The acquisition process took $16"ms"$.
  ]
) <sync_narrow> 
