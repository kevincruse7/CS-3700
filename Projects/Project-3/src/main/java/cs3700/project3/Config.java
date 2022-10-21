package cs3700.project3;

import java.util.Map;

/**
 * Configuration options for the application.
 */
public interface Config {
    String DOMAIN = "localhost";
    int NETWORK_BUFFER_SIZE_BYTES = 1048576;
    int NETWORK_CONNECTION_TIMEOUT_MILLIS = 5000;
    Map<String, Integer> ORIGIN_RANKINGS = Map.of(
        "IGP", 0,
        "EGP", 1,
        "UNK", 2
    );
    int SOURCE_ADDRESS_LEAST_SIGNIFICANT_BYTE = 1;
}
