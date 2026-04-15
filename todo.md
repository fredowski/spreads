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
