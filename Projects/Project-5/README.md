# CS 3700 Project 5: Web Crawler
Kevin Cruse

## Design Approach
This project follows a model-view-controller design with a three-layer controller implementation. The bottommost
controller layer is the socket layer, which controls basic reading and writing operations over a TCP socket with TLS.
The middle layer is the HTTP client, which manages sending and receiving HTTP messages with an HTTP service. The topmost
layer is the web crawler, which traverses a website by performing a DFS on the website's hyperlink graph.

## Challenges Faced
The main challenge I faced was implementing the HTTP client. Having to deal with variable-length messages proved
difficult with blocking sockets, and the `Content-Length` header was heavily used to remedy this. I was unable to
incorporate `gzip` decoding, however, as reading raw bytes from a `BufferedReader` of an input stream is not possible,
and the `BufferedReader` abstraction was necessary to read strings from the socket line-by-line.

## Testing
All testing of this project was done through the provided *Fakebook* website and verifying my solution with the
Gradescope autograder.
