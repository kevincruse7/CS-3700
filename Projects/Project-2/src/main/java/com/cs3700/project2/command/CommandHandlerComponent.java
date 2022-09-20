package com.cs3700.project2.command;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Singleton;
import java.net.URI;
import java.util.List;

@Component(modules = CommandHandlerComponent.CommandHandlerModule.class)
@Singleton
public interface CommandHandlerComponent {
    CommandHandler commandHandler();

    @Module
    @NoArgsConstructor
    @RequiredArgsConstructor
    class CommandHandlerModule {
        @NonNull
        private String command;

        @NonNull
        private List<String> params;

        @Provides
        @Singleton
        @SneakyThrows
        public CommandHandler provideCommandHandler() {
            switch (command) {
                case "ls":
                    if (params.size() != 1) {
                        throw new IllegalArgumentException("'ls' command should have a single argument");
                    }

                    return new LsCommandHandler(new URI(params.get(0)));
                case "mkdir":
                    if (params.size() != 1) {
                        throw new IllegalArgumentException("'mkdir' command should have a single argument");
                    }

                    return new MkdirCommandHandler(new URI(params.get(0)));
                case "rm":
                    if (params.size() != 1) {
                        throw new IllegalArgumentException("'rm' command should have a single argument");
                    }

                    return new RmCommandHandler(new URI(params.get(0)));
                case "rmdir":
                    if (params.size() != 1) {
                        throw new IllegalArgumentException("'rmdir' command should have a single argument");
                    }

                    return new RmdirCommandHandler(new URI(params.get(0)));
                case "cp":
                    if (params.size() != 2) {
                        throw new IllegalArgumentException("'cp' command should have two arguments");
                    }

                    return new CpCommandHandler(new URI(params.get(0)), new URI(params.get(1)));
                case "mv":
                    if (params.size() != 2) {
                        throw new IllegalArgumentException("'mv' command should have two arguments");
                    }

                    return new MvCommandHandler(new URI(params.get(0)), new URI(params.get(1)));
                default:
                    throw new IllegalArgumentException("Invalid command given: " + command);
            }
        }
    }
}
