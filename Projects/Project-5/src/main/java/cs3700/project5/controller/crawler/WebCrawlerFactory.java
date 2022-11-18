package cs3700.project5.controller.crawler;

import cs3700.project5.controller.http.HTTPConnection;
import cs3700.project5.controller.http.HTTPConnectionFactory;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Factor class for creating web crawler applications.
 */
public class WebCrawlerFactory {
    /**
     * Create a web crawler application that traverses the website at the given
     * host and port and authenticates with the provided login credentials.
     *
     * @param host Host name or address of website.
     * @param port Host port of HTTPS service.
     * @param username Username to log in with.
     * @param password Password to log in with.
     * @return Created web crawler application.
     * @throws IOException An error occurred when creating the web crawler application.
     */
    public static WebCrawler createWebCrawler(
        @NonNull String host,
        int port,
        @NonNull String username,
        @NonNull String password
    ) throws IOException {
        HTTPConnection HTTPConnection = HTTPConnectionFactory.createHTTPSConnection(host, port);
        ArrayList<String> flags = new ArrayList<>();
        HashSet<String> visitedPages = new HashSet<>();
        ArrayDeque<String> pageDeque = new ArrayDeque<>();

        return new BasicWebCrawler(HTTPConnection, username, password, flags, visitedPages, pageDeque);
    }
}
