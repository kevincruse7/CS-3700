# CS 3700 Project 4: Reliable Transport Protocol
Kevin Cruse

## Design
For this project, I decided to implement a stripped-down version of TCP Reno. Basically, I used the same algorithms for
flow control and congestion control, but I removed extraneous header and connection setup and teardown protocols that
weren't needed for this use case. This project is designed in three layers: the bottom UDP layer, the TCP connection
layer, and the sender/receiver process layer. The UDP layer provides basic methods for sending TCP packets as datagrams.
The TCP connection layer contains all the logic for reliably sending and receiving messages across hosts. Lastly, the
sender/receiver process layer contains the high-level logic for running their respective applications in the simulated
environment.

## Challenges Faced
Due to both internal and external factors, I ended up not starting this project until two days before the deadline. As
such, there was an immense time crunch, and I severely underestimated the amount of designing and tweaking this project
would require. While this wasn't my best work, I do believe I ultimately produced a functional and legible result.

## Features
The features which I am most proud of with this project are the implementations of TCP Reno's congestion controls, with
all of slow start, congestion avoidance, and fast retransmit making it into the final product. These controls provided
some invaluable parameters that could easily be tweaked for fine-tuning my code to pass the tests.

## Testing
I tested this project by running it through the provided simulator and test cases.