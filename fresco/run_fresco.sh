#!/bin/bash

case $(uname -n) in
   alpha)
      PLAYER_ID=1
      PARAMETER="00112233445566778899aabbccddeeff"
      ;;
   beta)
      PLAYER_ID=2
      PARAMETER="000102030405060708090a0b0c0d0e0f"
      ;;
   gamma)
      PLAYER_ID=3
      ;;
   delta)
      PLAYER_ID=4
      ;;
esac

echo $PLAYER_ID

#java -cp .:fresco-0.1-with-dependencies.jar AESDemo -i $PLAYER_ID -s spdz -p1:192.168.33.10:9001 -p2:192.168.33.11:9002 $PARAMETER

java -cp .:fresco-0.2-SNAPSHOT-jar-with-dependencies.jar SimpleAverageApplication -i $PLAYER_ID -s spdz -Dbgw.threshold=1 -Dspdz.preprocessingStrategy=DUMMY -p 1:192.168.33.10:9001 -p 2:192.168.33.11:9002 -p 3:192.168.33.12:9003 -p 4:192.168.33.13:9004
