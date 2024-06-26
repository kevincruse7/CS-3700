package cs3700.project4;

/**
 * Application configuration options.
 */
public interface Config {
    double TCP_CONGESTION_WINDOW_ROUNDING_TOLERANCE = 0.001;
    int TCP_DATA_SIZE_BYTES = 1374;
    int TCP_FAST_RETRANSMIT_THRESHOLD = 3;
    int TCP_FINAL_PACKET_TRANSMIT_ATTEMPTS = 5;
    int TCP_INITIAL_CONGESTION_WINDOW = 3;
    int TCP_INITIAL_SLOW_START_THRESHOLD = 13;
    int TCP_INITIAL_TIMEOUT_MILLIS = 1450;
    double TCP_ROUND_TRIP_ALPHA = 0.875;
    int UDP_DATAGRAM_SIZE_BYTES = 1500;
}
