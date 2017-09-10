#!/bin/bash

INPUT_FILE="gfp_vals.in"
OUTPUT_FILE="gfp_vals.out"

SPDZ_DIR="./SPDZ-2"

export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH


echo 1 > $INPUT_FILE
echo $1 >> $INPUT_FILE

$SPDZ_DIR/gen_input_fp.x
cp $OUTPUT_FILE Player-Data/Private-Input-0


echo 1 > $INPUT_FILE
echo $2 >> $INPUT_FILE

$SPDZ_DIR/gen_input_fp.x
cp $OUTPUT_FILE Player-Data/Private-Input-1


rm $INPUT_FILE
rm $OUTPUT_FILE

