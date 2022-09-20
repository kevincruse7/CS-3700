package com.cs3700.project2.command;

import com.cs3700.project2.ftp.DaggerFtpHandlerComponent;
import com.cs3700.project2.ftp.FtpHandler;
import com.cs3700.project2.ftp.FtpHandlerComponent.FtpHandlerModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URI;

@RequiredArgsConstructor
public class RmCommandHandler implements CommandHandler {
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
            ftpHandler.send(String.format("DELE %s\r\n", uri.getPath()));
            ftpHandler.receive();
        }
    }
}
