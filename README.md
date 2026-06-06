# Spread Spectrum SpinalHDL Project

This is a Electrical Engineerig Master student project. The goals is to achieve Spread Spectrum communication on an FPGA, using the SpinalHDL language and a DE2 Altera board. The project includes artificual noise generation to simulate realistic environments and is based on LSFRs. Currently, no FFT usage is planned.

This README will go over how to setup and use the project.

---

# SpinalHDL setup

For simulation, installing GTKWave and Verilator is advised/needed. Verilator also depends on a modern JDK. Choose your package manager and install the dependencies:

## apt
```bash
sudo apt install verilator gtkwave default-jdk
```

## pacman
```bash
sudo pacman -S verilator gtkwave jdk-openjdk
```

## brew
```bash
brew install verilator openjdk && brew install --cask gtkwave
```
## SpinalHDL

To setup SpinalHDL, it is advised to use the SpinalHDL [installation guide](https://spinalhdl.github.io/SpinalDoc-RTD/master/SpinalHDL/Getting%20Started/Install%20and%20setup.html).

--- 

# Directory structure overview

```
├── modeling
│   └── *.py
└── src
    ├── build.sbt
    ├── build.sc
    ├── hw
    │   └── spinal
    │       └── spreads
    │           ├── sim
    │           └── src
    ├── project
    ├── sim_output
    ├── simulate_with_wave.sh
    └── target
```
The `modeling` directory contains all our python3 simulations and testing scripts. 

in `src/spinal/spreads/src` or `src/spinal/spreads/sim` the source and simulation scala files can be found. 

In the following section, we will explain how to start a simple first simulation.

---

# Building & Simulation

## Build System

This project uses Mill as a build system. Mill is already setup for this repository with the `millw` wrapper. You do not have to install it locally.

## Makefile 

We try to avoid interacting with Mill directly, as its auto-completion can give lots of results, when few would suffice, and seems quite slow. 
For this, we wrote a simple `Makefile`. The Makefile lets us interact with all the most needed features. 

For a Makefile target overview, see:

| Target     | Description                                              |
|------------|----------------------------------------------------------|
| `gen`      | Generate Verilog sources from SpinalHDL                  |
| `gen-vhd`  | Generate VHDL sources from SpinalHDL                     |
| `sim`      | Run simulation, produces a VCD waveform file             |
| `wave`     | Open existing VCD waveform in GTKWave                    |
| `simwave`  | Run simulation then immediately open GTKWave             |
| `syn`      | Synthesize and place-and-route with Quartus              |
| `prog`     | Program the connected FPGA over JTAG (USB-Blaster)       |
| `gui`      | Open the project in the Quartus GUI                      |
| `clean`    | Clear all Mill build artifacts                           |

Generated sources land in `src/sim_output/SpreadSpectrumTopAnalog/rtl/*.v(hd)`, simulation waveforms in `src/sim_output/SpreadSpectrumTopAnalog/test/*.vcd`.

---

## Mill

If Mill is used instead of the Makefile, this README section should give a decent overview us Mill usage.

### Mill Auto-completion

If Mill is used instead of the Makefile, it is advised to install Mills auto-completion features. 
Use this command and then reload your shell:
`./mill mill.tabcomplete/install`


### All Mill Tasks listed


This is a list of all possible Mill tasks for this project

All tasks are run from the repo root as `./mill spreads.<task>`.

#### Generation

| Task | Description |
|---|---|
| `./mill spreads.generateVhdl` | Compile SpinalHDL and generate `spreads.vhd` to `src/sim_output/SpreadSpectrumTopAnalog/rtl/` |
| `./mill spreads.generateVerilog` | Compile SpinalHDL and generate `spreads.v` to `src/sim_output/SpreadSpectrumTopAnalog/rtl/` |

#### Simulation

| Task | Description |
|---|---|
| `./mill spreads.sim` | Run the SpinalSim `TopLevelSimAnalog` simulation, produce `wave.vcd` |
| `./mill spreads.simwave` | Run simulation and open the result in GTKWave |
| `./mill spreads.ghdlsim` | Analyse + elaborate + run the VHDL testbench with GHDL, produce `wave.vcd` |
| `./mill spreads.ghdlsimwave` | Same as above, then open the result in GTKWave |

#### Quartus

| Task | Description |
|---|---|
| `./mill spreads.qproject` | Generate a Quartus project from the VHDL output and pin/SDC files |
| `./mill spreads.synthesis` | Run a full Quartus compilation (produces `.sof`) |
| `./mill spreads.quartusgui` | Open the generated project in the Quartus GUI |
| `./mill spreads.prog` | Program the FPGA over JTAG |

---

 
