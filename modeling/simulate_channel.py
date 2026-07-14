import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm

class UnrollLFSR:
    def __init__(self, size, taps, steps, n_out=12):
        self.size = size
        self.mask = (1 << size) - 1
        self.taps = taps
        self.steps = steps
        self.n_out = n_out
        self.out_mask = (1 << n_out) - 1
        
        self.state = self.mask

    def step(self):
        fb = 0
        for tap in self.taps:
            fb ^= (self.state >> tap) & 1
            
        self.state = ((self.state << 1) | fb) & self.mask

    def get_output(self):
        for _ in range(self.steps):
            self.step()
        
        val = self.state & self.out_mask
        
        if val & (1 << (self.n_out - 1)):
            val -= (1 << self.n_out)
            
        return val

lfsr0 = UnrollLFSR(size=30, taps=[29, 22, 1, 0], steps=12)
lfsr1 = UnrollLFSR(size=31, taps=[30, 26, 22, 18, 14, 10, 9, 8, 6, 5, 4, 2, 1, 0], steps=10) #3
lfsr2 = UnrollLFSR(size=32, taps=[31, 21, 1, 0], steps=8) #3
lfsr3 = UnrollLFSR(size=32, taps=[31, 21, 20, 19, 17, 16, 14, 12, 11, 9, 7, 5, 3, 0], steps=6) #1
lfsr4 = UnrollLFSR(size=32, taps=[31, 27, 18, 17, 15, 13, 10, 9, 8, 5, 4, 0], steps=7) #5

num_samples = 100_000
noise_samples = np.zeros(num_samples)

for i in range(num_samples):
    X = lfsr0.get_output() + lfsr1.get_output() + lfsr2.get_output() + lfsr3.get_output() #+ lfsr4.get_output()
    trunc_X = np.clip(X, -2**13, 2**13-1,).round()
    
    # trunc_X = X & 0x3FFF
    

    # if trunc_X & 0x2000:
    #     trunc_X -= 0x4000
        
    noise_samples[i] = trunc_X / (2**13-1)


plt.figure(figsize=(10, 6))
count, bins, ignored = plt.hist(
    noise_samples, 
    bins=100, 
    density=True, 
    alpha=0.75, 
    color='tab:orange', 
    edgecolor='black',
    linewidth=0.5,
    label='FPGA Hardware Approximation'
)

mu, std = norm.fit(noise_samples)
pdf = norm.pdf(bins, mu, std)

plt.plot(bins, pdf, linewidth=2.5, color='black', label=f'Ideal Gaussian\n($\mu \simeq {mu:.1f}, \sigma \simeq {std:.1f}$)')

plt.title("Probability Density Function: Channel Noise Simulation", pad=15)
plt.xlabel("Amplitude (14-bit Signed Integer)")
plt.ylabel("Probability Density")
plt.legend(loc="upper right")
plt.grid(True, alpha=0.3, linestyle='--')
plt.tight_layout()

# plt.show()

# plt.figure()
# sp = np.fft.fft(noise_samples)
# freq = np.fft.fftfreq(noise_samples.shape[-1])

# plt.plot(freq,np.abs(sp))
# plt.xlabel("frequency")
# plt.ylabel("fft(x)")
plt.show()