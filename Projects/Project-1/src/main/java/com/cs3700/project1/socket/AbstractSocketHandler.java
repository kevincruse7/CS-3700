package com.cs3700.project1.socket;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

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

    @Override
    public String read() throws IOException {
        if (socket == null) {
            open();
        }

        return in.readLine();
    }

    @Override
    public void write(String message) throws IOException {
        if (socket == null) {
            open();
        }

        out.println(message);
    }

    @Override
    public void close() throws IOException {
        socket.close();
        in.close();
        out.close();

        this.socket = null;
        this.in = null;
        this.out = null;
    }

    protected abstract void open() throws IOException;
}
