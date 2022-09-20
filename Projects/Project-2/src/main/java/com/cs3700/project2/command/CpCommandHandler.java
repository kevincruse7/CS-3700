package com.cs3700.project2.command;

import com.cs3700.project2.Config;
import com.cs3700.project2.ftp.DaggerFtpHandlerComponent;
import com.cs3700.project2.ftp.FtpHandler;
import com.cs3700.project2.ftp.FtpHandlerComponent.FtpHandlerModule;
import com.cs3700.project2.socket.DaggerDataSocketHandlerComponent;
import com.cs3700.project2.socket.DataSocketHandler;
import com.cs3700.project2.socket.DataSocketHandlerComponent.DataSocketHandlerModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;

@RequiredArgsConstructor
public class CpCommandHandler implements CommandHandler {
    @NonNull
    private final URI from;

    @NonNull
    private final URI to;

    @Override
    @SneakyThrows
    public void run() {
        boolean fromRemoteToLocal = from.getHost() != null;

        try (
            FtpHandler ftpHandler = DaggerFtpHandlerComponent.builder()
                .ftpHandlerModule(new FtpHandlerModule(fromRemoteToLocal ? from : to))
                .build()
                .ftpHandler()
        ) {
            ftpHandler.send("PASV\r\n");
            String serverResponse = ftpHandler.receive();

            try (
                DataSocketHandler dataSocketHandler = DaggerDataSocketHandlerComponent.builder()
                    .dataSocketHandlerModule(new DataSocketHandlerModule(serverResponse))
                    .build()
                    .dataSocketHandler()
            ) {
                if (fromRemoteToLocal) {
                    ftpHandler.send(String.format("RETR %s\r\n", from.getPath()));
                    ftpHandler.receive();

                    try (FileOutputStream fileOutputStream = new FileOutputStream(to.getPath())) {
                        byte[] dataBuffer = dataSocketHandler.read();

                        while (dataBuffer.length > 0) {
                            fileOutputStream.write(dataBuffer);
                            dataBuffer = dataSocketHandler.read();
                        }
                    }
                } else {
                    ftpHandler.send(String.format("STOR %s\r\n", to.getPath()));
                    ftpHandler.receive();

                    try (FileInputStream fileInputStream = new FileInputStream(from.getPath())) {
                        byte[] dataBuffer = fileInputStream.readNBytes(Config.DATA_BLOCK_SIZE_BYTES);

                        while (dataBuffer.length > 0) {
                            dataSocketHandler.write(dataBuffer);
                            dataBuffer = fileInputStream.readNBytes(Config.DATA_BLOCK_SIZE_BYTES);
                        }
                    }
                }
            }

            ftpHandler.receive();
        }
    }
}
