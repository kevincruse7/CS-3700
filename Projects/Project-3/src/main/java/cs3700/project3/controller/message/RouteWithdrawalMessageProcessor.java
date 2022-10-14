package cs3700.project3.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.RouteWithdrawalMessage;
import cs3700.project3.model.route.RouteWithdrawal;
import cs3700.project3.model.routingtable.RoutingTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class RouteWithdrawalMessageProcessor implements MessageProcessor {
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RoutingTable routingTable;

    @NonNull
    private final Map<String, String> peerRelationshipMap;

    @NonNull
    private final RouteWithdrawalMessage routeWithdrawalMessage;

    @Override
    public Map<String, String> get() {
        final String src = routeWithdrawalMessage.getSrc();

        for (RouteWithdrawal routeWithdrawal : routeWithdrawalMessage.getRouteWithdrawals()) {
            routingTable.withdraw(src, routeWithdrawal);
        }

        return peerRelationshipMap.keySet()
            .stream()
            .filter(peerRelationshipMap.get(src).equals("cust")
                ? peer -> !peer.equals(src)
                : peer -> !peer.equals(src) && peerRelationshipMap.get(peer).equals("cust"))
            .collect(Collectors.toMap(Function.identity(), this::createRouteWithdrawalMessage));
    }

    @SneakyThrows
    private String createRouteWithdrawalMessage(@NonNull String peer) {
        final RouteWithdrawalMessage routeWithdrawalMessage = new RouteWithdrawalMessage();

        routeWithdrawalMessage.setSrc(MessageProcessorUtil.getSrcAddressFrom(peer));
        routeWithdrawalMessage.setDst(peer);
        routeWithdrawalMessage.setRouteWithdrawals(routeWithdrawalMessage.getRouteWithdrawals());

        return objectMapper.writeValueAsString(routeWithdrawalMessage);
    }
}
