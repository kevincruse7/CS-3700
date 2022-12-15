package cs3700.project6.controller.handler;

import cs3700.project6.Util;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.AppendEntriesFailMessage;
import cs3700.project6.model.message.AppendEntriesMessage;
import cs3700.project6.model.message.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Message handler implementation for {@code appendEntriesFail} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class AppendEntriesFailMessageHandler implements MessageHandler {
    @NonNull
    private final Random random;

    @NonNull
    private final Model model;

    @NonNull
    private final AppendEntriesFailMessage appendEntriesFailMessage;

    @Override
    public List<Message> handle() {
        if (appendEntriesFailMessage.getTerm() > model.getTerm()) {
            return model.setTerm(appendEntriesFailMessage.getTerm());
        }

        if (model.getMode() != Mode.LEADER) {
            return List.of();
        }

        Optional<Message> maybeAppendEntriesMessage = model.removeMessageTimeout(
            appendEntriesFailMessage.getMessageID(),
            appendEntriesFailMessage.getSource()
        );

        if (maybeAppendEntriesMessage.isEmpty()) {
            return List.of();
        }

        // Update next index for remote replica and send out a new 'appendEntries' message
        int remoteNextIndex = model.getNextIndex(appendEntriesFailMessage.getSource()) - 1;
        model.setNextIndex(appendEntriesFailMessage.getSource(), remoteNextIndex);

        AppendEntriesMessage newAppendEntriesMessage = AppendEntriesMessage.builder()
            .source(model.getID())
            .destination(appendEntriesFailMessage.getSource())
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

        model.putMessageTimeout(newAppendEntriesMessage);
        return List.of(newAppendEntriesMessage);
    }
}
