package com.cs3700.project2.socket;

import java.io.Closeable;

public interface DataSocketHandler extends Closeable {
    byte[] read();
    void write(byte[] data);
}
