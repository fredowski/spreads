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

The `modeling` directory contains all our python3 simulations and testing scripts. 

in `src/spinal/spreads/src` or `src/spinal/spreads/sim` the source and simulation scala files can be found. 

In the following section, we will explain how to start a simple first simulation.

# Simulation 

## Command line way
Using sbt, we can create Verilog from SpinalHDL and simulate the receiver.
Verilator will then create a [Value Change Dump](https://en.wikipedia.org/wiki/Value_change_dump) file (VCD).

To do this run:

``` sh
cd src
sbt "runMain TopLevelSimAnalog"
```
Then view the waveform with:

``` shg
gtkwave sim_output/SpreadSpectrumTopAnalog/test/wave.vcd -o
```

## Using script automation

For ease of use a simple script has been added that combines the commands. 
Use:

``` sh
cd src
./simulate_with_wave.sh
```

 
