package cs3700.project3.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.DumpMessage;
import cs3700.project3.model.message.TableMessage;
import cs3700.project3.model.routingtable.RoutingTable;
import cs3700.project3.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * Implementation of a message processor for dump messages.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class DumpMessageProcessor implements MessageProcessor {
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RoutingTable routingTable;

    @NonNull
    private final DumpMessage dumpMessage;

    @Override
    @SneakyThrows
    public Map<String, String> get() {
        final TableMessage tableMessage = new TableMessage();
        tableMessage.setSrc(Util.getSrcAddressFrom(dumpMessage.getSrc()));
        tableMessage.setDst(dumpMessage.getSrc());
        tableMessage.setRouteEntries(routingTable.dumpContents());

        return Map.of(dumpMessage.getSrc(), objectMapper.writeValueAsString(tableMessage));
    }
}
