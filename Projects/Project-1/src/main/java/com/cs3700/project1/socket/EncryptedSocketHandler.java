package com.cs3700.project1.socket;

import lombok.NonNull;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class EncryptedSocketHandler extends AbstractSocketHandler {
    public EncryptedSocketHandler(@NonNull String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected void open() throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);
        socket.startHandshake();

        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }
}
