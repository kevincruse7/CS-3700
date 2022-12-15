package cs3700.project6.controller.handler;

import cs3700.project6.Config;
import cs3700.project6.model.LogEntry;
import cs3700.project6.model.Mode;
import cs3700.project6.model.Model;
import cs3700.project6.model.message.AppendEntriesFailMessage;
import cs3700.project6.model.message.AppendEntriesMessage;
import cs3700.project6.model.message.AppendEntriesOkMessage;
import cs3700.project6.model.message.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Message handler implementation for {@code appendEntries} messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class AppendEntriesMessageHandler implements MessageHandler {
    @NonNull
    private final Random random;

    @NonNull
    private final Model model;

    @NonNull
    private final AppendEntriesMessage appendEntriesMessage;

    @Override
    public List<Message> handle() {
        ArrayList<Message> messages = new ArrayList<>();

        if (model.getTerm() < appendEntriesMessage.getTerm()) {
            messages.addAll(model.setTerm(appendEntriesMessage.getTerm()));
        }

        if (model.getTerm() == appendEntriesMessage.getTerm() && model.getLeader().equals(Config.RAFT_ID_UNSPECIFIED)) {
            model.setLeader(appendEntriesMessage.getLeader());
        }

        // Acknowledge received message as heartbeat
        if (model.getMode() != Mode.LEADER) {
            model.setLastHeartbeatTimestamp(Instant.now().toEpochMilli());

            model.setMessageTimeout(random.nextInt(
                Config.RAFT_ELECTION_TIMEOUT_MILLIS_MAX - Config.RAFT_ELECTION_TIMEOUT_MILLIS_MIN + 1
            ) + Config.RAFT_ELECTION_TIMEOUT_MILLIS_MIN);
        }

        List<LogEntry> logEntries = appendEntriesMessage.getEntries();

        if (logEntries.isEmpty()) {
            return messages;
        }

        int prevLogIndex = appendEntriesMessage.getPrevLogIndex();

        // If received message is consistent with this replica's log, append new entries
        if (model.getTerm() > appendEntriesMessage.getTerm()
            || (appendEntriesMessage.getPrevLogIndex() >= 0
                && (model.getLogSize() <= prevLogIndex
                    || model.getLogEntry(prevLogIndex).getTerm() != appendEntriesMessage.getPrevLogTerm()
                )
            )
        ) {
            messages.add(AppendEntriesFailMessage.builder()
                .source(model.getID())
                .destination(appendEntriesMessage.getSource())
                .leader(model.getLeader())
                .messageID(appendEntriesMessage.getMessageID())
                .term(model.getTerm())
                .build()
            );
        } else {
            for (int i = model.getLogSize() - 1; i > prevLogIndex; --i) {
                model.removeLogEntry(i);
            }

            for (LogEntry logEntry : logEntries) {
                model.addLogEntry(logEntry.getTerm(), logEntry.getKey(), logEntry.getValue());
            }

            if (appendEntriesMessage.getLeaderCommit() > model.getCommitIndex()) {
                model.setCommitIndex(Math.min(appendEntriesMessage.getLeaderCommit(), model.getLogSize() - 1));
            }

            while (model.getCommitIndex() > model.getLastApplied()) {
                LogEntry logEntry = model.getLogEntry(model.getLastApplied() + 1);
                model.putKeyValuePair(logEntry.getKey(), logEntry.getValue());
                model.setLastApplied(model.getLastApplied() + 1);
            }

            messages.add(AppendEntriesOkMessage.builder()
                .source(model.getID())
                .destination(appendEntriesMessage.getSource())
                .leader(model.getLeader())
                .messageID(appendEntriesMessage.getMessageID())
                .term(model.getTerm())
                .build()
            );
        }

        return messages;
    }
}
