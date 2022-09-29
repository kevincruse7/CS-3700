package com.cs3700.project2.ftp;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a connection to an FTP server, defining capabilities for sending and receiving
 * commands and responses on the command channel and opening new data channels.
 */
public interface FtpConnection extends Closeable {
    /**
     * Send the given command to the FTP server.
     *
     * @param command Command to send.
     * @throws IOException I/O error occurred when sending command to the FTP server.
     */
    void sendCommand(String command) throws IOException;

    /**
     * Receive a response from the FTP server.
     *
     * @return Response from command channel.
     * @throws IOException I/O error occurred when receiving response from the FTP server.
     */
    String receiveResponse() throws IOException;

    /**
     * Open a data channel with the FTP server.
     *
     * @return Opened data channel.
     * @throws IOException I/O error occurred when opening data channel with the FTP server.
     */
    FtpDataChannel openDataChannel() throws IOException;
}
