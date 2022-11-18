package cs3700.project5;

import cs3700.project5.controller.crawler.WebCrawler;
import cs3700.project5.controller.crawler.WebCrawlerFactory;

import java.io.IOException;

/**
 * Entry point for the application.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String host = Config.DEFAULT_HOST;
        int port = Config.DEFAULT_PORT;

        int argIndex;

        // Parse optional arguments
        for (argIndex = 0; argIndex < args.length - 2; argIndex += 2) {
            switch (args[argIndex]) {
                case "-s":
                    host = args[argIndex + 1];
                    break;
                case "-p":
                    port = Integer.parseInt(args[argIndex + 1]);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported option: " + args[argIndex]);
            }
        }

        if (argIndex == args.length) {
            throw new IllegalArgumentException("Missing username and password arguments");
        }

        String username = args[argIndex++];

        if (argIndex == args.length) {
            throw new IllegalArgumentException("Missing password argument");
        }

        String password = args[argIndex];

        // Run web crawler with the provided arguments
        try (WebCrawler webCrawler = WebCrawlerFactory.createWebCrawler(host, port, username, password)) {
            webCrawler.run();
        }
    }
}
