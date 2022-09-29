package com.cs3700.project2.ftp;

import com.cs3700.project2.Config;
import com.cs3700.project2.socket.SocketConnection;
import com.cs3700.project2.socket.SocketConnectionFactory;
import lombok.NonNull;

import java.io.IOException;
import java.net.URI;

/**
 * Factory class for FTP server connection implementations.
 */
public class FtpConnectionFactory {
    /**
     * Create a new FTP server connection.
     *
     * @param uri URI specifying server hostname or address, port number, and login credentials.
     * @return New FTP server connection with the specified parameters.
     * @throws IOException I/O error occurred when establishing FTP server connection.
     */
    public static FtpConnection createFtpConnection(@NonNull URI uri) throws IOException {
        if (!"ftp".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Unsupported protocol: " + uri.getScheme());
        }

        SocketConnection<String> commandSocketConnection = SocketConnectionFactory.createStringSocketConnection(
            uri.getHost(),
            uri.getPort() == -1 ? Config.FTP_DEFAULT_COMMAND_PORT : uri.getPort()
        );

        String username, password;

        if (uri.getUserInfo() != null) {
            String[] splitUserInfo = uri.getUserInfo().split(":");
            username = splitUserInfo[0];

            if (splitUserInfo.length > 1) {
                password = splitUserInfo[1];
            } else {
                password = "";
            }
        } else {
            username = "anonymous";
            password = "";
        }

        return new BasicFtpConnection(commandSocketConnection, username, password);
    }
}
