#!/bin/bash

SESSION="SecMpComp"

tmux -2 new-session -d -s $SESSION

tmux split-window -h

tmux select-pane -t 0
tmux split-window -h

tmux select-pane -t 2
tmux split-window -h

tmux select-pane -t 0
tmux send-keys "vagrant ssh alpha" C-m

tmux select-pane -t 1
tmux send-keys "vagrant ssh beta" C-m

tmux select-pane -t 2
tmux send-keys "vagrant ssh gamma" C-m

tmux select-pane -t 3
tmux send-keys "vagrant ssh delta" C-m

tmux setw synchronize-panes on


tmux -2 attach-session -t $SESSION
