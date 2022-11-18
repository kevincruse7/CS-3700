package cs3700.project5.controller.http;

import cs3700.project5.controller.socket.Socket;
import cs3700.project5.controller.socket.SocketFactory;
import cs3700.project5.model.HTTPCookie;
import lombok.NonNull;

import java.io.IOException;

/**
 * Factory class for creating HTTP service connections.
 */
public class HTTPConnectionFactory {
    /**
     * Create an HTTPS service connection with the given host and port.
     *
     * @param host Host name or address to connect to.
     * @param port Host port to connect to.
     * @return Created HTTPS service connection.
     * @throws IOException An error occurred when creating the HTTPS service connection.
     */
    public static HTTPConnection createHTTPSConnection(@NonNull String host, int port) throws IOException {
        Socket socket = SocketFactory.createTLSSocket(host, port);
        HTTPCookie cookie = new HTTPCookie();

        return new BasicHTTPConnection(socket, host, port, cookie);
    }
}
