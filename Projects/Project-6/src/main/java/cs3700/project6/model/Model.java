package cs3700.project6.model;

import cs3700.project6.model.message.Message;
import cs3700.project6.model.message.PutMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a model for application state.
 */
public interface Model {
    /**
     * Retrieves the ID of this replica.
     *
     * @return ID of this replica.
     */
    String getID();

    /**
     * Retrieves the total number of replicas on the network.
     *
     * @return Total number of replicas on the network.
     */
    int getNumReplicas();

    /**
     * Retrieves the set of IDs for active remote replicas.
     *
     * @return Set of IDs for active remote replicas.
     */
    Set<String> getRemotes();

    /**
     * Sets the given remote replica as inactive.
     *
     * @param remoteID ID of remote replica to set as inactive.
     */
    void removeRemote(String remoteID);

    /**
     * Retrieves the size of this replica's log.
     *
     * @return Size of this replica's log.
     */
    int getLogSize();

    /**
     * Retrieves the log entry at the given index.
     *
     * @param index Index of log entry to retrieve.
     * @return Log entry at the given index.
     */
    LogEntry getLogEntry(int index);

    /**
     * Adds a new log entry with the given values.
     *
     * @param term Term of new log entry.
     * @param key Key of new log entry.
     * @param value Value of new log entry.
     */
    void addLogEntry(int term, String key, String value);

    /**
     * Removes the log entry at the given index.
     *
     * @param index Index of log entry to remove.
     */
    void removeLogEntry(int index);

    /**
     * Retrieves the map of message timeouts.
     *
     * @return Map of message timeouts.
     */
    Map<String, List<MessageTimeout>> getMessageTimeouts();

    /**
     * Puts a new timeout for the given message.
     *
     * @param message Message to put timeout for.
     */
    void putMessageTimeout(Message message);

    /**
     * Removes the timeout for the given message.
     *
     * @param messageID ID of message to remove timeout for.
     * @param messageSource Source of message to remove timeout for.
     * @return Associated message of removed timeout, if it exists.
     */
    Optional<Message> removeMessageTimeout(String messageID, String messageSource);

    /**
     * Removes all message timeouts.
     */
    void clearMessageTimeouts();

    /**
     * Retrieves the datastore value for the given key.
     *
     * @param key Key for value to retrieve.
     * @return Associated value of given key, or the empty string if it doesn't exist.
     */
    String getValueFor(String key);

    /**
     * Puts the given key-value pair into the datastore.
     *
     * @param key Key of key-value pair to put.
     * @param value Value of key-value pair to put.
     */
    void putKeyValuePair(String key, String value);

    /**
     * Retrieves the term of this replica.
     *
     * @return Term of this replica.
     */
    int getTerm();

    /**
     * Sets the term of this replica to the given value.
     *
     * @param term Term to set this replica to.
     * @return List of messages to be sent out in response to term change.
     */
    List<Message> setTerm(int term);

    /**
     * Retrieves the mode of this replica.
     *
     * @return Mode of this replica.
     */
    Mode getMode();

    /**
     * Sets the mode of this replica to the given value.
     *
     * @param mode Mode to set this replica to.
     */
    void setMode(Mode mode);

    /**
     * Retrieves the leader of this replica.
     *
     * @return Leader of this replica.
     */
    String getLeader();

    /**
     * Sets the leader of this replica to the given value.
     *
     * @param leaderID ID of leader to set for this replica.
     */
    void setLeader(String leaderID);

    /**
     * Retrieves the ID of the candidate which this replica voted for in the current term.
     *
     * @return ID of replica voted for, or the unspecified ID sentinel if no vote was yet cast.
     */
    String getVotedFor();

    /**
     * Sets the ID of the candidate which this replica voted for in the current term.
     *
     * @param candidateID ID of replica voted for.
     */
    void setVotedFor(String candidateID);

    /**
     * Retrieves the number of votes cast for this replica.
     *
     * @return Number of votes cast for this replica.
     */
    int getVotesSize();

    /**
     * Casts a vote for this replica from the given replica ID.
     *
     * @param remoteID Replica ID to vote for this replica.
     */
    void addVote(String remoteID);

    /**
     * Retrieves the commit index of this replica.
     *
     * @return Commit index of this replica.
     */
    int getCommitIndex();

    /**
     * Sets the commit index of this replica to the given value.
     *
     * @param commitIndex Commit index to set for this replica.
     */
    void setCommitIndex(int commitIndex);

    /**
     * Retrieves the last applied index of this replica.
     *
     * @return Last applied index of this replica.
     */
    int getLastApplied();

    /**
     * Sets the last applied index of this replica to the given value.
     *
     * @param lastApplied Last applied index to set for this replica.
     */
    void setLastApplied(int lastApplied);

    /**
     * Retrieves the next index value for the given replica ID.
     *
     * @param remoteID Replica ID to retrieve next index value for.
     * @return Next index value for the given replica ID.
     */
    int getNextIndex(String remoteID);

    /**
     * Sets the next index value for the given replica ID to the given value.
     *
     * @param remoteID Replica ID to set next index value for.
     * @param index Next index value to set for the given replica.
     */
    void setNextIndex(String remoteID, int index);

    /**
     * Retrieves the match index value for the given replica ID.
     *
     * @param remoteID Replica ID to retrieve match index value for.
     * @return Match index value for the given replica ID.
     */
    int getMatchIndex(String remoteID);

    /**
     * Sets the match index value for the given replica ID to the given value.
     *
     * @param remoteID Replica ID to set match index value for.
     * @param index Match index value to set for the given replica.
     */
    void setMatchIndex(String remoteID, int index);

    /**
     * Retrieves the timestamp of the last received heartbeat to this replica.
     *
     * @return Timestamp of the last received heartbeat.
     */
    long getLastHeartbeatTimestamp();

    /**
     * Sets the timestamp of the last received heartbeat to the given value.
     *
     * @param timestamp Timestamp of last received heartbeat to set for this replica.
     */
    void setLastHeartbeatTimestamp(long timestamp);

    /**
     * Retrieves the timeout duration for this replica.
     *
     * @return Timeout duration for this replica.
     */
    int getMessageTimeout();

    /**
     * Sets the timeout duration for this replica.
     *
     * @param timeout Timeout duration to set for this replica.
     */
    void setMessageTimeout(int timeout);

    /**
     * Adds put message to the list of active put messages for this replica.
     *
     * @param message Put message to add to list of active put messages.
     */
    void addActivePutMessage(PutMessage message);

    /**
     * Removes the put message associated with the given key and value
     * from the list of active put messages for this replica.
     *
     * @param key Key affiliated with the active put message to remove.
     * @param value Value affiliated with the active put message to remove.
     * @return Removed put message.
     */
    PutMessage removeActivePutMessage(String key, String value);
}
