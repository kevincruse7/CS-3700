package cs3700.project4;

import cs3700.project4.controller.sender.Sender;
import cs3700.project4.controller.sender.SenderFactory;

import java.io.IOException;

/**
 * Entry point for the sender application.
 */
public class SenderMain {
    public static void main(String[] args) throws IOException {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Sender sender = SenderFactory.createSenderTo(hostname, port)) {
            sender.run();
        }
    }
}
