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

    @Override
    public GuessProgress guess(String word) throws IOException {
        if (id == null) {
            open();
        }

        String guessRequest = objectMapper.writeValueAsString(new GuessMessage(id, word));
        socketHandler.write(guessRequest);
        RetryOrByeMessage guessResponse = objectMapper.readValue(socketHandler.read(), RetryOrByeMessage.class);

        return GuessProgress.from(guessResponse);
    }

    @Override
    public void close() throws IOException {
        socketHandler.close();

        this.socketHandler = null;
        this.id = null;
    }

    private void open() throws IOException {
        this.socketHandler = encrypted
                ? new EncryptedSocketHandler(hostname, port)
                : new UnencryptedSocketHandler(hostname, port);

        String connectRequest = objectMapper.writeValueAsString(new HelloMessage(username));
        socketHandler.write(connectRequest);
        String connectResponse = socketHandler.read();

        this.id = objectMapper.readValue(connectResponse, StartMessage.class).getId();
    }
}
