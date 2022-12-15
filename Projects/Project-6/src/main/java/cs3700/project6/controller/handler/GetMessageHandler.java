package cs3700.project6.controller.handler;

import cs3700.project6.Config;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.GetFailMessage;
import cs3700.project6.model.message.GetMessage;
import cs3700.project6.model.message.GetOkMessage;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.RedirectMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Message handler implementation for {@code get} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class GetMessageHandler implements MessageHandler {
    @NonNull
    private final Random random;

    @NonNull
    private final Model model;

    @NonNull
    private final GetMessage getMessage;

    @Override
    public List<Message> handle() {
        ArrayList<Message> messages = new ArrayList<>();

        if (model.getMode() == Mode.LEADER) {
            messages.add(GetOkMessage.builder()
                .source(model.getID())
                .destination(getMessage.getSource())
                .leader(model.getLeader())
                .messageID(getMessage.getMessageID())
                .value(model.getValueFor(getMessage.getKey()))
                .build()
            );

            messages.addAll(new TimeoutMessageHandler(random, model).handle());
        } else {
            if (!model.getLeader().equals(Config.RAFT_ID_UNSPECIFIED)) {
                messages.add(RedirectMessage.builder()
                    .source(model.getID())
                    .destination(getMessage.getSource())
                    .leader(model.getLeader())
                    .messageID(getMessage.getMessageID())
                    .build()
                );
            } else {
                messages.add(GetFailMessage.builder()
                    .source(model.getID())
                    .destination(getMessage.getSource())
                    .leader(model.getLeader())
                    .messageID(getMessage.getMessageID())
                    .build()
                );
            }

            // Check if heartbeat timeout has been reached
            if (Instant.now().toEpochMilli() >= model.getLastHeartbeatTimestamp() + model.getMessageTimeout()) {
                messages.addAll(new TimeoutMessageHandler(random, model).handle());
            }
        }

        return messages;
    }
}
