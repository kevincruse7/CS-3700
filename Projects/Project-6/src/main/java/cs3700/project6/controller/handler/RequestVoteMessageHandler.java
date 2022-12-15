package cs3700.project6.controller.handler;

import cs3700.project6.Config;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.RequestVoteFailMessage;
import cs3700.project6.model.message.RequestVoteMessage;
import cs3700.project6.model.message.RequestVoteOkMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Message handler implementation for {@code requestVote} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class RequestVoteMessageHandler implements MessageHandler {
    @NonNull
    private final Model model;

    @NonNull
    private final RequestVoteMessage requestVoteMessage;

    @Override
    public List<Message> handle() {
        ArrayList<Message> messages = new ArrayList<>();

        if (model.getTerm() < requestVoteMessage.getTerm()) {
            messages.addAll(model.setTerm(requestVoteMessage.getTerm()));
        }

        int lastLogTerm = model.getLogSize() > 0 ? model.getLogEntry(model.getLogSize() - 1).getTerm() : 0;

        // Determine if this replica should vote for the requesting candidate
        if (model.getTerm() == requestVoteMessage.getTerm()
            && (model.getVotedFor().equals(Config.RAFT_ID_UNSPECIFIED)
                || model.getVotedFor().equals(requestVoteMessage.getSource())
            )
            && lastLogTerm <= requestVoteMessage.getLastLogTerm()
            && (lastLogTerm < requestVoteMessage.getLastLogTerm()
                || model.getLogSize() - 1 <= requestVoteMessage.getLastLogIndex()
            )
        ) {
            model.setVotedFor(requestVoteMessage.getSource());

            messages.add(RequestVoteOkMessage.builder()
                .source(model.getID())
                .destination(requestVoteMessage.getSource())
                .leader(model.getLeader())
                .messageID(requestVoteMessage.getMessageID())
                .term(model.getTerm())
                .build()
            );
        } else {
            messages.add(RequestVoteFailMessage.builder()
                .source(model.getID())
                .destination(requestVoteMessage.getSource())
                .leader(model.getLeader())
                .messageID(requestVoteMessage.getMessageID())
                .term(model.getTerm())
                .build()
            );
        }

        return messages;
    }
}
