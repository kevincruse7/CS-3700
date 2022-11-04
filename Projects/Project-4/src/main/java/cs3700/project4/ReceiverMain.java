package cs3700.project4;

import cs3700.project4.controller.receiver.Receiver;
import cs3700.project4.controller.receiver.ReceiverFactory;

import java.io.IOException;

/**
 * Entry point for the receiver application.
 */
public class ReceiverMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        try (Receiver receiver = ReceiverFactory.createReceiver()) {
            receiver.run();
        }

        Thread.sleep(Long.MAX_VALUE);
    }
}
