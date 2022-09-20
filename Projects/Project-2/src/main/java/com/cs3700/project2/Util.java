package com.cs3700.project2;

import lombok.NonNull;

import java.io.IOException;

public class Util {
    public static void assertStatusCodeOk(@NonNull String serverResponse) throws IOException {
        if (serverResponse.length() < 3) {
            throw new IOException("Unexpected response from server: " + serverResponse);
        }

        int statusCode = Integer.parseInt(serverResponse.substring(0, 3));

        if (statusCode >= 400) {
            throw new IOException("Unexpected response from server: " + serverResponse);
        }
    }
}
