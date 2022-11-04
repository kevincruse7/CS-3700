package cs3700.project4.controller.sender;

import cs3700.project4.controller.tcp.TCPConnectionFactory;
import lombok.NonNull;

import java.io.IOException;

/**
 * Factory class for creating sender processes.
 */
public class SenderFactory {
    public static Sender createSenderTo(@NonNull String hostname, int port) throws IOException {
        return new BasicSender(System.in, TCPConnectionFactory.createTCPConnection(hostname, port));
    }
}
