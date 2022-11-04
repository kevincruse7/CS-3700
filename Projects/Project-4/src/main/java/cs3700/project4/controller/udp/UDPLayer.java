package cs3700.project4.controller.udp;

import cs3700.project4.model.TCPPacket;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents an interface with the UDP layer of a TCP connection.
 */
public interface UDPLayer extends Closeable {
    /**
     * Write the given TCP packet to the UDP socket.
     *
     * @param packet Packet to write.
     * @throws IOException Error occurred when writing packet to socket.
     */
    void writePacket(TCPPacket packet) throws IOException;

    /**
     * Read a TCP packet from the UDP socket.
     *
     * @param roundTripTime Round trip time estimate to use for blocking duration. Values of 0 disable blocking.
     * @return TCP packet read from socket.
     * @throws IOException Error occurred when reading packet from socket.
     */
    TCPPacket readPacket(int roundTripTime) throws IOException;
}
