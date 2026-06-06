# Spread Spectrum SpinalHDL Project

This is a student spread spectrum SpinalHDL project.
(When the project is further advanced, we should add some more project description here)
This README will go over how to setup and use the project, the SpinalHDL setup will not be addressed by this README.

# SpinalHDL setup

For simulation, installing GTKWave and Verilator is advised/needed. Verialtor also depends on a modern JDK. Choose your package manager and install the depedencies:

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

# Building & Simulation

## Mill

Mill is already setup for this repository with the `millw` wrapper. You do not have to install it locally.

### Using Mill

```sh
cd src
./mill spreads.__
```
In this case, `spreads` is our project name. Then follow with the task you want to perform, for example:

```sh
./mill spreads.generateVerilog
```

### Mill Auto-completion

Ut is advised to install Mills auto-completion features. 
Use this command and then reload your shell:
`./mill mill.tabcomplete/install`


### All Mill Tasks listed

---

This is a list of all possible Mill tasks for this project

#### Build tasks

All tasks are run from the repo root as `./mill spreads.<task>`.

| Task | Description |
|---|---|
| `./mill spreads.generateVhdl` | Compile SpinalHDL and generate `spreads.vhd` to `src/sim_output/` |
| `./mill spreads.generateVerilog` | Compile SpinalHDL and generate `spreads.v` to `src/sim_output/` |

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

## Using script automation

For ease of use a simple script has been added that combines the commands. 
Use:

``` sh
cd src
./simulate_with_wave.sh
```

 
