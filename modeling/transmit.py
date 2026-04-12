from gps_code import PRN
from random import gauss
import scipy as sp
import numpy as np
import matplotlib.pyplot as plt
rng = np.random.default_rng()
seq = np.array(PRN(24))*2-1
sig = np.roll(np.concatenate([seq, -seq, -seq, seq]), 0) * 2 - 1
noise_amplitude = 2
noise = rng.uniform(low=-1,high=1, size=len(sig)) * noise_amplitude

noisy_sig = sig #+ noise
corr = sp.signal.correlate(in1=noisy_sig, in2=seq, mode='same') / len(PRN(24))
lags = sp.signal.correlation_lags(len(noisy_sig), len(PRN(24)), mode='same')

plt.plot(lags, corr)
plt.show()