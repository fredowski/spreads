#!/bin/bash
set -e

sbt "runMain TopLevelSimAnalog"
gtkwave sim_output/SpreadSpectrumTopAnalog/test/wave.vcd -o
