package cs3700.project5.controller.http;

import cs3700.project5.Config;
import cs3700.project5.controller.socket.Socket;
import cs3700.project5.model.HTTPCookie;
import cs3700.project5.model.HTTPHead;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Basic implementation of an HTTP service connection.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicHTTPConnection implements HTTPConnection {
    @NonNull
    private final Socket socket;

    @NonNull
    private final String host;

    private final int port;

    @NonNull
    private final HTTPCookie cookie;

    @Override
    public String getResource(@NonNull String path) throws IOException {
        String request = String.format(
            "GET %s HTTP/1.1\r\n"
                + "Host: %s:%d\r\n"
                + (cookie.toString().equals("") ? "%s" : "Cookie: %s\r\n")
                + "Connection: Keep-Alive\r\n"
                + "Accept: text/html\r\n"
                + "\r\n",
            path, host, port, cookie
        );

        HTTPHead responseHead = sendRequest(request);
        int status = responseHead.getStatus();

        if (status == Config.HTTP_FORBIDDEN || status == Config.HTTP_NOT_FOUND) {
            return "";
        }

        if (status != Config.HTTP_OK) {
            throw new IOException("Unexpected response code: " + status);
        }

        StringBuilder responseBuilder = new StringBuilder();
        List<String> transferEncodingHeader = responseHead.getHeader("Transfer-Encoding");

        // Determine if response is chunked or not
        if (transferEncodingHeader.size() > 0 && transferEncodingHeader.get(0).equals("chunked")) {
            String responseLine = socket.readLine();
            int chunkSize = Integer.parseInt(responseLine.split(";")[0], 16);

            while (chunkSize > 0) {
                responseBuilder.append(socket.readNChars(chunkSize));

                socket.readLine();
                responseLine = socket.readLine();

                chunkSize = Integer.parseInt(responseLine.split(";")[0], 16);
            }
        } else {
            int contentLength = Integer.parseInt(responseHead.getHeader("Content-Length").get(0));
            responseBuilder.append(socket.readNChars(contentLength));
        }

        return responseBuilder.toString();
    }

    @Override
    public String postToResource(@NonNull String path, @NonNull String body) throws IOException {
        String request = String.format(
            "POST %s HTTP/1.1\r\n"
                + "Host: %s:%d\r\n"
                + (cookie.toString().equals("") ? "%s" : "Cookie: %s\r\n")
                + "Connection: Keep-Alive\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: %d\r\n"
                + "\r\n"
                + "%s",
            path, host, port, cookie, body.length(), body
        );

        HTTPHead responseHead = sendRequest(request);
        int status = responseHead.getStatus();

        if (status != Config.HTTP_FOUND) {
            throw new IOException("Unexpected response code: " + status);
        }

        // Fetch and return the resource located at the provided redirect path
        return responseHead.getHeader("Location").get(0);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private HTTPHead sendRequest(@NonNull String request) throws IOException {
        HTTPHead responseHead;
        int status;

        do {
            socket.write(request);

            String responseHeadLine = socket.readLine();
            StringBuilder responseHeadBuilder = new StringBuilder();

            while (!responseHeadLine.isBlank()) {
                responseHeadBuilder.append(responseHeadLine).append('\n');
                responseHeadLine = socket.readLine();
            }

            responseHead = HTTPHead.from(responseHeadBuilder.toString());

            // Update cookie if new values were provided
            for (String cookieValue : responseHead.getHeader("Set-Cookie")) {
                String[] splitCookieValue = cookieValue.split(";")[0].split("=");
                cookie.set(splitCookieValue[0], splitCookieValue[1]);
            }

            status = responseHead.getStatus();

            // Discard response body if service unavailable response received
            if (status == Config.HTTP_SERVICE_UNAVAILABLE) {
                int contentLength = Integer.parseInt(responseHead.getHeader("Content-Length").get(0));
                socket.readNChars(contentLength);
            }
        } while (status == Config.HTTP_SERVICE_UNAVAILABLE);

        return responseHead;
    }
}
