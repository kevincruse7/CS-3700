package com.cs3700.project2.socket;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

@Component(modules = DataSocketHandlerComponent.DataSocketHandlerModule.class)
@Singleton
public interface DataSocketHandlerComponent {
    DataSocketHandler dataSocketHandler();

    @Module
    @NoArgsConstructor
    @RequiredArgsConstructor
    class DataSocketHandlerModule {
        @NonNull
        private String serverResponse;

        @Provides
        public DataSocketHandler provideDataSocketHandler() {
            return new BasicDataSocketHandler(serverResponse);
        }
    }
}
