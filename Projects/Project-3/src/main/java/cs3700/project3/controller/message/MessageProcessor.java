package cs3700.project3.controller.message;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents a processor that, given a received message, updates the
 * routing table and generates a map of responses to send to peers.
 */
public interface MessageProcessor extends Supplier<Map<String, String>> {
}
