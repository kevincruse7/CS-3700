package com.cs3700.project2.command;

import com.cs3700.project2.ftp.FtpConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Runner implementation of the {@code mv} command.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MvCommandRunner implements CommandRunner {
    @NonNull
    private final FtpConnection ftpConnection;

    @NonNull
    private final URI from;

    @NonNull
    private final URI to;

    @Override
    @SneakyThrows
    public void run() {
        new CpCommandRunner(ftpConnection, from, to).run();

        // Determine if the 'from' parameter is the FTP URI or the local path
        boolean fromParamIsFtpUri = from.getHost() != null;

        if (fromParamIsFtpUri) {
            new RmCommandRunner(ftpConnection, from).run();
        } else {
            boolean fileWasDeleted = new File(from.getPath()).delete();

            if (!fileWasDeleted) {
                throw new IOException("File not found: " + from);
            }
        }
    }

    @Override
    public void close() throws IOException {
        ftpConnection.close();
    }
}
