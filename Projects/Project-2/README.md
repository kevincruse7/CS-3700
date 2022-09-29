# CS 3700 Project 2
Kevin Cruse

## Design
This project follows an object-oriented approach with a three layer design: the socket layer, the FTP layer, and the
application command layer. The socket layer is the bottommost layer, providing simple functionality for reading to and
writing from TCP sockets with either strings or byte arrays. Building on the socket layer, the FTP layer handles
communication with FTP servers, providing methods to send commands, receive responses, and open data channels. Finally,
the application command layer provides command 'runners' that implement each of the required client commands: `cp`,
`ls`, `mkdir`, `mv`, `rm`, and `rmdir`.

## Challenges
The main challenge I encountered was trying to manually interact with the FTP server to see how it would respond to
different scenarios. The FTP client I tried didn't exactly show the bare client requests and server responses being made
for different operations, so getting an understanding of the exact transcript was difficult. However, I quickly
discovered netcat, which allows you to establish and interact with raw TCP sockets. It was through this bare-bones
interaction with the FTP server that I acquired the requisite knowledge to begin implementing my own client.

## Testing
To test my FTP client, I manually ran through each of the commands with the provided FTP server, ensuring that the
behavior of each was up to specification. Additionally, I confirmed that all the Gradescope tests passed.