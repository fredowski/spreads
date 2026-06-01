import numpy as np
import matplotlib.pyplot as plt

# ─── 1. PARAMETERS ───────────────────────────────────────────
data_bits   = [1, 0, 1, 1, 0, 1, 0, 0]  # message to send
lfsr_taps   = [8, 6, 5, 4]              # 8-bit LFSR tap positions
chip_rate   = 8                          # chips per data bit (spreading factor)

# ─── 2. LFSR / PRNS GENERATOR ────────────────────────────────
def generate_prns(taps, length, init=0xFF):
    state = init
    seq = []
    size = max(taps)
    for _ in range(length):
        # output the MSB as +1 or -1
        bit = (state >> (size - 1)) & 1
        seq.append(1 if bit == 1 else -1)
        # compute feedback
        feedback = 0
        for t in taps:
            feedback ^= (state >> (size - t)) & 1
        state = ((state << 1) | feedback) & ((1 << size) - 1)
    return np.array(seq)

# ─── 3. GENERATE SIGNALS ─────────────────────────────────────
n_chips = len(data_bits) * chip_rate

# PRNS sequence (one chip per step)
prns = generate_prns(lfsr_taps, n_chips)

# Data signal — each bit repeated chip_rate times, mapped to +1/-1
data_spread = np.repeat([1 if b == 1 else -1 for b in data_bits], chip_rate)

# Spread signal = data × PRNS
spread_signal = data_spread * prns

# ─── 4. PLOT ─────────────────────────────────────────────────
fig, axes = plt.subplots(3, 1, figsize=(12, 6), sharex=True)

axes[0].step(range(n_chips), data_spread, where='post', color='blue')
axes[0].set_title('Data Signal (repeated per chip)')
axes[0].set_ylim(-1.5, 1.5)
axes[0].grid(True)

axes[1].step(range(n_chips), prns, where='post', color='orange')
axes[1].set_title('PRNS Sequence')
axes[1].set_ylim(-1.5, 1.5)
axes[1].grid(True)

axes[2].step(range(n_chips), spread_signal, where='post', color='green')
axes[2].set_title('Spread Signal (Data × PRNS)')
axes[2].set_ylim(-1.5, 1.5)
axes[2].grid(True)

plt.xlabel('Chip index')
plt.tight_layout()
plt.show()
