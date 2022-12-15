package cs3700.project6.model;

import cs3700.project6.Config;
import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.PutFailMessage;
import cs3700.project6.model.message.PutMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Basic implementation of the application model.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicModel implements Model {
    @NonNull
    @Getter(AccessLevel.NONE)
    private final String id;

    private final int numReplicas;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Set<String> remotes;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final List<LogEntry> log;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Map<String, List<MessageTimeout>> messageTimeouts;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Map<String, String> keyValueStore;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Set<String> votes;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Map<String, Integer> nextIndices;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Map<String, Integer> matchIndices;

    @NonNull
    @Getter(AccessLevel.NONE)
    private final List<PutMessage> activePutMessages;

    private int term;

    @NonNull
    private Mode mode;

    @NonNull
    private String leader;

    @NonNull
    private String votedFor;

    private int commitIndex;
    private int lastApplied;
    private long lastHeartbeatTimestamp;
    private int messageTimeout;

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Set<String> getRemotes() {
        return Collections.unmodifiableSet(remotes);
    }

    @Override
    public void removeRemote(@NonNull String remoteID) {
        remotes.remove(remoteID);
    }

    @Override
    public int getLogSize() {
        return log.size();
    }

    @Override
    public LogEntry getLogEntry(int index) {
        return log.get(index);
    }

    @Override
    public void addLogEntry(int term, @NonNull String key, @NonNull String value) {
        LogEntry logEntry = LogEntry.builder()
            .term(term)
            .key(key)
            .value(value)
            .build();

        log.add(logEntry);
    }

    @Override
    public void removeLogEntry(int index) {
        log.remove(index);
    }

    @Override
    public Map<String, List<MessageTimeout>> getMessageTimeouts() {
        return Collections.unmodifiableMap(messageTimeouts);
    }

    @Override
    public void putMessageTimeout(@NonNull Message message) {
        messageTimeouts.putIfAbsent(message.getMessageID(), new ArrayList<>());
        List<MessageTimeout> messageTimeoutList = messageTimeouts.get(message.getMessageID());
        long timeout = Instant.now().toEpochMilli() + Config.RAFT_MESSAGE_TIMEOUT_MILLIS;

        messageTimeoutList.add(MessageTimeout.builder()
            .message(message)
            .numTimeouts(0)
            .timeout(timeout)
            .build()
        );
    }

    @Override
    public Optional<Message> removeMessageTimeout(@NonNull String messageID, @NonNull String messageSource) {
        List<MessageTimeout> messageTimeoutList = messageTimeouts.get(messageID);

        if (messageTimeoutList == null) {
            return Optional.empty();
        }

        Message message = null;

        for (int i = 0; i < messageTimeoutList.size(); ++i) {
            message = messageTimeoutList.get(i).getMessage();

            if (message.getDestination().equals(messageSource)) {
                messageTimeoutList.remove(i);
                break;
            }
        }

        if (message == null) {
            return Optional.empty();
        }

        if (messageTimeoutList.isEmpty()) {
            messageTimeouts.remove(messageID);
        }

        return Optional.of(message);
    }

    @Override
    public void clearMessageTimeouts() {
        messageTimeouts.clear();
    }

    @Override
    public String getValueFor(@NonNull String key) {
        return keyValueStore.getOrDefault(key, "");
    }

    @Override
    public void putKeyValuePair(@NonNull String key, @NonNull String value) {
        keyValueStore.put(key, value);
    }

    @Override
    public List<Message> setTerm(int term) {
        this.term = term;

        // Our leadership has ended, so send out failure messages for all active put messages
        List<Message> messages = activePutMessages.stream()
            .map(putMessage -> PutFailMessage.builder()
                .source(getID())
                .destination(putMessage.getSource())
                .leader(getLeader())
                .messageID(putMessage.getMessageID())
                .build())
            .collect(Collectors.toList());

        setMode(Mode.FOLLOWER);
        setLeader(Config.RAFT_ID_UNSPECIFIED);
        clearMessageTimeouts();
        setVotedFor(Config.RAFT_ID_UNSPECIFIED);
        votes.clear();
        nextIndices.clear();
        matchIndices.clear();
        activePutMessages.clear();

        return messages;
    }

    @Override
    public int getVotesSize() {
        return votes.size();
    }

    @Override
    public void addVote(@NonNull String remoteID) {
        votes.add(remoteID);
    }

    @Override
    public int getNextIndex(@NonNull String remoteID) {
        return nextIndices.get(remoteID);
    }

    @Override
    public void setNextIndex(@NonNull String remoteID, int index) {
        nextIndices.put(remoteID, index);
    }

    @Override
    public int getMatchIndex(@NonNull String remoteID) {
        return matchIndices.get(remoteID);
    }

    @Override
    public void setMatchIndex(@NonNull String remoteID, int index) {
        matchIndices.put(remoteID, index);
    }

    @Override
    public void addActivePutMessage(@NonNull PutMessage message) {
        activePutMessages.add(message);
    }

    @Override
    public PutMessage removeActivePutMessage(@NonNull String key, @NonNull String value) {
        PutMessage foundPutMessage = activePutMessages.stream()
            .filter(putMessage -> putMessage.getKey().equals(key) && putMessage.getValue().equals(value))
            .findFirst()
            .orElseThrow();

        activePutMessages.remove(foundPutMessage);
        return foundPutMessage;
    }
}
