package cs3700.project6.controller.handler;

import cs3700.project6.Config;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.RequestVoteOkMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Random;

/**
 * Message handler implementation for {@code requestVoteOk} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class RequestVoteOkMessageHandler implements MessageHandler {
    @NonNull
    private final Random random;

    @NonNull
    private final Model model;

    @NonNull
    private final RequestVoteOkMessage requestVoteOkMessage;

    @Override
    public List<Message> handle() {
        if (model.getMode() != Mode.CANDIDATE) {
            return List.of();
        }

        model.removeMessageTimeout(requestVoteOkMessage.getMessageID(), requestVoteOkMessage.getSource());
        model.addVote(requestVoteOkMessage.getSource());

        if (model.getVotesSize() <= model.getNumReplicas() / 2) {
            return List.of();
        }

        // Promote this replica to leader if a majority of votes was received
        model.setMode(Mode.LEADER);
        model.setLeader(model.getID());
        model.clearMessageTimeouts();
        model.setMessageTimeout(Config.RAFT_MESSAGE_TIMEOUT_MILLIS);

        int logSize = model.getLogSize();

        for (String remoteID : model.getRemotes()) {
            model.setNextIndex(remoteID, logSize);
            model.setMatchIndex(remoteID, 0);
        }

        TimeoutMessageHandler timeoutMessageHandler = new TimeoutMessageHandler(random, model);
        return timeoutMessageHandler.handle();
    }
}
