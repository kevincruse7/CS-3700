package com.cs3700.project2.command;

import com.cs3700.project2.ftp.FtpConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;

/**
 * Runner implementation of the {@code rmdir} command.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RmdirCommandRunner implements CommandRunner {
    @NonNull
    private final FtpConnection ftpConnection;

    @NonNull
    private final URI uri;

    @Override
    @SneakyThrows
    public void run() {
        ftpConnection.sendCommand("RMD " + uri.getPath());
        ftpConnection.receiveResponse();
    }

    @Override
    public void close() throws IOException {
        ftpConnection.close();
    }
}
