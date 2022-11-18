package cs3700.project5.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class that represents the head of an HTTP message.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class HTTPHead {
    @Getter
    private final int status;

    @NonNull
    private final Map<String, List<String>> headers;

    /**
     * Construct an instance of {@code HTTPHead} from a string representation.
     *
     * @param head String representation of HTTP message head.
     * @return Constructed instance of {@code HTTPHead}.
     * @throws IOException An error occurred when parsing the string representation.
     */
    public static HTTPHead from(@NonNull String head) throws IOException {
        BufferedReader headReader = new BufferedReader(new StringReader(head));

        int status = Integer.parseInt(headReader.readLine().split(" ")[1]);
        Map<String, List<String>> headers = new HashMap<>();

        String headerLine = headReader.readLine();

        while (headerLine != null) {
            String[] splitHeader = headerLine.split(":");
            String headerName = splitHeader[0].toLowerCase();
            String headerValue = splitHeader[1].strip();

            headers.putIfAbsent(headerName, new ArrayList<>());
            headers.get(headerName).add(headerValue);

            headerLine = headReader.readLine();
        }

        headers.replaceAll((headerName, headerValue) -> Collections.unmodifiableList(headerValue));
        return new HTTPHead(status, headers);
    }

    /**
     * Fetch the value(s) of the given header.
     *
     * @param name Name of header to fetch values of.
     * @return List of value(s) of the given header.
     */
    public List<String> getHeader(@NonNull String name) {
        return headers.getOrDefault(name.toLowerCase(), Collections.emptyList());
    }
}
