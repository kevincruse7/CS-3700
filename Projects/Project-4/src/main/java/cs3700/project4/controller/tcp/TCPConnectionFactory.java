package cs3700.project4.controller.tcp;

import cs3700.project4.controller.udp.UDPLayer;
import cs3700.project4.controller.udp.UDPLayerFactory;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Factory class for stripped-down TCP Reno connections.
 */
public class TCPConnectionFactory {
    public static TCPConnection createTCPConnection() throws IOException {
        return createTCPConnection(UDPLayerFactory.createUDPLayer());
    }

    public static TCPConnection createTCPConnection(@NonNull String hostname, int port) throws IOException {
        return createTCPConnection(UDPLayerFactory.createUDPLayer(hostname, port));
    }

    private static TCPConnection createTCPConnection(UDPLayer udpLayer) {
        return new BasicTCPConnection(
            udpLayer,
            new ArrayList<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>()
        );
    }
}
