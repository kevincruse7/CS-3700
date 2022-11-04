package cs3700.project4.controller.tcp;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a stripped-down TCP Reno connection.
 */
public interface TCPConnection extends Closeable {
    /**
     * Send the given message to the connected recipient.
     *
     * @param message Message to send.
     * @throws IOException Error occurred when sending message.
     */
    void send(String message) throws IOException;

    /**
     * Receive a message from the connected sender.
     *
     * @return Received message.
     * @throws IOException Error occurred when receiving message.
     */
    String receive() throws IOException;
}
