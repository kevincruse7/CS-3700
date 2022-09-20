package com.cs3700.project2.socket;

import java.io.Closeable;

public interface CommandSocketHandler extends Closeable {
    String read();
    void write(String data);
}
