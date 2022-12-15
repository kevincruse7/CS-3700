package cs3700.project6;

/**
 * Compile-time configuration options for the application.
 */
public interface Config {
    int RAFT_ELECTION_TIMEOUT_MILLIS_MIN = 500;
    int RAFT_ELECTION_TIMEOUT_MILLIS_MAX = 1000;
    int RAFT_MESSAGE_MAX_TIMEOUTS = 10;
    int RAFT_MESSAGE_TIMEOUT_MILLIS = 250;

    @SuppressWarnings("SpellCheckingInspection")
    String RAFT_ID_MULTICAST = "FFFF";

    @SuppressWarnings("SpellCheckingInspection")
    String RAFT_ID_UNSPECIFIED = "FFFF";

    int SOCKET_BUFFER_SIZE_BYTES = 1048576;
    String SOCKET_HOST = "localhost";
}
