# ToDos

Estimate for data rates+spreading factors possible with hardware used

## Python modeling: 

- SNR/ Channel Capacity
- float vs 14 bit
    - Level plan
    - Frequency Offset influence
      - How big offset to make sync difficult?
      - How long
- Signal generation ✅
- Noise generation / channel model ✅
- Synchronization / decoding ✅

## SpinalHDL: 

- toolchain setup ✅
- learn basics 
- adc/dac setup
- transmitter
  - LFSR  
- receiver
  - LFSR
  - Unroll LFSR for more steps per cycle
    - Serial Correlator + Accumulator
      - Evaluate tradeoffs: Size vs sync speed
        - see how frequency offset is handled
  - Test how big parallel correlator would be
- noise generator / channel simulator


## Todos python

- Write channel in python with our polynomial's, simulate as Gaussian noise pseudo random generator, plot (PDF) histogram

- Python adjust existing code for receiver to give plot BNR to Signal to noise ratio. Already exists,  TODO: fix axis, beautify scatterplot 

- python: FT to try and detect transform. Graph over symbols, how many symbols correlate to find the peak. X axis signal to noise, Y axis success rate. one line for 1 symbol, one or 8, one for 100 ... . Maybe instead of integrating over symbols, integrate over time as label for plot -> Should give linear relationship

- Frequency error vs time until time error is bigger than one chip -> Maximum useful integration time

- quantisation and truncation python (ADC/DAC)

- Check, are we making uniform noise in the channel? 

- 


