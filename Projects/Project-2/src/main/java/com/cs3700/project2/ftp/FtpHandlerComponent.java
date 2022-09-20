package com.cs3700.project2.ftp;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.net.URI;

@Component(modules = FtpHandlerComponent.FtpHandlerModule.class)
@Singleton
public interface FtpHandlerComponent {
    FtpHandler ftpHandler();

    @Module
    @NoArgsConstructor
    @RequiredArgsConstructor
    class FtpHandlerModule {
        @NonNull
        private URI uri;

        @Provides
        public FtpHandler provideFtpHandler() {
            return new BasicFtpHandler(uri);
        }
    }
}
