package com.cs3700.project2.socket;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BasicCommandCommandSocketHandler implements CommandSocketHandler {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    @SneakyThrows
    public BasicCommandCommandSocketHandler(@NonNull String hostname, int port) {
        this.socket = new Socket(hostname, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    @SneakyThrows
    public String read() {
        return in.readLine();
    }

    @Override
    @SneakyThrows
    public void write(@NonNull String data) {
        out.println(data);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
