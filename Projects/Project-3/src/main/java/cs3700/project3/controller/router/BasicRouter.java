package cs3700.project3.controller.router;

import cs3700.project3.controller.message.MessageProcessorFactory;
import cs3700.project3.model.routingtable.RoutingTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;

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

        while (channelSelector.keys().size() > 0) {
            channelSelector.select(this::receiveFromChannelAndRespond);
        }

        channelSelector.close();
    }

    @SneakyThrows
    private void sendMessages(@NonNull Map<String, String> peerMessageMap) {
        for (final String peer : peerMessageMap.keySet()) {
            final String message = peerMessageMap.get(peer);

            ioBuffer.clear();
            ioBuffer.put(message.getBytes());

            ioBuffer.flip();
            peerChannelMap.get(peer).write(ioBuffer);

            System.out.println("Sent message: " + message);
        }
    }

    @SneakyThrows
    private void receiveFromChannelAndRespond(SelectionKey channelKey) {
        final DatagramChannel channel = (DatagramChannel) channelKey.channel();

        ioBuffer.clear();
        final int bytesRead = channel.read(ioBuffer);

        if (bytesRead == 0) {
            return;
        } else if (bytesRead == -1) {
            System.out.println("Channel indicated closure: " + channel);

            channel.close();
            channelKey.cancel();

            return;
        }

        final byte[] messageBytes = new byte[bytesRead];

        ioBuffer.flip();
        ioBuffer.get(messageBytes);

        final String message = new String(messageBytes);
        System.out.println("Received message: " + message);

        sendMessages(MessageProcessorFactory.createProcessorFor(message, routingTable, peerRelationshipMap).get());
    }
}
