# CS 3700 Project 3: BGP Router
Kevin Cruse

## Design
This project is divided into three main components: the router, the routing table, and the message processors. The
routing table handles the work of adding, updating, withdrawing, aggregating, and disaggregating routes. The message
processors take in messages received by peers, make necessary method calls to the routing table, and return a
collection of messages to send to its peers in response. Finally, the router is responsible for continuously receiving
messages from peers, forwarding them to message processors, and sending out any response messages returned from the
processors.

## Challenges Faced
The main challenge I faced in the development of this project was figuring out how channels from the `java.nio.channels`
package worked. This package works substantially different from the `java.io` package, and it allows you to use
multiplexed input and output through channel selectors. After much reading and experimentation, I was eventually able to
wrap my head around the concepts and abstractions provided by this package.

## Favorite Design Elements
My favorite design element from this project was creating the message processor abstraction, which allowed me to 
cleanly break apart the code for handling various types of messages into their own processor classes, making for easy
location and modification of the code.

## Testing
I tested my code by running some manual cases and by using the provided test suite.
