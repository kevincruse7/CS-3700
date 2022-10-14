package cs3700.project3.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.RouteUpdateMessage;
import cs3700.project3.model.route.RouteUpdate;
import cs3700.project3.model.routingtable.RoutingTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class RouteUpdateMessageProcessor implements MessageProcessor {
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RoutingTable routingTable;

    @NonNull
    private final Map<String, String> peerRelationshipMap;

    @NonNull
    private final RouteUpdateMessage routeUpdateMessage;

    @Override
    public Map<String, String> get() {
        final String src = routeUpdateMessage.getSrc();
        final RouteUpdate routeUpdate = routeUpdateMessage.getRouteUpdate();

        routingTable.update(src, routeUpdate);

        // Add our AS number to the path, if it's not already there
        if (!routeUpdate.getAsPath().contains(routingTable.getAs())) {
            final List<Integer> newAsPath = new ArrayList<>(routeUpdate.getAsPath());
            newAsPath.add(0, routingTable.getAs());
            routeUpdate.setAsPath(newAsPath);
        }

        return peerRelationshipMap.keySet()
            .stream()
            .filter(peerRelationshipMap.get(src).equals("cust")
                ? peer -> !peer.equals(src)
                : peer -> !peer.equals(src) && peerRelationshipMap.get(peer).equals("cust"))
            .collect(Collectors.toMap(Function.identity(), this::createRouteUpdateMessage));
    }

    @SneakyThrows
    private String createRouteUpdateMessage(@NonNull String peer) {
        final RouteUpdateMessage newRouteUpdateMessage = new RouteUpdateMessage();
        newRouteUpdateMessage.setSrc(MessageProcessorUtil.getSrcAddressFrom(peer));
        newRouteUpdateMessage.setDst(peer);

        final RouteUpdate routeUpdate = routeUpdateMessage.getRouteUpdate();
        routeUpdate.setLocalPref(null);
        routeUpdate.setSelfOrigin(null);
        routeUpdate.setOrigin(null);
        newRouteUpdateMessage.setRouteUpdate(routeUpdate);

        return objectMapper.writeValueAsString(newRouteUpdateMessage);
    }
}
