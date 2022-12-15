package cs3700.project6.controller.handler;

import cs3700.project6.Config;
import cs3700.project6.Util;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.AppendEntriesMessage;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.RequestVoteMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Message handler implementation for message timeouts.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class TimeoutMessageHandler implements MessageHandler {
    @NonNull
    private final Random random;

    @NonNull
    private final Model model;

    @Override
    public List<Message> handle() {
        Message message;

        if (model.getMode() == Mode.LEADER) {
            // Send a heartbeat if we're a leader
            message = AppendEntriesMessage.builder()
                .source(model.getID())
                .destination(Config.RAFT_ID_MULTICAST)
                .leader(model.getLeader())
                .messageID(Util.generateMessageIDFrom(random))
                .term(model.getTerm())
                .prevLogIndex(-1)
                .prevLogTerm(0)
                .entries(Collections.emptyList())
                .leaderCommit(model.getCommitIndex())
                .build();
        } else {
            // Otherwise, initiate an election
            model.setTerm(model.getTerm() + 1);
            model.setMode(Mode.CANDIDATE);
            model.setVotedFor(model.getID());
            model.addVote(model.getID());

            int lastLogIndex = model.getLogSize() - 1;

            RequestVoteMessage.RequestVoteMessageBuilder<?, ?> messageBuilder = RequestVoteMessage.builder()
                .source(model.getID())
                .destination(Config.RAFT_ID_MULTICAST)
                .leader(model.getLeader())
                .messageID(Util.generateMessageIDFrom(random))
                .term(model.getTerm())
                .lastLogIndex(lastLogIndex)
                .lastLogTerm(lastLogIndex >= 0 ? model.getLogEntry(lastLogIndex).getTerm() : 0);

            message = messageBuilder.build();

            for (String remoteID : model.getRemotes()) {
                model.putMessageTimeout(messageBuilder
                    .destination(remoteID)
                    .build()
                );
            }
        }

        return List.of(message);
    }
}
