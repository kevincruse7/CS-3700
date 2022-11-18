package cs3700.project5.controller.socket;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic implementation of a string network socket.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicSocket implements Socket {
    @NonNull
    private final BufferedReader input;

    @NonNull
    private final PrintWriter output;

    @Override
    public String readNChars(int n) throws IOException {
        char[] chars = new char[n];

        //noinspection ResultOfMethodCallIgnored
        input.read(chars);

        return new String(chars);
    }

    @Override
    public String readLine() throws IOException {
        return input.readLine();
    }

    @Override
    public void write(@NonNull String data) {
        output.println(data);
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
    }
}
