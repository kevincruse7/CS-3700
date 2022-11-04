package cs3700.project4.controller.receiver;

import cs3700.project4.controller.tcp.TCPConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Basic implementation of the receiver process.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicReceiver implements Receiver {
    @NonNull
    private final PrintStream outputStream;

    @NonNull
    private final TCPConnection tcpConnection;

    @Override
    @SneakyThrows
    public void run() {
        String data = tcpConnection.receive();
        outputStream.print(data);
    }

    @Override
    public void close() throws IOException {
        tcpConnection.close();
    }
}
