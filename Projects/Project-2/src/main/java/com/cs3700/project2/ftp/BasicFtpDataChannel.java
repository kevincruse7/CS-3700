package com.cs3700.project2.ftp;

import com.cs3700.project2.socket.SocketConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicFtpDataChannel implements FtpDataChannel {
    @NonNull
    private final SocketConnection<byte[]> dataSocketConnection;

    @Override
    public byte[] read() throws IOException {
        return dataSocketConnection.read();
    }

    @Override
    public void write(byte @NonNull [] data) throws IOException {
        dataSocketConnection.write(data);
    }

    @Override
    public void close() throws IOException {
        dataSocketConnection.close();
    }
}
