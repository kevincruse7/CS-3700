package cs3700.project6.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project6.Config;
import cs3700.project6.controller.handler.MessageHandler;
import cs3700.project6.controller.handler.MessageHandlerFactory;
import cs3700.project6.controller.socket.StringSocket;
import cs3700.project6.model.MessageTimeout;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Basic implementation of a top-level application controller.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicController implements Controller {
    @NonNull
    private final Random random;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final Model model;

    @NonNull
    private final StringSocket stringSocket;

    @Override
    @SneakyThrows
    public void run() {
        // Send out 'hello' message on initialization
        MessageHandler helloMessageHandler = MessageHandlerFactory.createHelloMessageHandler(random, model);
        Message helloMessage = helloMessageHandler.handle().get(0);
        String serializedHelloMessage = objectMapper.writeValueAsString(helloMessage);
        stringSocket.write(serializedHelloMessage);

        String receivedMessage = receiveMessage();

        //noinspection InfiniteLoopStatement
        while (true) {
            Map<String, List<MessageTimeout>> messageTimeouts = model.getMessageTimeouts();

            // Process any expired message timeouts
            for (String messageID : messageTimeouts.keySet()) {
                List<MessageTimeout> messageTimeoutList = messageTimeouts.get(messageID);

                for (int i = 0; i < messageTimeoutList.size(); ++i) {
                    MessageTimeout messageTimeout = messageTimeoutList.get(i);
                    long currentTime = Instant.now().toEpochMilli();

                    if (messageTimeout.getTimeout() > currentTime) {
                        continue;
                    }

                    Message message = messageTimeout.getMessage();
                    messageTimeout.setNumTimeouts(messageTimeout.getNumTimeouts() + 1);

                    // If a message has failed to send several times, set the destination replica as inactive
                    if (messageTimeout.getNumTimeouts() >= Config.RAFT_MESSAGE_MAX_TIMEOUTS) {
                        String badRemote = message.getDestination();
                        model.removeMessageTimeout(messageID, badRemote);

                        if (model.getRemotes().contains(badRemote)) {
                            model.removeRemote(badRemote);
                        }

                        --i;
                        continue;
                    }

                    // Otherwise, resend the message
                    String serializedMessage = objectMapper.writeValueAsString(message);
                    stringSocket.write(serializedMessage);

                    messageTimeout.setTimeout(currentTime + Config.RAFT_MESSAGE_TIMEOUT_MILLIS);
                }
            }

            Optional<MessageHandler> maybeMessageHandler = MessageHandlerFactory.createMessageHandlerFor(
                objectMapper, receivedMessage, random, model
            );

            if (maybeMessageHandler.isPresent()) {
                List<Message> responseMessages = maybeMessageHandler.get().handle();

                // Send out any response messages returned from the message handler
                for (Message responseMessage : responseMessages) {
                    String serializedResponseMessage = objectMapper.writeValueAsString(responseMessage);
                    stringSocket.write(serializedResponseMessage);
                }
            }

            receivedMessage = receiveMessage();
        }
    }

    @Override
    public void close() throws IOException {
        stringSocket.close();
    }

    // Receive a message from the connected socket
    private String receiveMessage() throws IOException {
        Optional<String> maybeReceivedMessage;

        do {
            maybeReceivedMessage = stringSocket.read(model.getMessageTimeout());

            // Handle socket timeout
            if (maybeReceivedMessage.isEmpty()) {
                MessageHandler timeoutHandler = MessageHandlerFactory.createTimeoutHandler(random, model);
                List<Message> responseMessages = timeoutHandler.handle();

                for (Message responseMessage : responseMessages) {
                    String serializedResponseMessage = objectMapper.writeValueAsString(responseMessage);
                    stringSocket.write(serializedResponseMessage);
                }
            }
        } while (maybeReceivedMessage.isEmpty());

        return maybeReceivedMessage.get();
    }
}
