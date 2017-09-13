#!/bin/bash

case $(uname -n) in
   alpha)
      PLAYER_ID=1
      INPUT="$1"
      ;;
   beta)
      PLAYER_ID=2
      INPUT="$2"
      ;;
   gamma)
      PLAYER_ID=3
      INPUT="$3"
      ;;
   delta)
      PLAYER_ID=4
      INPUT="$4"
      ;;
esac

echo $PLAYER_ID

java -cp  .:fresco-0.2-SNAPSHOT-jar-with-dependencies.jar Average \
   -s bgw -Dbgw.threshold=1 \
   -p 1:192.168.33.10:9001 -p 2:192.168.33.11:9002 -p 3:192.168.33.12:9003 -p 4:192.168.33.13:9004 \
   -i $PLAYER_ID -in $INPUT
