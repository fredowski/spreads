##
# Project Spreads
#
# @file
# @version 0.1

# Targets: gen, gen-vhd, sim, wave, syn, prog, gui
# Usage: make <target>


MILL = ./mill
MODULE = spreads
ARGS = $(filter-out $@,$(MAKECMDGOALS))

gen:
	@if [ "$(ARGS)" = "tx" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel Transmitter_Analog; \
	elif [ "$(ARGS)" = "rx" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel Receiver_Analog; \
	elif [ "$(ARGS)" = "channel" ]; then \
		$(MILL) $(MODULE).generateVerilog --topLevel Channel; \
	elif [ -n "$(ARGS)" ]; then \
		echo "Error: Unknown argument '$(ARGS)'"; exit 1; \
	else \
		$(MILL) $(MODULE).generateVerilog; \
	fi

gen-vhd:
	$(MILL) $(MODULE).generateVhdl

sim:
	$(MILL) $(MODULE).sim

wave:
	$(MILL) $(MODULE).simwave

simwave:
	$(MILL) $(MODULE).sim 
	$(MILL) $(MODULE).simwave

syn:
	@test -f src/sim_output/SpreadSpectrumTopAnalog/rtl/SpreadSpectrumTopAnalog.v || $(MAKE) gen
	$(MILL) $(MODULE).synthesis

prog:
	$(MILL) $(MODULE).prog

gui:
	$(MILL) $(MODULE).quartusgui

clean:
	$(MILL) clean

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

.PHONY: gen tx rx channel gen-vhd sim wave simwave syn prog gui help clean


# Catch all 
%:
	@:

# end
