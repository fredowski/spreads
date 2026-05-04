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

# Simulation 

## Command line way
Using sbt, we can create Verilog from SpinalHDL and simulate the receiver.
Verilator will then create a [Value Change Dump](https://en.wikipedia.org/wiki/Value_change_dump) file (VCD).

To do this run:

``` sh
cd src
sbt "runMain ReceiverSim"
```
Then view the waveform with:

``` sh
gtkwave src/sim_output/DSSSReceiver/test/wave.vcd
```

## Using script automation

For ease of use a simple script has been added that combines the commands. 
Use:

``` sh
cd src
./simulate_with_wave.sh
```

 
