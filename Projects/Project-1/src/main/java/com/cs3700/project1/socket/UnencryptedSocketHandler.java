package com.cs3700.project1.socket;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Implementation of an unencrypted socket handler. */
public class UnencryptedSocketHandler extends AbstractSocketHandler {
    public UnencryptedSocketHandler(@NonNull String hostname, int port) {
        super(hostname, port);
    }

    // Open an unencrypted socket connection
    @Override
    protected void open() throws IOException {
        this.socket = new Socket(hostname, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }
}
