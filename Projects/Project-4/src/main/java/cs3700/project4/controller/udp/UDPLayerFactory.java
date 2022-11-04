package cs3700.project4.controller.udp;

import cs3700.project4.Config;
import lombok.NonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Factory class for creating UDP layers for TCP connections.
 */
public class UDPLayerFactory {
    public static UDPLayer createUDPLayer() throws IOException {
        return createUDPLayer(null);
    }

    public static UDPLayer createUDPLayer(@NonNull String hostname, int port) throws IOException {
        return createUDPLayer(new InetSocketAddress(hostname, port));
    }

    private static UDPLayer createUDPLayer(SocketAddress remoteHost) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(0));
        datagramChannel.configureBlocking(false);

        if (remoteHost != null) {
            datagramChannel.connect(remoteHost);
        }

        System.err.println("Bound to port " + ((InetSocketAddress) datagramChannel.getLocalAddress()).getPort());

        Selector selector = Selector.open();
        datagramChannel.register(selector, SelectionKey.OP_READ);

        return new BasicUDPLayer(
            ByteBuffer.allocate(Config.UDP_DATAGRAM_SIZE_BYTES),
            datagramChannel,
            selector,
            remoteHost
        );
    }
}
