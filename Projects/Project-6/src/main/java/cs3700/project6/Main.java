package cs3700.project6;

import cs3700.project6.controller.Controller;
import cs3700.project6.controller.ControllerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entry point for the application.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        String id = args[1];
        Set<String> remotes = Arrays.stream(args).skip(2).collect(Collectors.toSet());

        try (Controller controller = ControllerFactory.createController(port, id, remotes)) {
            controller.run();
        }
    }
}
