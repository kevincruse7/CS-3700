package cs3700.project4.controller.sender;

import cs3700.project4.controller.tcp.TCPConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic implementation of a sender process.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicSender implements Sender {
    @NonNull
    private final InputStream dataInputStream;

    @NonNull
    private final TCPConnection tcpConnection;

    @Override
    @SneakyThrows
    public void run() {
        final String message = new String(dataInputStream.readAllBytes());
        tcpConnection.send(message);
    }

    @Override
    public void close() throws IOException {
        tcpConnection.close();
    }
}
