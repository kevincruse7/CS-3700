package cs3700.project5.controller.http;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a connection with an HTTP service, supporting {@code GET} and {@code POST} requests.
 */
public interface HTTPConnection extends Closeable {
    /**
     * Fetch the resource located on the connected host at the given path.
     *
     * @param path Path of resource to fetch.
     * @return String representation of fetched resource.
     * @throws IOException An error occurred when fetching the resource.
     */
    String getResource(String path) throws IOException;

    /**
     * Posts the given body to the resource located on the connected host at the given path.
     *
     * @param path Path of resource to post to.
     * @param body Data to post to resource.
     * @return String representation of response body.
     * @throws IOException An error occurred when posting to the resource.
     */
    String postToResource(String path, String body) throws IOException;
}
