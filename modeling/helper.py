import numpy as np
import scipy as sp
from itertools import batched
from matplotlib import pyplot as plt

def code_acquisition(signal, code, n=None):
    corr = sp.signal.correlate(in1=signal[:n], in2=code, mode='same')
    lags = sp.signal.correlation_lags(len(signal[:n]), len(code), mode='same')
    #plt.plot(lags, corr)
    #plt.show()
    offset = lags[np.argmax(np.abs(corr))] % len(code)
    return offset

def decode(signal, code, offset, n=None):
    return [None if len(batch) != len(code) else 1 if np.sum(batch * code) > 0 else -1 for batch in batched(signal[offset:], len(code))]

def ber(sent, received):
    return np.count_nonzero(sent != received) / len(sent)

def bytesToPlusMinus(B):
    data = np.array(tobits(B)).flatten()
    return data * 2 - 1

# Source - https://stackoverflow.com/a/10238140
# Posted by John Gaines Jr., modified by community. See post 'Timeline' for change history
# Retrieved 2026-04-12, License - CC BY-SA 3.0
def tobits(B):
    result = []
    for b in B:
        bits = bin(b)[2:]
        bits = '00000000'[len(bits):] + bits
        result.extend([int(b) for b in bits])
    return result

def frombits(bits):
    chars = []
    for b in range(int(len(bits) / 8)):
        byte = bits[b*8:(b+1)*8]
        chars.append(chr(int(''.join([str(bit) for bit in byte]), 2)))
    return ''.join(chars)