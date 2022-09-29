package com.cs3700.project2.command;

import com.cs3700.project2.Config;
import com.cs3700.project2.ftp.FtpConnection;
import com.cs3700.project2.ftp.FtpDataChannel;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Runner implementation of the {@code cp} command.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class CpCommandRunner implements CommandRunner {
    @NonNull
    private final FtpConnection ftpConnection;

    @NonNull
    private final URI from;

    @NonNull
    private final URI to;

    @Override
    @SneakyThrows
    public void run() {
        try (FtpDataChannel ftpDataChannel = ftpConnection.openDataChannel()) {
            // Determine if the 'from' parameter is the FTP URI or the local path
            boolean fromParamIsFtpUri = from.getHost() != null;

            if (fromParamIsFtpUri) {
                ftpConnection.sendCommand("RETR " + from.getPath());
                ftpConnection.receiveResponse();

                try (FileOutputStream fileOutputStream = new FileOutputStream(to.getPath())) {
                    byte[] dataBuffer = ftpDataChannel.read();

                    while (dataBuffer.length > 0) {
                        fileOutputStream.write(dataBuffer);
                        dataBuffer = ftpDataChannel.read();
                    }
                }
            } else {
                ftpConnection.sendCommand("STOR " + to.getPath());
                ftpConnection.receiveResponse();

                try (FileInputStream fileInputStream = new FileInputStream(from.getPath())) {
                    byte[] dataBuffer = fileInputStream.readNBytes(Config.FTP_TRANSFER_BLOCK_SIZE_BYTES);

                    while (dataBuffer.length > 0) {
                        ftpDataChannel.write(dataBuffer);
                        dataBuffer = fileInputStream.readNBytes(Config.FTP_TRANSFER_BLOCK_SIZE_BYTES);
                    }
                }
            }
        }

        ftpConnection.receiveResponse();
    }

    @Override
    public void close() throws IOException {
        ftpConnection.close();
    }
}
