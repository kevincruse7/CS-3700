package com.cs3700.project2.socket;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

@Component(modules = CommandSocketHandlerComponent.CommandSocketHandlerModule.class)
@Singleton
public interface CommandSocketHandlerComponent {
    CommandSocketHandler commandSocketHandler();

    @Module
    @NoArgsConstructor
    @RequiredArgsConstructor
    class CommandSocketHandlerModule {
        @NonNull
        private String hostname;

        @NonNull
        private Integer port;

        @Provides
        public CommandSocketHandler provideCommandSocketHandler() {
            return new BasicCommandCommandSocketHandler(hostname, port);
        }
    }
}
