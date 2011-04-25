#!/bin/sh
export SKETCHBOOK=/home/dvanhumb/.config/createSimulator/sketches
java create.simulator.window.MainLauncher -s=$SKETCHBOOK -l$0
