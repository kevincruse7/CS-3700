package cs3700.project6.controller.handler;

import cs3700.project6.Config;
import cs3700.project6.Util;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.AppendEntriesMessage;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.PutFailMessage;
import cs3700.project6.model.message.PutMessage;
import cs3700.project6.model.message.RedirectMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Message handler implementation for {@code put} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class PutMessageHandler implements MessageHandler {
    @NonNull
    private final Random random;

    @NonNull
    private final Model model;

    @NonNull
    private final PutMessage putMessage;

    @Override
    public List<Message> handle() {
        ArrayList<Message> messages = new ArrayList<>();

        if (model.getMode() == Mode.LEADER) {
            model.addActivePutMessage(putMessage);
            model.addLogEntry(model.getTerm(), putMessage.getKey(), putMessage.getValue());
            model.clearMessageTimeouts();

            // Send out 'appendEntries' messages for new put
            for (String remoteID : model.getRemotes()) {
                int remoteNextIndex = model.getNextIndex(remoteID);

                AppendEntriesMessage appendEntriesMessage = AppendEntriesMessage.builder()
                    .source(model.getID())
                    .destination(remoteID)
                    .leader(model.getLeader())
                    .messageID(Util.generateMessageIDFrom(random))
                    .term(model.getTerm())
                    .prevLogIndex(remoteNextIndex - 1)
                    .prevLogTerm(remoteNextIndex > 0 ? model.getLogEntry(remoteNextIndex - 1).getTerm() : 0)
                    .entries(IntStream.range(remoteNextIndex, model.getLogSize())
                        .mapToObj(model::getLogEntry)
                        .collect(Collectors.toList()))
                    .leaderCommit(model.getCommitIndex())
                    .build();

                messages.add(appendEntriesMessage);
                model.putMessageTimeout(appendEntriesMessage);
            }
        } else {
            if (!model.getLeader().equals(Config.RAFT_ID_UNSPECIFIED)) {
                messages.add(RedirectMessage.builder()
                    .source(model.getID())
                    .destination(putMessage.getSource())
                    .leader(model.getLeader())
                    .messageID(putMessage.getMessageID())
                    .build()
                );
            } else {
                messages.add(PutFailMessage.builder()
                    .source(model.getID())
                    .destination(putMessage.getSource())
                    .leader(model.getLeader())
                    .messageID(putMessage.getMessageID())
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
