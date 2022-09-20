package com.cs3700.project2.socket;

import com.cs3700.project2.Config;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BasicDataSocketHandler implements DataSocketHandler {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    @SneakyThrows
    public BasicDataSocketHandler(@NonNull String serverResponse) {
        String[] ipComponents = serverResponse
            .substring(serverResponse.indexOf('(') + 1, serverResponse.indexOf(')'))
            .split(",");

        String ip = Arrays.stream(ipComponents)
            .limit(Config.IP_SIZE_BYTES)
            .collect(Collectors.joining("."));

        int port = (Integer.parseInt(ipComponents[Config.IP_SIZE_BYTES]) << 8)
            + Integer.parseInt(ipComponents[Config.IP_SIZE_BYTES + 1]);

        this.socket = new Socket(ip, port);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    @Override
    @SneakyThrows
    public byte[] read() {
        return in.readNBytes(Config.DATA_BLOCK_SIZE_BYTES);
    }

    @Override
    @SneakyThrows
    public void write(byte @NonNull [] data) {
        out.write(data);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
