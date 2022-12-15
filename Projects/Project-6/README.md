# CS 3700 Project 6: Distributed Key-Value Store
Kevin Cruse

## Design
This project utilizes a model-view-controller design, with the implementation only requiring a model and a controller.
The model consists of top-level application state and various data classes for internal representation of messages. The
controller has a three-layer design consisting of the top-level application controller, message handlers, and the string
UDP socket connection. The application controller continuously reads in messages from the socket, passes them off to the
message handlers, and writes out any response messages to the socket. Message handlers process received messages and
produce the necessary state changes and response messages in accordance with the received message. Finally, the socket
connection handles all read and write operations to and from the connected UDP socket.

## Challenge(s) Faced
Tha main challenge I faced while working on this project was debugging my code, as the simulator required a nearly
complete implementation to even run properly, and attaching a debugger to one of the replica instances
would've created issues due to the time-sensitive nature of the program. So, all debugging had to be done through print
statements and scanning produced log files, complicating the task of diagnosing bugs.

## Achievement(s)
Implementing the RAFT protocol is a difficult task with innumerable edge cases, each of which requiring substantial
code modifications. I believe I was able to iterate through these cases without devolving the code into total
spaghetti, so I'd consider my ability to preserve some cohesive structure in the codebase as my primary achievement with
this project.
