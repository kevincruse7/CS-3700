package cs3700.project6.controller.socket;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

/**
 * Represents a connection to a UDP socket for sending and receiving string messages.
 */
public interface StringSocket extends Closeable {
    /**
     * Read a message from the socket within the given timeout duration.
     *
     * @param timeout Duration to wait for messages before timing out.
     * @return Read message, if it exists.
     * @throws IOException Failed to read from the socket.
     */
    Optional<String> read(int timeout) throws IOException;

    /**
     * Write a message to the socket.
     *
     * @param data Message to write to the socket.
     * @throws IOException Failed to write to the socket.
     */
    void write(String data) throws IOException;
}
