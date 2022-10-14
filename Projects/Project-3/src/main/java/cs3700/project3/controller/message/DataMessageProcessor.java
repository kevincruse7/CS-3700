package cs3700.project3.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.DataMessage;
import cs3700.project3.model.message.NoRouteMessage;
import cs3700.project3.model.routingtable.RoutingTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class DataMessageProcessor implements MessageProcessor {
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RoutingTable routingTable;

    @NonNull
    private final DataMessage dataMessage;

    @Override
    @SneakyThrows
    public Map<String, String> get() {
        final String dst;
        final Object message;
        final String nextHop = routingTable.nextHop(dataMessage.getDst());

        if (nextHop.length() == 0) {
            dst = dataMessage.getSrc();
            final NoRouteMessage noRouteMessage = new NoRouteMessage();

            noRouteMessage.setSrc(MessageProcessorUtil.getSrcAddressFrom(dst));
            noRouteMessage.setDst(dst);

            message = noRouteMessage;
        } else {
            dst = nextHop;
            message = dataMessage;
        }

        return Map.of(dst, objectMapper.writeValueAsString(message));
    }
}