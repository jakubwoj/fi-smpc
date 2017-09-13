## FRESCO
[A **FR**amework for **E**fficient **S**ecure **CO**mputation](https://github.com/aicis/fresco)

Do the following in order to run the example program `Average.java`.

1. Compile the program. You need Java 8.
   FRESCO and all other dependencies are checked in as a JAR.
   ```
   javac -cp fresco-0.2-SNAPSHOT-jar-with-dependencies.jar Average.java
   ```

2. Start all four Vagrant VMs by running `greek.sh`.

3. Run `run_average.sh` in all four VMs.
   The script will distribute the values to the right players automatically.
   ```
   ./run_average.sh <int> <int> <int> <int>
   ```

Done.
