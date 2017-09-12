## Bristol SPDZ

First install Bristol SPDZ by running `install_bristol.sh`. This should work on any Ubuntu flavored machine.

Then do the following in order to run the example program `avg.mpc`.

0. Run `export LD_LIBRARY_PATH=/usr/local/lib`.

1. Compile the program.
   `./SPDZ-2/compile.py avg.mpc`

2. Prepare the hostnames or IP addresses of the players in the `HOSTS` file.
   
3. Prepare the offline data using MASCOT.
   In two separate terminals on host1 and host2 run:
   `./SPDZ-2/ot-offline.x -p 0`
   and
   `./SPDZ-2/ot-offline.x -p 1`

4. Choose the input values of the program.
   `./prepare_values.sh <int> <int>`

5. Start the server on host1.
   `./SPDZ-2/Server.x 2 5000 &`

6. In two separate terminals on host1 and host2 run:
   `./SPDZ-2/Player-Online.x -lg2 128 -pn 5000 -h host1 0 avg`
   and
   `./SPDZ-2/Player-Online.x -lg2 128 -pn 5000 -h host1 1 avg`

Done.

