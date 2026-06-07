##
# Project Spreads
#
# @file
# @version 0.1

# Targets: gen, gen-vhd, sim, wave, syn, prog, gui
# Usage: make <target>


MILL = ./mill
MODULE = spreads

gen:
	$(MILL) $(MODULE).generateVerilog

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
	@echo "Targets: gen, gen-vhd, sim, wave, simwave, syn, prog, gui, clean"

.PHONY: gen gen-vhd sim wave syn prog gui help clean

# end
