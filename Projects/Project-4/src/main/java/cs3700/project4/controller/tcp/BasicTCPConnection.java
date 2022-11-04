package cs3700.project4.controller.tcp;

import cs3700.project4.Config;
import cs3700.project4.controller.udp.UDPLayer;
import cs3700.project4.model.TCPPacket;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Basic implementation of a stripped-down TCP Reno connection.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicTCPConnection implements TCPConnection {
    @NonNull
    private final UDPLayer udpLayer;

    @NonNull
    private final List<Integer> packetSequences;

    @NonNull
    private final Map<Integer, TCPPacket> sequencePacketMap;

    @NonNull
    private final Map<Integer, Long> packetSequenceSendTimeMap;

    @NonNull
    private final Map<Integer, Long> packetSequenceTimeoutMap;

    private int currentSequence;
    private int roundTripTimeMillis;
    private int currentPacketIndex;
    private int slowStartThreshold;
    private double congestionWindow;

    @Override
    public void send(@NonNull String message) throws IOException {
        currentSequence = 0;
        createPacketsFrom(message);

        roundTripTimeMillis = -1;
        currentPacketIndex = 0;
        slowStartThreshold = Config.TCP_INITIAL_SLOW_START_THRESHOLD;
        congestionWindow = Config.TCP_INITIAL_CONGESTION_WINDOW;

        int lastAcknowledgement = -1;
        int lastAcknowledgementOccurrences = 1;

        // Continuously send and receive packets until sending is complete
        while (packetSequences.size() > 0) {
            handleTimeouts();
            sendPacketsInWindow();

            // Receive any queued up packets
            TCPPacket receivedPacket = udpLayer.readPacket(roundTripTimeMillis > 0
                ? roundTripTimeMillis
                : Config.TCP_INITIAL_TIMEOUT_MILLIS
            );
            while (receivedPacket != null) {
                int receivedAcknowledgement = receivedPacket.getSequence();

                // Ignore out-of-bounds ACKs
                if (!sequencePacketMap.containsKey(receivedAcknowledgement)) {
                    receivedPacket = udpLayer.readPacket(0);
                    continue;
                }

                // Don't reprocess repeated ACKs
                if (receivedAcknowledgement == lastAcknowledgement) {
                    lastAcknowledgementOccurrences += 1;

                    // Set up fast retransmit if three duplicate ACKs were received
                    if (lastAcknowledgementOccurrences == Config.TCP_FAST_RETRANSMIT_THRESHOLD) {
                        currentPacketIndex = packetSequences.indexOf(lastAcknowledgement) + 1;
                        congestionWindow = Math.max(congestionWindow / 2, 1);
                    }

                    receivedPacket = udpLayer.readPacket(0);
                    continue;
                }

                lastAcknowledgement = receivedAcknowledgement;
                lastAcknowledgementOccurrences = 1;

                // Update round trip time estimate
                if (packetSequenceSendTimeMap.get(receivedAcknowledgement) != null) {
                    int roundTripTimeSampleMillis = (int) (
                        Instant.now().toEpochMilli() - packetSequenceSendTimeMap.get(receivedAcknowledgement)
                    );
                    roundTripTimeMillis = roundTripTimeMillis == -1
                        ? roundTripTimeSampleMillis
                        : (int) (Config.TCP_ROUND_TRIP_ALPHA * roundTripTimeMillis
                        + (1 - Config.TCP_ROUND_TRIP_ALPHA) * roundTripTimeSampleMillis
                    );
                }

                // Update congestion window based on operating mode
                if (congestionWindow < slowStartThreshold) {
                    // Slow start mode
                    congestionWindow = (int) congestionWindow + 1;
                } else {
                    // Congestion avoidance mode
                    congestionWindow += 1 / congestionWindow;
                }

                // Remove packets from send queue that have been acknowledged
                int receivedAcknowledgementIndex = packetSequences.indexOf(receivedAcknowledgement);
                for (int i = 0; i <= receivedAcknowledgementIndex; ++i) {
                    int packetSequence = packetSequences.remove(0);
                    sequencePacketMap.remove(packetSequence);
                    packetSequenceSendTimeMap.remove(packetSequence);
                    packetSequenceTimeoutMap.remove(packetSequence);
                }
                currentPacketIndex = Math.max(currentPacketIndex - receivedAcknowledgementIndex - 1, 0);

                receivedPacket = udpLayer.readPacket(0);
            }
        }

        // Send final packet to indicate the end of transmission
        TCPPacket finPacket = new TCPPacket(currentSequence, "");
        retransmitUntilAcknowledged(finPacket);
    }

    @Override
    public String receive() throws IOException {
        // Continuously receive packets and send ACKs until final empty packet is received
        int lastConsecutivePacketSequence = -1;
        TCPPacket receivedPacket = udpLayer.readPacket(Config.TCP_INITIAL_TIMEOUT_MILLIS);
        while (receivedPacket == null || !"".equals(receivedPacket.getData())) {
            // Ignore null packets
            if (receivedPacket == null) {
                receivedPacket = udpLayer.readPacket(Config.TCP_INITIAL_TIMEOUT_MILLIS);
                continue;
            }

            // Place received packet in sorted order
            int packetSequence = receivedPacket.getSequence();
            if (!sequencePacketMap.containsKey(packetSequence)) {
                int i;
                for (i = 0; i < packetSequences.size(); ++i) {
                    if (packetSequences.get(i) >= packetSequence) {
                        break;
                    }
                }
                packetSequences.add(i, packetSequence);

                for (i = lastConsecutivePacketSequence + 1; i < packetSequences.size(); ++i) {
                    if (packetSequences.get(i) == lastConsecutivePacketSequence + 1) {
                        ++lastConsecutivePacketSequence;
                    } else {
                        break;
                    }
                }
            }
            sequencePacketMap.put(packetSequence, receivedPacket);

            // Send acknowledgement for greatest packet sequence number in a consecutive order
            TCPPacket acknowledgementPacket = new TCPPacket(lastConsecutivePacketSequence, "");
            udpLayer.writePacket(acknowledgementPacket);

            receivedPacket = udpLayer.readPacket(Config.TCP_INITIAL_TIMEOUT_MILLIS);
        }

        // Acknowledge final empty packet
        TCPPacket finAckPacket = new TCPPacket(receivedPacket.getSequence(), "");
        udpLayer.writePacket(finAckPacket);

        // Collapse packets back into original string
        String data = packetSequences.stream()
            .map(packetSequence -> sequencePacketMap.get(packetSequence).getData())
            .collect(Collectors.joining());

        packetSequences.clear();
        sequencePacketMap.clear();

        return data;
    }

    @Override
    public void close() throws IOException {
        udpLayer.close();
    }

    // Chunkify message into packets
    private void createPacketsFrom(String message) {
        int i;
        for (i = Config.TCP_DATA_SIZE_BYTES; i < message.length(); i += Config.TCP_DATA_SIZE_BYTES) {
            int packetSeqNumber = currentSequence++;
            String messageChunk = message.substring(i - Config.TCP_DATA_SIZE_BYTES, i);
            createPacketFrom(packetSeqNumber, messageChunk);
        }
        if (i != message.length()) {
            i -= Config.TCP_DATA_SIZE_BYTES;

            int packetSeqNumber = currentSequence++;
            String messageChunk = message.substring(i);
            createPacketFrom(packetSeqNumber, messageChunk);
        }
    }

    private void createPacketFrom(int packetSeqNumber, String messageChunk) {
        TCPPacket packet = new TCPPacket(packetSeqNumber, messageChunk);
        packetSequences.add(packetSeqNumber);
        sequencePacketMap.put(packetSeqNumber, packet);
    }

    // Process timed-out packets
    private void handleTimeouts() {
        for (final int packetSeqNumber : Set.copyOf(packetSequenceTimeoutMap.keySet())) {
            if (packetSequenceTimeoutMap.get(packetSeqNumber) > Instant.now().toEpochMilli()) {
                continue;
            }

            // Packet has timed out, so prepare to resend
            currentPacketIndex = Math.min(currentPacketIndex, packetSequences.indexOf(packetSeqNumber));

            // Remove associated timings so that they don't affect round trip estimates
            packetSequenceSendTimeMap.remove(packetSeqNumber);
            packetSequenceTimeoutMap.remove(packetSeqNumber);

            // Re-enter slow start
            slowStartThreshold = Math.max(slowStartThreshold / 2, 1);
            congestionWindow = Math.min(Config.TCP_INITIAL_CONGESTION_WINDOW, slowStartThreshold);
        }
    }

    // Determine which packets are within the congestion window and send them
    private void sendPacketsInWindow() throws IOException {
        while (currentPacketIndex < packetSequences.size()
            && currentPacketIndex < (int) (congestionWindow + Config.TCP_CONGESTION_WINDOW_ROUNDING_TOLERANCE)
        ) {
            int packetSeqNumber = packetSequences.get(currentPacketIndex);
            TCPPacket packet = sequencePacketMap.get(packetSeqNumber);
            udpLayer.writePacket(packet);

            long epochMillisNow = Instant.now().toEpochMilli();
            packetSequenceSendTimeMap.put(packetSeqNumber, epochMillisNow);
            packetSequenceTimeoutMap.put(packetSeqNumber, epochMillisNow + (roundTripTimeMillis > 0
                ? roundTripTimeMillis * 2L
                : Config.TCP_INITIAL_TIMEOUT_MILLIS));

            ++currentPacketIndex;
        }
    }

    private void retransmitUntilAcknowledged(TCPPacket packet) throws IOException {
        for (int i = 0; i < Config.TCP_FINAL_PACKET_TRANSMIT_ATTEMPTS; ++i) {
            udpLayer.writePacket(packet);
            TCPPacket receivedPacket = udpLayer.readPacket(roundTripTimeMillis);

            if (receivedPacket != null && receivedPacket.getSequence() == packet.getSequence()) {
                break;
            }
        }
    }
}
