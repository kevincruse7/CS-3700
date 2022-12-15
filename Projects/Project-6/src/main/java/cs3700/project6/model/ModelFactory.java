package cs3700.project6.model;

import cs3700.project6.Config;
import cs3700.project6.model.message.PutMessage;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Factory class for creating application models.
 */
public class ModelFactory {
    /**
     * Creates an application model from the given values.
     *
     * @param random Random object to set initial message timeout with.
     * @param id ID of this replica.
     * @param remotes Set of IDs for remote replicas.
     * @return Created application model.
     */
    public static Model createModel(@NonNull Random random, @NonNull String id, @NonNull Set<String> remotes) {
        int numReplicas = remotes.size() + 1;
        ArrayList<LogEntry> log = new ArrayList<>();
        HashMap<String, List<MessageTimeout>> messageTimeouts = new HashMap<>();
        HashMap<String, String> keyValueStore = new HashMap<>();
        HashSet<String> votes = new HashSet<>();
        HashMap<String, Integer> nextIndices = new HashMap<>();
        HashMap<String, Integer> matchIndices = new HashMap<>();
        ArrayList<PutMessage> activePutMessages = new ArrayList<>();

        int messageTimeout = random.nextInt(
            Config.RAFT_ELECTION_TIMEOUT_MILLIS_MAX - Config.RAFT_ELECTION_TIMEOUT_MILLIS_MIN + 1
        ) + Config.RAFT_ELECTION_TIMEOUT_MILLIS_MIN;

        return new BasicModel(
            id,
            numReplicas,
            remotes,
            log,
            messageTimeouts,
            keyValueStore,
            votes,
            nextIndices,
            matchIndices,
            activePutMessages,
            -1,
            Mode.FOLLOWER,
            Config.RAFT_ID_UNSPECIFIED,
            Config.RAFT_ID_UNSPECIFIED,
            -1,
            -1,
            -1,
            messageTimeout
        );
    }
}
