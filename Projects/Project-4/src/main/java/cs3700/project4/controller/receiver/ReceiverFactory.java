package cs3700.project4.controller.receiver;

import cs3700.project4.controller.tcp.TCPConnectionFactory;

import java.io.IOException;

/**
 * Factory class for creating receiver processes.
 */
public class ReceiverFactory {
    public static Receiver createReceiver() throws IOException {
        return new BasicReceiver(System.out, TCPConnectionFactory.createTCPConnection());
    }
}
