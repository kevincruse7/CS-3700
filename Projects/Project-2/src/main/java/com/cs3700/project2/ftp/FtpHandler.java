package com.cs3700.project2.ftp;

import java.io.Closeable;

public interface FtpHandler extends Closeable {
    void send(String command);
    String receive();
}
