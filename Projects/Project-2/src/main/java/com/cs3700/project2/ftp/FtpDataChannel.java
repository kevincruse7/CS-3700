package com.cs3700.project2.ftp;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a data channel connection with an FTP server.
 */
public interface FtpDataChannel extends Closeable {
    /**
     * Read the next block of data from the FTP data channel.
     *
     * @return Block of data read from the FTP data channel.
     * @throws IOException I/O error occurred when reading block of data from the FTP data channel.
     */
    byte[] read() throws IOException;

    /**
     * Write the given block of data to the FTP data channel.
     *
     * @param data Block of data to write to the FTP data channel.
     * @throws IOException I/O error occurred when writing block of data to the FTP data channel.
     */
    void write(byte[] data) throws IOException;
}
