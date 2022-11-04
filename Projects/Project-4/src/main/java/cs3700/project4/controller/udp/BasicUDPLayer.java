package cs3700.project4.controller.udp;

import cs3700.project4.model.TCPPacket;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

/**
 * Basic implementation of a UDP layer for TCP connections.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicUDPLayer implements UDPLayer {
    @NonNull
    private final ByteBuffer buffer;

    @NonNull
    private final DatagramChannel datagramChannel;

    @NonNull
    private final Selector selector;

    private SocketAddress remoteHost;

    @Override
    public void writePacket(@NonNull TCPPacket packet) throws IOException {
        if (remoteHost == null) {
            throw new IllegalStateException("Remote host was not specified.");
        }

        // Write packet to buffer
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(packet);
        byte[] serializedPacket = byteArrayOutputStream.toByteArray();
        buffer.clear();
        buffer.put(serializedPacket);

        // Writer buffer to socket
        buffer.flip();
        datagramChannel.send(buffer, remoteHost);
    }

    @Override
    public TCPPacket readPacket(int roundTripTimeMillis) throws IOException {
        // Block thread, if requested
        if (roundTripTimeMillis > 0) {
            selector.select(roundTripTimeMillis * 2L);
        }

        // Read from socket to buffer
        buffer.clear();
        SocketAddress datagramSender = datagramChannel.receive(buffer);
        if (datagramSender == null) {
            return null;
        }

        // Establish connection if not already done
        if (remoteHost == null) {
            remoteHost = datagramSender;
            datagramChannel.connect(remoteHost);
        }

        // Read from buffer to packet
        buffer.flip();
        byte[] serializedPacket = new byte[buffer.limit()];
        buffer.get(serializedPacket);
        TCPPacket packet;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedPacket);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            packet = (TCPPacket) objectInputStream.readObject();
        } catch (Exception e) {
            return null;
        }

        // Verify integrity of packet
        if (packet.getHash() != packet.hashCode()) {
            return null;
        }

        return packet;
    }

    @Override
    public void close() throws IOException {
        selector.close();
        datagramChannel.close();
    }
}
