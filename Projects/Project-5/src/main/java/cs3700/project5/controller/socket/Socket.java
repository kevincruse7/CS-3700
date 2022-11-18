package cs3700.project5.controller.socket;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a network socket that communicates in strings.
 */
public interface Socket extends Closeable {
    /**
     * Read up to {@code n} characters from the socket.
     *
     * @param n Number of characters to read from the socket.
     * @return String of characters read from the socket.
     * @throws IOException An error occurred when trying to read from the socket.
     */
    String readNChars(int n) throws IOException;

    /**
     * Read a line of text from the socket.
     *
     * @return Line of text read from the socket.
     * @throws IOException An error occurred when trying to read from the socket.
     */
    String readLine() throws IOException;

    /**
     * Write the given string to the socket.
     *
     * @param data String to write to the socket.
     */
    void write(String data);
}
