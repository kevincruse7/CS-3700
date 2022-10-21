package cs3700.project3.controller.router;

import cs3700.project3.Config;
import cs3700.project3.controller.message.MessageProcessorFactory;
import cs3700.project3.model.routingtable.RoutingTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;

/**
 * Basic implementation of a BGP router.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicRouter implements Router {
    @NonNull
    private final RoutingTable routingTable;

    @NonNull
    private final ByteBuffer ioBuffer;

    @NonNull
    private final Selector channelSelector;

    @NonNull
    private final Map<String, DatagramChannel> peerChannelMap;

    @NonNull
    private final Map<String, String> peerRelationshipMap;

    @Override
    @SneakyThrows
    public void run() {
        // Send handshake messages to peers
        sendMessages(MessageProcessorFactory.createHandshakeProcessorFor(peerRelationshipMap.keySet()).get());

        // Receive, process, and send messages
        int selectedChannels = 1;
        while (selectedChannels > 0) {
            selectedChannels = channelSelector.select(
                this::receiveFromChannelAndRespond,
                Config.NETWORK_CONNECTION_TIMEOUT_MILLIS
            );
        }

        // Close out selector and channels on exit
        channelSelector.close();
        for (DatagramChannel channel : peerChannelMap.values()) {
            channel.close();
        }
    }

    @SneakyThrows
    private void sendMessages(@NonNull Map<String, String> peerMessageMap) {
        for (final String peer : peerMessageMap.keySet()) {
            final String message = peerMessageMap.get(peer);

            // Populate IO buffer with message
            ioBuffer.clear();
            ioBuffer.put(message.getBytes());

            // Write IO buffer to channel
            ioBuffer.flip();
            peerChannelMap.get(peer).write(ioBuffer);
        }
    }

    @SneakyThrows
    private void receiveFromChannelAndRespond(@NonNull SelectionKey channelKey) {
        final String peer = (String) channelKey.attachment();
        final DatagramChannel channel = (DatagramChannel) channelKey.channel();

        // Receive message into IO buffer
        ioBuffer.clear();
        final int bytesRead;
        try {
            bytesRead = channel.read(ioBuffer);
        } catch (PortUnreachableException e) {
            return;
        }

        if (bytesRead <= 0) {
            return;
        }

        // Read message from IO buffer
        final byte[] messageBytes = new byte[bytesRead];
        ioBuffer.flip();
        ioBuffer.get(messageBytes);
        final String message = new String(messageBytes);

        // Process message and send necessary responses
        sendMessages(MessageProcessorFactory.createProcessorFor(
            peer, message, routingTable, peerRelationshipMap
        ).get());
    }
}
