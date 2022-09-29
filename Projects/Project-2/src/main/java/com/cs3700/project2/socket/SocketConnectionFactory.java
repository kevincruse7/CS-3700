package com.cs3700.project2.socket;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Factory class for socket connection implementations.
 */
public class SocketConnectionFactory {
    /**
     * Create a data socket connection.
     *
     * @param hostname       Hostname or IP address of device to connect to.
     * @param port           Port number of device to connect to.
     * @param blockSizeBytes Maximum number of bytes to read from socket at once.
     * @return New data socket connection with specified parameters.
     * @throws IOException I/O error occurred when establishing socket connection.
     */
    public static SocketConnection<byte[]> createDataSocketConnection(
        @NonNull String hostname, int port, int blockSizeBytes
    ) throws IOException {
        Socket socket = new Socket(hostname, port);
        return new BasicDataSocketConnection(socket, socket.getInputStream(), socket.getOutputStream(), blockSizeBytes);
    }

    /**
     * Create a string socket connection.
     *
     * @param hostname Hostname or IP address of device to connect to.
     * @param port     Port number of device to connect to.
     * @return New string socket connection with specified parameters.
     * @throws IOException I/O error occurred when establishing socket connection.
     */
    public static SocketConnection<String> createStringSocketConnection(@NonNull String hostname, int port)
        throws IOException {
        Socket socket = new Socket(hostname, port);

        return new BasicStringSocketConnection(
            socket,
            new BufferedReader(new InputStreamReader(socket.getInputStream())),
            new PrintWriter(socket.getOutputStream(), true)
        );
    }
}
