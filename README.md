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

# Simulation 

## Command line way

### Mill

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

#### Mill Auto-completion

Ut is advised to install Mills auto-completion features. 
Use this command and then reload your shell:
`./mill mill.tabcomplete/install`


## Using script automation

For ease of use a simple script has been added that combines the commands. 
Use:

``` sh
cd src
./simulate_with_wave.sh
```

 
