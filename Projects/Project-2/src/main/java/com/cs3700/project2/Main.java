package com.cs3700.project2;

import com.cs3700.project2.command.CommandRunner;
import com.cs3700.project2.command.CommandRunnerFactory;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entry point for the application.
 */
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No command provided");
        }

        String command = args[0];

        if (args.length == 1) {
            throw new IllegalArgumentException("No command arguments provided");
        }

        List<URI> params = Arrays.stream(args)
            .skip(1)
            .map(param -> {
                try {
                    return new URI(param);
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e);
                }
            })
            .collect(Collectors.toList());

        try (CommandRunner commandRunner = CommandRunnerFactory.createCommandRunner(command, params)) {
            commandRunner.run();
        }
    }
}
