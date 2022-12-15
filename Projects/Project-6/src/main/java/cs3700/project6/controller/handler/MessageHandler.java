package cs3700.project6.controller.handler;

import cs3700.project6.model.message.Message;

import java.util.List;

/**
 * Represents a handler for processing received messages.
 */
public interface MessageHandler {
    /**
     * Handles the received message.
     *
     * @return List of messages to be sent out in response to received message.
     */
    List<Message> handle();
}
