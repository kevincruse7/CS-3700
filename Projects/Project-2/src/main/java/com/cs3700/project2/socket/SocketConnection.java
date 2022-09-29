package com.cs3700.project2.socket;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a network socket connection, defining capabilities to read from and write to the socket.
 *
 * @param <T> Type of data that is read from and written to the socket.
 */
public interface SocketConnection<T> extends Closeable {
    /**
     * Read the next segment of data from the socket.
     *
     * @return Segment of data retrieved from the socket.
     * @throws IOException I/O error occurred when reading from socket.
     */
    T read() throws IOException;

    /**
     * Write the given data to the socket.
     *
     * @param data Data to write to the socket.
     * @throws IOException I/O error occurred when writing to the socket.
     */
    void write(T data) throws IOException;
}
