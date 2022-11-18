package cs3700.project5.controller.socket;

import lombok.NonNull;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Factory class for creating string network sockets.
 */
public class SocketFactory {
    /**
     * Create a TCP string network socket with TLS connected to the given host and port.
     *
     * @param host Host name or address to connect to.
     * @param port Port of host to connect to.
     * @return Created string network socket.
     * @throws IOException An error occurred when creating the string network socket.
     */
    public static Socket createTLSSocket(@NonNull String host, int port) throws IOException {
        java.net.Socket tlsSocket = SSLSocketFactory.getDefault().createSocket(host, port);
        BufferedReader input = new BufferedReader(new InputStreamReader(tlsSocket.getInputStream()));
        PrintWriter output = new PrintWriter(tlsSocket.getOutputStream(), true);

        return new BasicSocket(input, output);
    }
}
