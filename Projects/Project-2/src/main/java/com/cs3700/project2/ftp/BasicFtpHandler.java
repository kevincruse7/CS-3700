package com.cs3700.project2.ftp;

import com.cs3700.project2.Config;
import com.cs3700.project2.Util;
import com.cs3700.project2.socket.CommandSocketHandler;
import com.cs3700.project2.socket.CommandSocketHandlerComponent.CommandSocketHandlerModule;
import com.cs3700.project2.socket.DaggerCommandSocketHandlerComponent;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.net.URI;

public class BasicFtpHandler implements FtpHandler {
    private final CommandSocketHandler commandSocketHandler;

    @SneakyThrows
    public BasicFtpHandler(@NonNull URI uri) {
        if (!"ftp".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Unsupported scheme: " + uri.getScheme());
        }

        this.commandSocketHandler = DaggerCommandSocketHandlerComponent.builder()
            .commandSocketHandlerModule(new CommandSocketHandlerModule(
                uri.getHost(),
                uri.getPort() == -1 ? Config.DEFAULT_COMMAND_PORT : uri.getPort()
            ))
            .build()
            .commandSocketHandler();
        receive();

        String username;
        String password;

        if (uri.getUserInfo() != null) {
            String[] splitUserInfo = uri.getUserInfo().split(":");
            username = splitUserInfo[0];

            if (splitUserInfo.length > 1) {
                password = splitUserInfo[1];
            } else {
                password = "";
            }
        } else {
            username = "anonymous";
            password = "";
        }

        send(String.format("USER %s\r\n", username));
        receive();

        if (password.length() > 0) {
            send(String.format("PASS %s\r\n", password));
            receive();
        }

        send("TYPE I\r\n");
        receive();
        send("MODE S\r\n");
        receive();
        send("STRU F\r\n");
        receive();
    }

    @Override
    @SneakyThrows
    public void send(String command) {
        System.out.print(command);
        commandSocketHandler.write(command);
    }

    @Override
    @SneakyThrows
    public String receive() {
        String serverResponse = commandSocketHandler.read();
        System.out.println(serverResponse);
        Util.assertStatusCodeOk(serverResponse);

        return serverResponse;
    }

    @Override
    @SneakyThrows
    public void close() {
        send("QUIT\r\n");
        receive();
    }
}
