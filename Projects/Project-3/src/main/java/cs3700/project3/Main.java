package cs3700.project3;

import cs3700.project3.controller.router.RouterFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entry point for the application.
 */
public class Main {
    public static void main(String[] args) {
        // Parse the given arguments
        int as = Integer.parseInt(args[0]);
        List<String> peers = Arrays.stream(args).skip(1).collect(Collectors.toList());

        RouterFactory.createRouter(as, peers).run();
    }
}
