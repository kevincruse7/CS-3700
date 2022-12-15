package cs3700.project6.controller.handler;

import cs3700.project6.model.Model;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.RequestVoteFailMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * Message handler implementation for {@code requestVoteFail} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class RequestVoteFailMessageHandler implements MessageHandler {
    @NonNull
    private final Model model;

    @NonNull
    private final RequestVoteFailMessage requestVoteFailMessage;

    @Override
    public List<Message> handle() {
        if (model.getTerm() < requestVoteFailMessage.getTerm()) {
            return model.setTerm(requestVoteFailMessage.getTerm());
        }

        model.removeMessageTimeout(requestVoteFailMessage.getMessageID(), requestVoteFailMessage.getSource());
        return List.of();
    }
}
