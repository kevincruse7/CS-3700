package com.cs3700.project2.socket;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Basic implementation of socket connection for transmitting strings.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicStringSocketConnection implements SocketConnection<String> {
    @NonNull
    private final Socket socket;

    @NonNull
    private final BufferedReader in;

    @NonNull
    private final PrintWriter out;

    @Override
    public String read() throws IOException {
        return in.readLine();
    }

    @Override
    public void write(@NonNull String data) {
        out.println(data);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
