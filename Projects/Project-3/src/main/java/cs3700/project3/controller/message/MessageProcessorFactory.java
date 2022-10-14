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

public class MessageProcessorFactory {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static MessageProcessor createHandshakeProcessorFor(@NonNull Set<String> peers) {
        return new HandshakeMessageProcessor(OBJECT_MAPPER, peers);
    }

    @SneakyThrows
    public static MessageProcessor createProcessorFor(
        @NonNull String message, @NonNull RoutingTable routingTable, @NonNull Map<String, String> peerRelationshipMap
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
