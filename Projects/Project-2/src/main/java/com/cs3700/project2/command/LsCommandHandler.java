package com.cs3700.project2.command;

import com.cs3700.project2.ftp.DaggerFtpHandlerComponent;
import com.cs3700.project2.ftp.FtpHandler;
import com.cs3700.project2.ftp.FtpHandlerComponent.FtpHandlerModule;
import com.cs3700.project2.socket.DaggerDataSocketHandlerComponent;
import com.cs3700.project2.socket.DataSocketHandler;
import com.cs3700.project2.socket.DataSocketHandlerComponent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URI;

@RequiredArgsConstructor
public class LsCommandHandler implements CommandHandler {
    @NonNull
    private final URI uri;

    @Override
    @SneakyThrows
    public void run() {
        try (
            FtpHandler ftpHandler = DaggerFtpHandlerComponent.builder()
                .ftpHandlerModule(new FtpHandlerModule(uri))
                .build()
                .ftpHandler()
        ) {
            ftpHandler.send("PASV\r\n");
            String serverResponse = ftpHandler.receive();

            try (
                DataSocketHandler dataSocketHandler = DaggerDataSocketHandlerComponent.builder()
                    .dataSocketHandlerModule(new DataSocketHandlerComponent.DataSocketHandlerModule(serverResponse))
                    .build()
                    .dataSocketHandler()
            ) {
                ftpHandler.send(String.format("LIST %s\r\n", uri.getPath()));
                ftpHandler.receive();

                StringBuilder output = new StringBuilder();
                byte[] dataBuffer = dataSocketHandler.read();

                while (dataBuffer.length > 0) {
                    output.append(new String(dataBuffer));
                    dataBuffer = dataSocketHandler.read();
                }

                System.out.print(output);
            }

            ftpHandler.receive();
        }
    }
}
