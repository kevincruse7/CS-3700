package com.cs3700.project2.command;

import com.cs3700.project2.command.CommandHandlerComponent.CommandHandlerModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
public class MvCommandHandler implements CommandHandler {
    @NonNull
    private final URI from;

    @NonNull
    private final URI to;

    @Override
    @SneakyThrows
    public void run() {
        DaggerCommandHandlerComponent.builder()
            .commandHandlerModule(new CommandHandlerModule("cp", Arrays.asList(
                from.toString(),
                to.toString()
            )))
            .build()
            .commandHandler()
            .run();

        if (from.getHost() != null) {
            DaggerCommandHandlerComponent.builder()
                .commandHandlerModule(new CommandHandlerModule("rm", Collections.singletonList(
                    from.toString()
                )))
                .build()
                .commandHandler()
                .run();
        } else if (!new File(from.getPath()).delete()) {
            throw new IOException("File not found: " + from);
        }
    }
}
