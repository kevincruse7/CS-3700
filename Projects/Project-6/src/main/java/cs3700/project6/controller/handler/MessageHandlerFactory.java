package cs3700.project6.controller.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project6.Config;
import cs3700.project6.Util;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.AppendEntriesFailMessage;
import cs3700.project6.model.message.AppendEntriesMessage;
import cs3700.project6.model.message.AppendEntriesOkMessage;
import cs3700.project6.model.message.GetMessage;
import cs3700.project6.model.message.HelloMessage;
import cs3700.project6.model.message.PutMessage;
import cs3700.project6.model.message.RequestVoteFailMessage;
import cs3700.project6.model.message.RequestVoteMessage;
import cs3700.project6.model.message.RequestVoteOkMessage;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Factory class for creating message handlers.
 */
public class MessageHandlerFactory {
    /**
     * Creates a handler for sending out a {@code hello} message with the given values.
     *
     * @param random Random object to generate message ID with.
     * @param model Application model to use with the handler.
     * @return Created handler for sending out a {@code hello} message.
     */
    public static MessageHandler createHelloMessageHandler(@NonNull Random random, @NonNull Model model) {
        return () -> List.of(HelloMessage.builder()
            .source(model.getID())
            .destination(Config.RAFT_ID_MULTICAST)
            .leader(Config.RAFT_ID_UNSPECIFIED)
            .messageID(Util.generateMessageIDFrom(random))
            .build()
        );
    }

    /**
     * Creates a handler for message timeouts with the given values.
     *
     * @param random Random object to generate message IDs with.
     * @param model Application model to use with the handler.
     * @return Created handler for message timeouts.
     */
    public static MessageHandler createTimeoutHandler(@NonNull Random random, @NonNull Model model) {
        return new TimeoutMessageHandler(random, model);
    }

    /**
     * Creates a message handler for the given values.
     *
     * @param objectMapper Mapper object to use for (de)serialization.
     * @param message Received message to handle.
     * @param random Random object to use for generating message IDs.
     * @param model Application model to use with the handler.
     * @return Created message handler, if it exists.
     * @throws IOException Failed to deserialize message.
     */
    public static Optional<MessageHandler> createMessageHandlerFor(
        @NonNull ObjectMapper objectMapper,
        @NonNull String message,
        @NonNull Random random,
        @NonNull Model model
    ) throws IOException {
        String messageType = objectMapper.readTree(message).get("type").textValue();

        switch (messageType) {
            case "get":
                return Optional.of(new GetMessageHandler(random, model, objectMapper.readValue(
                    message,
                    GetMessage.class
                )));
            case "put":
                return Optional.of(new PutMessageHandler(random, model, objectMapper.readValue(
                    message,
                    PutMessage.class
                )));
            case "appendEntries":
                return Optional.of(new AppendEntriesMessageHandler(random, model, objectMapper.readValue(
                    message,
                    AppendEntriesMessage.class
                )));
            case "appendEntriesOk":
                return Optional.of(new AppendEntriesOkMessageHandler(model, objectMapper.readValue(
                    message,
                    AppendEntriesOkMessage.class
                )));
            case "appendEntriesFail":
                return Optional.of(new AppendEntriesFailMessageHandler(random, model, objectMapper.readValue(
                    message,
                    AppendEntriesFailMessage.class
                )));
            case "requestVote":
                return Optional.of(new RequestVoteMessageHandler(model, objectMapper.readValue(
                    message,
                    RequestVoteMessage.class
                )));
            case "requestVoteOk":
                return Optional.of(new RequestVoteOkMessageHandler(random, model, objectMapper.readValue(
                    message,
                    RequestVoteOkMessage.class
                )));
            case "requestVoteFail":
                return Optional.of(new RequestVoteFailMessageHandler(model, objectMapper.readValue(
                    message,
                    RequestVoteFailMessage.class
                )));
            default:
                return Optional.empty();
        }
    }
}
