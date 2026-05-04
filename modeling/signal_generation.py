import random

def run_once(chips=25, noise_std=2.0, timing_offset=0):
    
    preamble        = [1, 1, -1, 1]
    message_bits    = [1, -1, 1, 1, -1]
    spreading_code  = [random.choice([-1, 1]) for rand in range(chips)]

    transmitted_signal = []
    for bit in preamble + message_bits:
        for chip in spreading_code:
            transmitted_signal.append(bit * chip)

    received_signal = [chip + random.gauss(0, noise_std) for chip in transmitted_signal]
    #print("tx bit0: " + str(transmitted_signal[0:chips]))
    #print("rx bit0: " + str(received_signal[0:chips]))

    offset_signal = [random.gauss(0, noise_std) for _ in range(timing_offset)] + received_signal

    preamble_chips = []
    for bit in preamble:
        for chip in spreading_code:
            preamble_chips.append(bit * chip)

    best_score  = -1
    best_offset = 0
    search_window = len(offset_signal) - len(preamble_chips)
    for candidate_offset in range(search_window):
        score = 0
        for j in range(len(preamble_chips)):
            score += offset_signal[candidate_offset + j] * preamble_chips[j]
        if score > best_score:
            best_score  = score
            best_offset = candidate_offset

    message_start = best_offset + len(preamble_chips)
    decoded_bits = []
    for i in range(len(message_bits)):
        start       = message_start + i * chips
        end         = start + chips
        current_bit = offset_signal[start : end]
        if len(current_bit) < chips:
            decoded_bits.append(0)
            continue
        correlation_score = 0
        for j in range(chips):
            correlation_score += current_bit[j] * spreading_code[j]
        decoded_bits.append(1 if correlation_score > 0 else -1)

    #print(f"SENT:     {message_bits}")
    #print(f"RECEIVED: {decoded_bits}")

    errors = 0
    for sent_bit, received_bit in zip(message_bits, decoded_bits):
        if sent_bit != received_bit:
            errors += 1
    return errors / len(message_bits)

for chips in [5, 20, 50, 100]:
    print(f"\nCurrent chip length: {chips}")
    for noise_std in [0.5, 1, 2, 4]:        
        runs = 1000 
        bit_error_rate = sum(run_once(chips, noise_std) for _ in range(runs)) / runs
        print(f"chips={chips:4d}  noise={noise_std}  BIT ERROR RATE={bit_error_rate:.3f}")
