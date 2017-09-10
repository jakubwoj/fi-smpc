## Bristol SPDZ

First install Bristol SPDZ by running `install_bristol.sh`. This should work on any Ubuntu flavored machine.

Then do the following in order to run the example program `avg.mpc`.

1. Compile the program.
   `./SPDZ-2/compile.py avg.mpc`
   
2. Prepare offline data.
   `LD_LIBRARY_PATH=/usr/local/lib ./SPDZ-2/Scripts/setup-online.sh`

3. Choose the input values of the program.
   `./prepare_values.sh <int> <int>`

4. Start the server.
   ` LD_LIBRARY_PATH=/usr/local/lib ./SPDZ-2/Server.x 2 5000 &`

5. In two separate terminals run
   `LD_LIBRARY_PATH=/usr/local/lib ./SPDZ-2/Player-Online.x -pn 5000 0 avg`
   and
   `LD_LIBRARY_PATH=/usr/local/lib ./SPDZ-2/Player-Online.x -pn 5000 1 avg`

Done.

