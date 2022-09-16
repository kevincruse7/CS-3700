package com.cs3700.project1.socket;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/** Basic implementation of a generic socket handler. */
public abstract class AbstractSocketHandler implements SocketHandler {
    protected final String hostname;
    protected final int port;

    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;

    public AbstractSocketHandler(@NonNull String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /** Read line from socket. */
    @Override
    public String read() throws IOException {
        // If connection isn't yet established, open one
        if (socket == null) {
            open();
        }

        return in.readLine();
    }

    /** Write line to socket. */
    @Override
    public void write(String message) throws IOException {
        // If connection isn't yet established, open one
        if (socket == null) {
            open();
        }

        out.println(message);
    }

    /** Close socket connection. */
    @Override
    public void close() throws IOException {
        // If socket is already closed, then do nothing
        if (socket == null) {
            return;
        }

        socket.close();
        in.close();
        out.close();

        this.socket = null;
        this.in = null;
        this.out = null;
    }

    // Open socket connection
    protected abstract void open() throws IOException;
}
