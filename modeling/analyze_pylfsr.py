import scipy as sp
import numpy as np
import matplotlib.pyplot as plt

import pylfsr as pyl
from pylfsr import LFSR

# Source - https://stackoverflow.com/a/10238140
# Posted by John Gaines Jr., modified by community. See post 'Timeline' for change history
# Retrieved 2026-04-12, License - CC BY-SA 3.0
def tobits(s):
    result = []
    for c in s:
        bits = bin(ord(c))[2:]
        bits = '00000000'[len(bits):] + bits
        result.extend([int(b) for b in bits])
    return result

def frombits(bits):
    chars = []
    for b in range(int(len(bits) / 8)):
        byte = bits[b*8:(b+1)*8]
        chars.append(chr(int(''.join([str(bit) for bit in byte]), 2)))
    return ''.join(chars)

#config
stdev = 3
offset = 123
thresh = 600

#initialize LFSRs
fpoly = pyl.get_fpolyList(m=10)[0]
L_tx = LFSR(fpoly=fpoly)
L_rx = LFSR(fpoly=fpoly)
rx_code = L_tx.getFullPeriod() * 2 - 1 

L_tx.runKCycle(offset)
tx_code = L_tx.getFullPeriod() * 2 - 1
rng = np.random.default_rng()


#generate transmitted signal
data = np.array(tobits("test 123 test")).flatten()

data = data * 2 - 1

tx_sig = np.outer(data, tx_code).flatten()
print(np.shape(tx_sig))

noise = rng.normal(size=len(tx_sig), scale=stdev)
tx_sig_noisy = tx_sig + noise

#compute correlations
corr = sp.signal.correlate(in1=tx_sig_noisy, in2=rx_code, mode='same')
lags = sp.signal.correlation_lags(len(tx_sig_noisy), len(rx_code), mode='same')

#calculate tx rx offset
highest_corr_indx = np.argmax(np.abs(corr))

rx_offset = 1023 - lags[np.argmax(np.abs(corr))] % len(rx_code)
print(rx_offset)
#shift rx LFSR to match TX
L_rx.runKCycle(rx_offset)
rx_code_offset = L_rx.getFullPeriod() * 2 - 1


#compute correlations with corrected offset
corr_o = sp.signal.correlate(in1=tx_sig_noisy, in2=rx_code_offset, mode='same')
lags_o = sp.signal.correlation_lags(len(tx_sig_noisy), len(rx_code_offset), mode='same')

#decode
rx_data = []
for c in corr_o:
    if c < -thresh: 
        rx_data.append(0)
    if c > thresh:
        rx_data.append(1)

rx_result = frombits(rx_data)
print(rx_result)

rx_data_uncorrected = []
for c in corr:
    if c < -thresh: 
        rx_data_uncorrected.append(0)
    if c > thresh:
        rx_data_uncorrected.append(1)

rx_result_uncorrected = frombits(rx_data_uncorrected)
print(rx_result_uncorrected)

#max=len(rx_code)*20
max=-1
plt.vlines(lags_o[0:max],0, corr_o[0:max], label="corrected", colors="#1a85ff")
plt.scatter(lags[0:max], corr[0:max], label="uncorrected", c="#d41159", marker="x")
plt.scatter(lags[highest_corr_indx], corr[highest_corr_indx], label="synchronization point", edgecolors="black", marker="o", s=200, facecolors="none")
plt.axhline(-thresh, ls="--")
plt.axhline(thresh, ls="--")
plt.title(rx_result)
plt.legend()
plt.show()