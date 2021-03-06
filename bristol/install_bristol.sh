#!/bin/bash -e

# Install g++ to compile Bristol SPDZ
sudo apt-get install -y g++

# Install m4 to install MPIR
sudo apt-get install -y m4

# Install unzip because the software comes zipped
sudo apt-get install -y unzip


# Installing MPIR
wget http://mpir.org/mpir-2.7.2.zip

unzip mpir-2.7.2.zip
cd mpir-2.7.2

./configure --enable-cxx
make
sudo make install


cd ..


# Installing Bristol SPDZ
wget https://github.com/bristolcrypto/SPDZ-2/archive/master.zip

unzip master.zip
mv SPDZ-2-master SPDZ-2
cd SPDZ-2

sed -i "s/\(USE_GF2N_LONG *= *\).*/\11/" ./CONFIG

wget https://github.com/pascholl/SimpleOT/archive/52b43a250922fb45f0bfff73ba2f9b9a11c1784c.zip
unzip 52b43a250922fb45f0bfff73ba2f9b9a11c1784c.zip

rm -R SimpleOT
mv SimpleOT-52b43a250922fb45f0bfff73ba2f9b9a11c1784c SimpleOT
cd SimpleOT

make

cd ..

make

