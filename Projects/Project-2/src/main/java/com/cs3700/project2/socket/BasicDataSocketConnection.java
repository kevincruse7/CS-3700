package com.cs3700.project2.socket;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Basic implementation of socket connection for transmitting data.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicDataSocketConnection implements SocketConnection<byte[]> {
    @NonNull
    private final Socket socket;

    @NonNull
    private final InputStream in;

    @NonNull
    private final OutputStream out;

    @NonNull
    private final Integer blockSizeBytes;

    @Override
    public byte[] read() throws IOException {
        return in.readNBytes(blockSizeBytes);
    }

    @Override
    public void write(byte @NonNull [] data) throws IOException {
        out.write(data);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
