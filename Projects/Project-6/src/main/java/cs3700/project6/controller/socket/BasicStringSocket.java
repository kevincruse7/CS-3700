package cs3700.project6.controller.socket;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Optional;

/**
 * Basic implementation of a string UDP socket connection.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicStringSocket implements StringSocket {
    @NonNull
    private final Selector selector;

    @NonNull
    private final DatagramChannel datagramChannel;

    @NonNull
    private final ByteBuffer ioBuffer;

    @Override
    public Optional<String> read(int timeout) throws IOException {
        selector.selectedKeys().clear();

        if (selector.select(timeout) == 0) {
            return Optional.empty();
        }

        ioBuffer.clear();
        int readBytes = datagramChannel.read(ioBuffer);
        ioBuffer.flip();

        if (readBytes <= 0) {
            return Optional.empty();
        }

        byte[] receivedBytes = new byte[readBytes];
        ioBuffer.get(receivedBytes);
        String receivedMessage = new String(receivedBytes);

        return Optional.of(receivedMessage);
    }

    @Override
    public void write(@NonNull String data) throws IOException {
        ioBuffer.clear();
        ioBuffer.put(data.getBytes());
        ioBuffer.flip();

        datagramChannel.write(ioBuffer);
    }

    @Override
    public void close() throws IOException {
        datagramChannel.close();
    }
}
