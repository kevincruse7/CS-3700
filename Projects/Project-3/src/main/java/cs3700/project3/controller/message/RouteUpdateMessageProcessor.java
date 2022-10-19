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
        final List<Integer> forwardedAsPath;
        if (!routeUpdate.getAsPath().contains(routingTable.getAs())) {
            forwardedAsPath = new ArrayList<>(routeUpdate.getAsPath());
            forwardedAsPath.add(0, routingTable.getAs());
        } else {
            forwardedAsPath = routeUpdate.getAsPath();
        }

        return peerRelationshipMap.keySet()
            .stream()
            .filter(peerRelationshipMap.get(src).equals("cust")
                ? peer -> !peer.equals(src)
                : peer -> !peer.equals(src) && peerRelationshipMap.get(peer).equals("cust"))
            .collect(Collectors.toMap(Function.identity(), peer -> createRouteUpdateMessage(peer, forwardedAsPath)));
    }

    @SneakyThrows
    private String createRouteUpdateMessage(@NonNull String peer, @NonNull List<Integer> forwardedAsPath) {
        final RouteUpdate routeUpdate = routeUpdateMessage.getRouteUpdate();

        final RouteUpdate forwardedRouteUpdate = new RouteUpdate();
        forwardedRouteUpdate.setNetwork(routeUpdate.getNetwork());
        forwardedRouteUpdate.setNetmask(routeUpdate.getNetmask());
        forwardedRouteUpdate.setAsPath(forwardedAsPath);

        final RouteUpdateMessage forwardedRouteUpdateMessage = new RouteUpdateMessage();
        forwardedRouteUpdateMessage.setSrc(MessageProcessorUtil.getSrcAddressFrom(peer));
        forwardedRouteUpdateMessage.setDst(peer);
        forwardedRouteUpdateMessage.setRouteUpdate(forwardedRouteUpdate);

        return objectMapper.writeValueAsString(forwardedRouteUpdateMessage);
    }
}
