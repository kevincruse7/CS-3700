package cs3700.project3.controller.router;

import cs3700.project3.Config;
import cs3700.project3.model.routingtable.RoutingTable;
import cs3700.project3.model.routingtable.RoutingTableFactory;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating BGP routers.
 */
public class RouterFactory {
    /**
     * Creates a BGP router with the given autonomous system and peers.
     *
     * @param as Autonomous system that created router should affiliate with.
     * @param peers List of peers and their corresponding ports, addresses, and relationships
     * @return Created BGP router.
     */
    @SneakyThrows
    public static Router createRouter(int as, @NonNull List<String> peers) {
        final RoutingTable routingTable = RoutingTableFactory.createRoutingTable(as);
        final ByteBuffer ioBuffer = ByteBuffer.allocate(Config.NETWORK_BUFFER_SIZE_BYTES);
        final Selector channelSelector = Selector.open();
        final Map<String, DatagramChannel> peerChannelMap = new HashMap<>();
        final Map<String, String> peerRelationshipMap = new HashMap<>();

        for (final String peer : peers) {
            final String[] peerComponents = peer.split("-");

            // Extract components from peer string
            final int port = Integer.parseInt(peerComponents[0]);
            final String address = peerComponents[1];
            final String relationship = peerComponents[2];

            // Connect to specified UDP socket
            final DatagramChannel channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(Config.DOMAIN, 0));
            channel.connect(new InetSocketAddress(Config.DOMAIN, port));
            channel.configureBlocking(false);
            channel.register(channelSelector, SelectionKey.OP_READ).attach(address);

            peerChannelMap.put(address, channel);
            peerRelationshipMap.put(address, relationship);
        }

        return new BasicRouter(routingTable, ioBuffer, channelSelector, peerChannelMap, peerRelationshipMap);
    }
}
