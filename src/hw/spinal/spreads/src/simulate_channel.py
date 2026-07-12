import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm

class UnrollLFSR:
    def __init__(self, size, taps, steps, n_out=14):
        self.size = size
        self.mask = (1 << size) - 1
        self.taps = taps
        self.steps = steps
        self.n_out = n_out
        self.out_mask = (1 << n_out) - 1
        
        # Initialize with all 1s to match: B(m_lfsr bits, default -> True)
        self.state = self.mask

    def step(self):
        # XOR the tapped bits to calculate the feedback
        fb = 0
        for tap in self.taps:
            fb ^= (self.state >> tap) & 1
            
        # Shift left and insert the new feedback bit at the LSB (index 0)
        self.state = ((self.state << 1) | fb) & self.mask

    def get_output(self):
        # Advance the LFSR by the designated number of steps per cycle
        for _ in range(self.steps):
            self.step()
        
        # Extract the lower 14 bits
        val = self.state & self.out_mask
        
        # Treat as two's complement signed integer
        if val & (1 << (self.n_out - 1)):
            val -= (1 << self.n_out)
            
        return val

# 1. Initialize the 5 LFSRs exactly as defined in the SpinalHDL Channel()
lfsr0 = UnrollLFSR(size=30, taps=[29, 22, 1, 0], steps=14)
lfsr1 = UnrollLFSR(size=31, taps=[30, 26, 22, 18, 14, 10, 9, 8, 6, 5, 4, 2, 1, 0], steps=1)
lfsr2 = UnrollLFSR(size=32, taps=[31, 21, 1, 0], steps=2)
lfsr3 = UnrollLFSR(size=32, taps=[31, 21, 20, 19, 17, 16, 14, 12, 11, 9, 7, 5, 3, 0], steps=3)
lfsr4 = UnrollLFSR(size=32, taps=[31, 27, 18, 17, 15, 13, 10, 9, 8, 5, 4, 0], steps=1)

# 2. Simulate 100,000 clock cycles
num_samples = 100_000
noise_samples = np.zeros(num_samples, dtype=np.int32)

for i in range(num_samples):
    # Sum the signed outputs
    X = lfsr0.get_output() + lfsr1.get_output() + lfsr2.get_output() + lfsr3.get_output() + lfsr4.get_output()
    
    # Hardware truncation: X(13 downto 0)
    trunc_X = X & 0x3FFF
    
    # Re-evaluate the truncated 14-bit value as a signed integer
    if trunc_X & 0x2000:
        trunc_X -= 0x4000
        
    noise_samples[i] = trunc_X

# 3. Plot the Histogram (PDF) against an Ideal Gaussian
plt.figure(figsize=(10, 6))

# Plot normalized histogram of our hardware simulation
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

# Fit an ideal normal distribution to the data
mu, std = norm.fit(noise_samples)
pdf = norm.pdf(bins, mu, std)

# Plot the ideal Gaussian curve
plt.plot(bins, pdf, linewidth=2.5, color='black', label=f'Ideal Gaussian\n($\mu \simeq {mu:.1f}, \sigma \simeq {std:.1f}$)')

# Formatting
plt.title("Probability Density Function: Channel Noise Simulation", pad=15)
plt.xlabel("Amplitude (14-bit Signed Integer)")
plt.ylabel("Probability Density")
plt.legend(loc="upper right")
plt.grid(True, alpha=0.3, linestyle='--')
plt.tight_layout()

plt.show()
