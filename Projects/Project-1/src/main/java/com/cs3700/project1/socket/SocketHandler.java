package com.cs3700.project1.socket;

import java.io.Closeable;
import java.io.IOException;

public interface SocketHandler extends Closeable {
    String read() throws IOException;
    void write(String message) throws IOException;
}
