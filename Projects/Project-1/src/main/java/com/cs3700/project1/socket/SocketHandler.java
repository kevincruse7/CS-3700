package com.cs3700.project1.socket;

import java.io.Closeable;
import java.io.IOException;

/** Handler for reading strings from and writing strings to a socket. */
public interface SocketHandler extends Closeable {
    /** Read line from socket. */
    String read() throws IOException;

    /** Write line to socket. */
    void write(String message) throws IOException;
}
