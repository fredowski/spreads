import scipy as sp
import numpy as np
import matplotlib.pyplot as plt
import pylfsr as pyl
from pylfsr import LFSR
import math
import pandas as pd

from helper import bytesToPlusMinus, code_acquisition, decode, ber

rng = np.random.default_rng()

#binary noise:
#Var(n) = N²

#AWGN: Variance PER SYMBOL N_0/2 = N²   


#T = 1/(2W)
#Energy per symbol S²/(2W) for signal, N²/(2W) for binary antipodal noise

repeats = 1
transmitted_bytes = 100
lfsr_size_max = 10
snrs_db = np.array([0, -3, -6, -10, -13, -16, -20, -23, -26, -30, -33, -36, -40])

S = math.floor(2**13 * (10**(0.05*(-40))))
results = {"awgn" : {}, "bn" : {}}
for i in range(repeats):
    bits = (rng.integers(0,2,transmitted_bytes*8)*2-1)
    data =  (bits*S).astype(np.int64)
    for m in [10]: #range(4,lfsr_size_max+1):
        print("LFSR size: " + str(m))
        fpoly = pyl.get_fpolyList(m=m)[0]
        L = LFSR(fpoly=fpoly)
        code = (L.getFullPeriod() * 2 - 1).astype(np.int64)
        for snr in snrs_db:
            N = S / (10**(0.05 * snr)) #voltage
            res_awgn = []
            res_bn = []
            for offset in range(0,len(code),10):
                padding = ((rng.integers(0,2,offset)*2-1)*S).astype(np.int64)
                tx_code = code #np.roll(code,offset)
                tx_modulated = np.outer(data, tx_code).flatten()
                tx_sig = np.concat((padding, tx_modulated))

                awgn = rng.normal(size=len(tx_sig), scale=N)
                awgn_int = np.clip(awgn, -2**13, 2**13-1,).round().astype(np.int64)
                binary_noise = ((rng.integers(0,2,len(tx_sig))*2-1)*N).round().astype(np.int64)

                tx_sig_awgn = tx_sig + awgn
                tx_sig_bn = tx_sig + binary_noise
                offset_awgn = code_acquisition(tx_sig_awgn, code)
                offset_bn = code_acquisition(tx_sig_bn, code)
                data_awgn = decode(tx_sig_awgn, code, offset)[:len(data)]
                data_bn = decode(tx_sig_bn, code, offset)[:len(data)]
                ber_awgn = ber(bits, data_awgn)
                ber_bn = ber(bits, data_bn)
                res_awgn.append(ber_awgn)
                res_bn.append(ber_bn)
                print("AWGN SNR        : " + str(snr) + "; offset: " + str(offset) + "; detected offset: " + str(offset_awgn) + "; BER: " + str(ber_awgn))
                print("Binary Noise SNR: " + str(snr) + "; offset: " + str(offset) + "; detected offset: " + str(offset_bn) + "; BER: " + str(ber_bn))
            results["awgn"][snr] = res_awgn
            results["bn"][snr] = res_bn

fig, axs = plt.subplots(2)
fig.suptitle("BER vs SNR")
axs[0].set_title("AWGN")
for snr in results["awgn"].keys():
    axs[0].scatter([snr] * len(results["awgn"][snr]), results["awgn"][snr])

axs[1].set_title("Binary Noise")
for snr in results["bn"].keys():
    axs[1].scatter([snr] * len(results["bn"][snr]), results["bn"][snr])

for ax in axs.flat:
    ax.set(xlabel='SNR[dB]', ylabel='BER')
plt.show()

