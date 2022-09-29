package com.cs3700.project2.ftp;

import com.cs3700.project2.Config;
import com.cs3700.project2.socket.SocketConnection;
import com.cs3700.project2.socket.SocketConnectionFactory;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Basic implementation of an FTP server connection.
 */
class BasicFtpConnection implements FtpConnection {
    private final SocketConnection<String> commandSocketConnection;

    BasicFtpConnection(
        @NonNull SocketConnection<String> commandSocketConnection, @NonNull String username, @NonNull String password
    ) throws IOException {
        this.commandSocketConnection = commandSocketConnection;
        receiveResponse();

        sendCommand("USER " + username);
        receiveResponse();

        if (password.length() > 0) {
            sendCommand("PASS " + password);
            receiveResponse();
        }

        sendCommand("TYPE I");  // 8-bit binary data type
        receiveResponse();
        sendCommand("MODE S");  // Stream mode
        receiveResponse();
        sendCommand("STRU F");  // File-oriented structure
        receiveResponse();
    }

    @Override
    public void sendCommand(@NonNull String command) throws IOException {
        System.out.println(command);
        commandSocketConnection.write(command + "\r\n");
    }

    @Override
    public String receiveResponse() throws IOException {
        String serverResponse = commandSocketConnection.read();
        System.out.println(serverResponse);

        // Ensure response status code isn't in the error range
        if (serverResponse.length() < 3
            || Integer.parseInt(serverResponse.substring(0, 3)) >= Config.FTP_ERROR_STATUS_LOWER_BOUND) {
            throw new IOException("Unexpected response from server: " + serverResponse);
        }

        return serverResponse;
    }

    @Override
    public FtpDataChannel openDataChannel() throws IOException {
        sendCommand("PASV");
        String serverResponse = receiveResponse();

        String[] ipComponents = serverResponse
            .substring(serverResponse.indexOf('(') + 1, serverResponse.indexOf(')'))
            .split(",");

        String ip = Arrays.stream(ipComponents)
            .limit(Config.IP_LENGTH_BYTES)
            .collect(Collectors.joining("."));

        int port = (Integer.parseInt(ipComponents[Config.IP_LENGTH_BYTES]) << 8)
            + Integer.parseInt(ipComponents[Config.IP_LENGTH_BYTES + 1]);

        return new BasicFtpDataChannel(SocketConnectionFactory.createDataSocketConnection(
            ip, port, Config.FTP_TRANSFER_BLOCK_SIZE_BYTES
        ));
    }

    @Override
    public void close() throws IOException {
        sendCommand("QUIT");
        receiveResponse();
    }
}
