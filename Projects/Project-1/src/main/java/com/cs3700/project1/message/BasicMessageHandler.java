package com.cs3700.project1.message;

import com.cs3700.project1.model.GuessProgress;
import com.cs3700.project1.model.message.GuessMessage;
import com.cs3700.project1.model.message.HelloMessage;
import com.cs3700.project1.model.message.RetryOrByeMessage;
import com.cs3700.project1.model.message.StartMessage;
import com.cs3700.project1.socket.EncryptedSocketHandler;
import com.cs3700.project1.socket.SocketHandler;
import com.cs3700.project1.socket.UnencryptedSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.io.IOException;

/** Basic implementation of a Wordle message handler. */
public class BasicMessageHandler implements MessageHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final int port;
    private final boolean encrypted;
    private final String hostname;
    private final String username;

    private SocketHandler socketHandler;
    private String id;

    public BasicMessageHandler(int port, boolean encrypted, @NonNull String hostname, @NonNull String username) {
        this.port = port;
        this.encrypted = encrypted;
        this.hostname = hostname;
        this.username = username;
    }

    /** Send word guess to server and retrieve match response. */
    @Override
    public GuessProgress guess(String word) throws IOException {
        // If connection isn't already established, open one
        if (id == null) {
            open();
        }

        // Serialize guess, send to server, and deserialize response
        String guessRequest = objectMapper.writeValueAsString(new GuessMessage(id, word));
        socketHandler.write(guessRequest);
        RetryOrByeMessage guessResponse = objectMapper.readValue(socketHandler.read(), RetryOrByeMessage.class);

        return GuessProgress.from(guessResponse);
    }

    /** Close server connection. */
    @Override
    public void close() throws IOException {
        // If server connection is already closed, do nothing
        if (socketHandler == null) {
            return;
        }

        socketHandler.close();

        this.socketHandler = null;
        this.id = null;
    }

    // Open server connection
    private void open() throws IOException {
        // Open either an encrypted or unencrypted connection depending on user specification
        this.socketHandler = encrypted
                ? new EncryptedSocketHandler(hostname, port)
                : new UnencryptedSocketHandler(hostname, port);

        // Send 'hello' message
        String connectRequest = objectMapper.writeValueAsString(new HelloMessage(username));
        socketHandler.write(connectRequest);
        String connectResponse = socketHandler.read();

        // Save ID from 'start' response
        this.id = objectMapper.readValue(connectResponse, StartMessage.class).getId();
    }
}
