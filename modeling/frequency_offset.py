import numpy as np
import matplotlib.pyplot as plt

# ─── PARAMETERS ──────────────────────────────────────────────
n_bits    = 20    # fewer bits → less drift
chip_rate = 8
snr_db    = 10

# ─── PRNS GENERATOR ──────────────────────────────────────────
def make_prns(length):
    state = 0xFF
    seq   = []
    for _ in range(length):
        bit      = (state >> 7) & 1
        feedback = ((state >> 7) ^ (state >> 5) ^
                   (state >> 4) ^ (state >> 3)) & 1
        state    = ((state << 1) | feedback) & 0xFF
        seq.append(1 if bit else -1)
    return np.array(seq)

# ─── TRANSMITTER ─────────────────────────────────────────────
np.random.seed(42)
n_chips  = n_bits * chip_rate
data     = np.random.choice([-1, 1], size=n_bits)
prns     = make_prns(n_chips)
spread   = np.repeat(data, chip_rate) * prns

# ─── ADD NOISE ───────────────────────────────────────────────
power    = np.mean(spread ** 2)
noise    = np.random.normal(0, np.sqrt(power / 10**(snr_db/10)), n_chips)
received = spread + noise

# ─── TEST DIFFERENT OFFSETS ──────────────────────────────────
offsets = [0.0, 0.01, 0.02, 0.05, 0.10, 0.20]
results = {}

for offset in offsets:
    t        = np.arange(n_chips)
    rotated  = received * np.cos(2 * np.pi * offset * t / chip_rate)
    despread = rotated * prns
    errors   = 0
    for i in range(n_bits):
        chunk = despread[i*chip_rate:(i+1)*chip_rate]
        bit   = 1 if np.sum(chunk) > 0 else -1
        if bit != data[i]:
            errors += 1
    ber = errors / n_bits
    results[offset] = ber
    print(f"Offset = {offset:.2f} → BER = {ber:.3f} → "
          f"{'✅ good' if ber < 0.05 else '⚠️ bad' if ber < 0.2 else '❌ failed'}")

# ─── PLOT ────────────────────────────────────────────────────
plt.figure(figsize=(8, 5))
plt.bar([str(o) for o in offsets],
        [results[o] for o in offsets],
        color=['green' if results[o] < 0.05
               else 'orange' if results[o] < 0.2
               else 'red' for o in offsets])
plt.axhline(y=0.05, color='black', linestyle='--', label='5% BER threshold')
plt.title('BER vs Frequency Offset')
plt.xlabel('Frequency Offset (fraction of chip rate)')
plt.ylabel('Bit Error Rate')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()
