package cs3700.project3.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.DataMessage;
import cs3700.project3.model.message.NoRouteMessage;
import cs3700.project3.model.routingtable.RoutingTable;
import cs3700.project3.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * Implementation of a message processor for data messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class DataMessageProcessor implements MessageProcessor {
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RoutingTable routingTable;

    @NonNull
    private final Map<String, String> peerRelationshipMap;

    @NonNull
    private final String srcPeer;

    @NonNull
    private final DataMessage dataMessage;

    @Override
    @SneakyThrows
    public Map<String, String> get() {
        final String dst;
        final Object message;
        final String nextHop = routingTable.nextHop(dataMessage.getDst());

        // Determine whether to forward message or to respond with a no route message
        if (nextHop.length() == 0
            || !peerRelationshipMap.get(srcPeer).equals("cust")
            && !peerRelationshipMap.get(nextHop).equals("cust")
        ) {
            dst = srcPeer;

            final NoRouteMessage noRouteMessage = new NoRouteMessage();
            noRouteMessage.setSrc(Util.getSrcAddressFrom(dst));
            noRouteMessage.setDst(dataMessage.getSrc());

            message = noRouteMessage;
        } else {
            dst = nextHop;
            message = dataMessage;
        }

        return Map.of(dst, objectMapper.writeValueAsString(message));
    }
}
