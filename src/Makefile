##
# Project Spreads
#
# @file
# @version 0.1

# Targets: gen, gen-vhd, sim, wave, syn, prog, gui
# Usage: make <target>

MILL = ./mill
MODULE = spreads
TARGETS = gen gen-vhd sim wave simwave syn prog gui clean help
ARGS = $(filter-out $(TARGETS),$(MAKECMDGOALS))

# Stamp variables to track completion per argument
STAMP_SUFFIX = $(if $(ARGS),$(ARGS),default)
GEN_STAMP = .gen_$(STAMP_SUFFIX)
SYN_STAMP = .syn_$(STAMP_SUFFIX)

# Track all Scala files (and build.sc) to know when code actually changes
SRC_FILES = $(shell find . -type f -name '*.scala' 2>/dev/null)

gen: $(GEN_STAMP)

$(GEN_STAMP): $(SRC_FILES)
	@if [ "$(ARGS)" = "tx" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel tx_top; \
	elif [ "$(ARGS)" = "rx" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel rx_top; \
	elif [ "$(ARGS)" = "channel" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel channel_top; \
	elif [ "$(ARGS)" = "adc" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel de1_adc; \
	elif [ -n "$(ARGS)" ]; then \
		echo "Error: Unknown argument '$(ARGS)'"; exit 1; \
	else \
		$(MILL) $(MODULE).generateVerilog; \
	fi
	@touch $@

gen-vhd:
	$(MILL) $(MODULE).generateVhdl

sim:
	$(MILL) $(MODULE).sim

wave:
	$(MILL) $(MODULE).simwave

simwave:
	$(MILL) $(MODULE).sim 
	$(MILL) $(MODULE).simwave

syn: $(SYN_STAMP)

$(SYN_STAMP): $(GEN_STAMP)
	@if [ "$(ARGS)" = "tx" ]; then \
		$(MILL) $(MODULE).synthesis --topLevel tx_top; \
	elif [ "$(ARGS)" = "rx" ]; then \
		$(MILL) $(MODULE).synthesis --topLevel rx_top; \
	elif [ "$(ARGS)" = "channel" ]; then \
		$(MILL) $(MODULE).synthesis --topLevel channel_top; \
	elif [ "$(ARGS)" = "adc" ]; then \
		$(MILL) $(MODULE).synthesis --topLevel de1_adc; \
	elif [ -n "$(ARGS)" ]; then \
		echo "Error: Unknown argument '$(ARGS)'"; exit 1; \
	else \
		$(MILL) $(MODULE).synthesis; \
	fi
	@touch $@

prog: $(SYN_STAMP)
	@if [ "$(ARGS)" = "tx" ]; then \
		$(MILL) $(MODULE).prog --topLevel tx_top; \
	elif [ "$(ARGS)" = "rx" ]; then \
		$(MILL) $(MODULE).prog --topLevel rx_top; \
	elif [ "$(ARGS)" = "channel" ]; then \
		$(MILL) $(MODULE).prog --topLevel channel_top; \
	elif [ "$(ARGS)" = "adc" ]; then \
		$(MILL) $(MODULE).prog --topLevel de1_adc; \
	elif [ -n "$(ARGS)" ]; then \
		echo "Error: Unknown argument '$(ARGS)'"; exit 1; \
	else \
		$(MILL) $(MODULE).prog; \
	fi

gui:
	$(MILL) $(MODULE).quartusgui

clean:
	$(MILL) clean
	rm -f .gen_* .syn_*

help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Generation Targets:"
	@echo "  gen       - Generate Verilog (default top level)"
	@echo "  tx        - Generate Verilog for Transmitter_Analog"
	@echo "  rx        - Generate Verilog for Receiver_Analog"
	@echo "  channel   - Generate Verilog for Channel"
	@echo "  gen-vhd   - Generate VHDL"
	@echo "  syn       - Run synthesis"
	@echo ""
	@echo "Simulation & Hardware:"
	@echo "  sim       - Run simulation"
	@echo "  wave      - View simulation waves"
	@echo "  simwave   - Run simulation and view waves"
	@echo "  prog      - Program the FPGA"
	@echo "  gui       - Open Quartus GUI"
	@echo "  clean     - Clean build files"

# Removed tx, rx, channel from .PHONY so the catch-all silences them
.PHONY: gen gen-vhd sim wave simwave syn prog gui help clean

# Catch all 
%:
	@:

# end
