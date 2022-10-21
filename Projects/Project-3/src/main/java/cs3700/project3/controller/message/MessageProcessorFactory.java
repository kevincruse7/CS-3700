package cs3700.project3.controller.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.DataMessage;
import cs3700.project3.model.message.DumpMessage;
import cs3700.project3.model.message.RouteUpdateMessage;
import cs3700.project3.model.message.RouteWithdrawalMessage;
import cs3700.project3.model.routingtable.RoutingTable;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Set;

/**
 * Factory class for creating message processors.
 */
public class MessageProcessorFactory {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * Creates a message processor for generating handshakes to the given peers.
     *
     * @param peers Set of peer addresses to send handshakes to.
     * @return Created handshake message processor.
     */
    public static MessageProcessor createHandshakeProcessorFor(@NonNull Set<String> peers) {
        return new HandshakeMessageProcessor(OBJECT_MAPPER, peers);
    }

    /**
     * Creates a message processor for processing the given received message.
     *
     * @param srcPeer Address of peer from which the message was received.
     * @param message Received message.
     * @param routingTable Routing table to update, if need be.
     * @param peerRelationshipMap Map of peer addresses and their relationships to use, if need be.
     * @return Created message processor.
     */
    @SneakyThrows
    public static MessageProcessor createProcessorFor(
        @NonNull String srcPeer,
        @NonNull String message,
        @NonNull RoutingTable routingTable,
        @NonNull Map<String, String> peerRelationshipMap
    ) {
        final String messageType = OBJECT_MAPPER.readTree(message).get("type").textValue();
        switch (messageType) {
            case "update":
                return new RouteUpdateMessageProcessor(
                    OBJECT_MAPPER,
                    routingTable,
                    peerRelationshipMap,
                    OBJECT_MAPPER.readValue(message, RouteUpdateMessage.class)
                );
            case "withdraw":
                return new RouteWithdrawalMessageProcessor(
                    OBJECT_MAPPER,
                    routingTable,
                    peerRelationshipMap,
                    OBJECT_MAPPER.readValue(message, RouteWithdrawalMessage.class)
                );
            case "data":
                return new DataMessageProcessor(
                    OBJECT_MAPPER,
                    routingTable,
                    peerRelationshipMap,
                    srcPeer,
                    OBJECT_MAPPER.readValue(message, DataMessage.class)
                );
            case "dump":
                return new DumpMessageProcessor(
                    OBJECT_MAPPER,
                    routingTable,
                    OBJECT_MAPPER.readValue(message, DumpMessage.class)
                );
            default:
                throw new IllegalArgumentException("Unknown message type: " + messageType);
        }
    }
}
