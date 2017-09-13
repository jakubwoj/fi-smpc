## Bristol SPDZ

First install Bristol SPDZ by running `install_bristol.sh`. This should work on any Ubuntu flavored machine.

Then do the following in order to run the example program `avg.mpc`.

0. Run `export LD_LIBRARY_PATH=/usr/local/lib`.

1. Compile the program.
   `./SPDZ-2/compile.py -p 128 -g 128 avg.mpc`

2. Prepare offline data.
   `./SPDZ-2/Scripts/setup-online.sh 2 128 128`

3. Prepare the hostnames or IP addresses of the players in the `HOSTS` file.
   
4. Run MASCOT. In two separate terminals on host1 and host2 run:
   `./SPDZ-2/ot-offline.x -p 0`
   and
   `./SPDZ-2/ot-offline.x -p 1`

5. Choose the input values of the program.
   `./prepare_values.sh <int> <int>`

6. Start the server on host1.
   `./SPDZ-2/Server.x 2 5000 &`

7. In two separate terminals on host1 and host2 run:
   `./SPDZ-2/Player-Online.x -lg2 128 -pn 5000 -h host1 0 avg`
   and
   `./SPDZ-2/Player-Online.x -lg2 128 -pn 5000 -h host1 1 avg`

Done.

