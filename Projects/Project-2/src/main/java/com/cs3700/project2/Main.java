package com.cs3700.project2;

import com.cs3700.project2.command.CommandHandlerComponent.CommandHandlerModule;
import com.cs3700.project2.command.DaggerCommandHandlerComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Command not provided");
        }

        if (args.length == 1) {
            throw new IllegalArgumentException("Command parameter(s) not provided");
        }

        DaggerCommandHandlerComponent.builder()
            .commandHandlerModule(new CommandHandlerModule(args[0], Arrays.stream(args)
                .skip(1)
                .collect(Collectors.toList())))
            .build()
            .commandHandler()
            .run();
    }
}
