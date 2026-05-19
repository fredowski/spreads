import scipy as sp
import numpy as np
import matplotlib.pyplot as plt
import pylfsr as pyl
from pylfsr import LFSR
import math
import pandas as pd

from helper import bytesToPlusMinus, code_acquisition, decode, ber

rng = np.random.default_rng()

#binary noise: A(noise) = A(signal)
#Var(n) = A²

repeats = 1
transmitted_bytes = 1000
lfsr_size_max = 10
snrs_db = np.array([0, -3, -6, -10, -13, -16, -20, -23, -26, -30, -33, -36, -40])

results = {"awgn" : {}, "bn" : {}}
for i in range(repeats):
    data =  bytesToPlusMinus(rng.bytes(transmitted_bytes))
    for m in [10]: #range(4,lfsr_size_max+1):
        print("LFSR size: " + str(m))
        fpoly = pyl.get_fpolyList(m=m)[0]
        L = LFSR(fpoly=fpoly)
        code = L.getFullPeriod() * 2 - 1
        for snr in snrs_db:
            A = 1 / math.sqrt(10**(0.1 * snr))
            res_awgn = []
            res_bn = []
            for offset in range(0,len(code),10):
                padding = rng.integers(0,2,offset)*2-1
                tx_code = code #np.roll(code,offset)
                tx_modulated = np.outer(data, tx_code).flatten()
                tx_sig = np.concat((padding, tx_modulated))

                awgn = rng.normal(size=len(tx_sig), scale=A)
                binary_noise = (rng.integers(0,2,len(tx_sig))*2-1) * A

                tx_sig_awgn = tx_sig + awgn
                tx_sig_bn = tx_sig + binary_noise
                offset_awgn = code_acquisition(tx_sig_awgn, code)
                offset_bn = code_acquisition(tx_sig_bn, code)
                data_awgn = decode(tx_sig_awgn, code, offset)[:len(data)]
                data_bn = decode(tx_sig_bn, code, offset)[:len(data)]
                ber_awgn = ber(data, data_awgn)
                ber_bn = ber(data, data_bn)
                res_awgn.append(ber_awgn)
                res_bn.append(ber_bn)
                print("AWGN SNR        : " + str(snr) + "; offset: " + str(offset) + "; detected offset: " + str(offset_awgn) + "; BER: " + str(ber_awgn))
                print("Binary Noise SNR: " + str(snr) + "; offset: " + str(offset) + "; detected offset: " + str(offset_bn) + "; BER: " + str(ber_bn))
            results["awgn"][snr] = res_awgn
            results["bn"][snr] = res_bn

for snr in results["awgn"].keys():
    plt.scatter([snr] * len(results["awgn"][snr]), results["awgn"][snr])

plt.figure()
for snr in results["bn"].keys():
    plt.scatter([snr] * len(results["bn"][snr]), results["bn"][snr])
plt.show()

