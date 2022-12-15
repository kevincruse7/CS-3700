package cs3700.project6.controller.handler;

import cs3700.project6.model.LogEntry;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.AppendEntriesMessage;
import cs3700.project6.model.message.AppendEntriesOkMessage;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.PutMessage;
import cs3700.project6.model.message.PutOkMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message handler implementation for {@code appendEntriesOk} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class AppendEntriesOkMessageHandler implements MessageHandler {
    @NonNull
    private final Model model;

    @NonNull
    private final AppendEntriesOkMessage appendEntriesOkMessage;

    @Override
    public List<Message> handle() {
        if (model.getMode() != Mode.LEADER) {
            return List.of();
        }

        Optional<Message> maybeAppendEntriesMessage = model.removeMessageTimeout(
            appendEntriesOkMessage.getMessageID(),
            appendEntriesOkMessage.getSource()
        );

        if (maybeAppendEntriesMessage.isEmpty()) {
            return List.of();
        }

        AppendEntriesMessage appendEntriesMessage = (AppendEntriesMessage) maybeAppendEntriesMessage.get();
        int matchIndex = appendEntriesMessage.getPrevLogIndex() + appendEntriesMessage.getEntries().size();

        // Update remote replica's match and next indices
        model.setMatchIndex(appendEntriesOkMessage.getSource(), matchIndex);
        model.setNextIndex(appendEntriesOkMessage.getSource(), matchIndex + 1);

        ArrayList<Message> messages = new ArrayList<>();

        // Update our commit index, and send out put message responses if needed
        for (int i = model.getCommitIndex() + 1; i < model.getLogSize(); ++i) {
            LogEntry logEntry = model.getLogEntry(i);

            if (logEntry.getTerm() < model.getTerm()) {
                continue;
            }

            int iCopy = i;

            if (model.getRemotes()
                    .stream()
                    .filter(remoteID -> model.getMatchIndex(remoteID) >= iCopy)
                    .count()
                < model.getNumReplicas() / 2
            ) {
                break;
            }

            model.setCommitIndex(i);
            model.putKeyValuePair(logEntry.getKey(), logEntry.getValue());
            PutMessage putMessage = model.removeActivePutMessage(logEntry.getKey(), logEntry.getValue());

            messages.add(PutOkMessage.builder()
                .source(model.getID())
                .destination(putMessage.getSource())
                .leader(model.getLeader())
                .messageID(putMessage.getMessageID())
                .build()
            );
        }

        return messages;
    }
}
