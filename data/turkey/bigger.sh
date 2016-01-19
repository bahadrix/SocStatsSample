#!/bin/sh

for i in $(seq 1 $1); do cat all.csv >> big.csv; done
