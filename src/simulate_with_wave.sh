#!/bin/bash
set -e

sbt "runMain ReceiverSim"
gtkwave sim_output/Receiver/test/wave.vcd
