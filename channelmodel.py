import numpy as np
import matplotlib.pyplot as plt

# ─── 1. PARAMETERS ───────────────────────────────────────────
data_bits   = [1, 0, 1, 1, 0, 1, 0, 0]
lfsr_taps   = [8, 6, 5, 4]
chip_rate   = 8
SNR_dB      = 3  # Signal to Noise Ratio in decibels — try changing this!

# ─── 2. LFSR / PRNS GENERATOR ────────────────────────────────
def generate_prns(taps, length, init=0xFF):
    state = init
    seq = []
    size = max(taps)
    for _ in range(length):
        bit = (state >> (size - 1)) & 1
        seq.append(1 if bit == 1 else -1)
        feedback = 0
        for t in taps:
            feedback ^= (state >> (size - t)) & 1
        state = ((state << 1) | feedback) & ((1 << size) - 1)
    return np.array(seq)

# ─── 3. GENERATE SPREAD SIGNAL ───────────────────────────────
n_chips = len(data_bits) * chip_rate
prns = generate_prns(lfsr_taps, n_chips)
data_spread = np.repeat([1 if b == 1 else -1 for b in data_bits], chip_rate)
spread_signal = data_spread * prns

# ─── 4. ADD NOISE (AWGN CHANNEL) ─────────────────────────────
signal_power = np.mean(spread_signal ** 2)
SNR_linear   = 10 ** (SNR_dB / 10)
noise_power  = signal_power / SNR_linear
noise        = np.random.normal(0, np.sqrt(noise_power), n_chips)

received_signal = spread_signal + noise

# ─── 5. PLOT ─────────────────────────────────────────────────
fig, axes = plt.subplots(3, 1, figsize=(12, 6), sharex=True)

axes[0].step(range(n_chips), spread_signal, where='post', color='green')
axes[0].set_title('Transmitted Spread Signal (no noise)')
axes[0].set_ylim(-3, 3)
axes[0].grid(True)

axes[1].plot(range(n_chips), noise, color='red', linewidth=0.8)
axes[1].set_title(f'AWGN Noise (SNR = {SNR_dB} dB)')
axes[1].set_ylim(-3, 3)
axes[1].grid(True)

axes[2].plot(range(n_chips), received_signal, color='purple', linewidth=0.8)
axes[2].set_title('Received Signal (spread + noise)')
axes[2].set_ylim(-3, 3)
axes[2].grid(True)

plt.xlabel('Chip index')
plt.tight_layout()
plt.show()
