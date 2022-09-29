package com.cs3700.project2.command;

import com.cs3700.project2.ftp.FtpConnection;
import com.cs3700.project2.ftp.FtpDataChannel;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;

/**
 * Runner implementation of the {@code ls} command.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class LsCommandRunner implements CommandRunner {
    @NonNull
    private final FtpConnection ftpConnection;

    @NonNull
    private final URI uri;

    @Override
    @SneakyThrows
    public void run() {
        try (FtpDataChannel ftpDataChannel = ftpConnection.openDataChannel()) {
            ftpConnection.sendCommand("LIST " + uri.getPath());
            ftpConnection.receiveResponse();

            StringBuilder output = new StringBuilder();
            byte[] dataBuffer = ftpDataChannel.read();

            while (dataBuffer.length > 0) {
                output.append(new String(dataBuffer));
                dataBuffer = ftpDataChannel.read();
            }

            System.out.print(output);
        }

        ftpConnection.receiveResponse();
    }

    @Override
    public void close() throws IOException {
        ftpConnection.close();
    }
}
