package cs3700.project6.controller.socket;

import cs3700.project6.Config;
import lombok.NonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Factory class for creating string UDP socket connections.
 */
public class StringSocketFactory {
    /**
     * Creates a string UDP socket connection from the given values.
     *
     * @param host Host to connect to.
     * @param port Port of host to connect to.
     * @return Created string UDP socket connection.
     * @throws IOException Failed to connect to host.
     */
    public static StringSocket createStringSocket(@NonNull String host, int port) throws IOException {
        Selector selector = Selector.open();

        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.register(selector, SelectionKey.OP_READ);
        datagramChannel.connect(new InetSocketAddress(host, port));

        ByteBuffer ioBuffer = ByteBuffer.allocate(Config.SOCKET_BUFFER_SIZE_BYTES);

        return new BasicStringSocket(selector, datagramChannel, ioBuffer);
    }
}
