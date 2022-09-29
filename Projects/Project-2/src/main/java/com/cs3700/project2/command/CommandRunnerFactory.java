package com.cs3700.project2.command;

import com.cs3700.project2.ftp.FtpConnection;
import com.cs3700.project2.ftp.FtpConnectionFactory;
import lombok.NonNull;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Factory class for command runner implementations.
 */
public class CommandRunnerFactory {
    /**
     * Create a command runner.
     *
     * @param command Command to be run.
     * @param params  Parameters for running the command.
     * @return New command runner with the specified parameters.
     * @throws IOException I/O error occurred when establishing network connections required for command runner.
     */
    public static CommandRunner createCommandRunner(@NonNull String command, @NonNull List<URI> params)
        throws IOException {
        // Determine if the first parameter is the FTP URI or the local path
        boolean firstParamIsFtpUri = params.size() == 1 || params.get(0).getHost() != null;

        FtpConnection ftpConnection = FtpConnectionFactory.createFtpConnection(
            firstParamIsFtpUri ? params.get(0) : params.get(1)
        );

        switch (command) {
            case "ls":
                if (params.size() != 1) {
                    throw new IllegalArgumentException("'ls' command should have a single argument");
                }

                return new LsCommandRunner(ftpConnection, params.get(0));
            case "mkdir":
                if (params.size() != 1) {
                    throw new IllegalArgumentException("'mkdir' command should have a single argument");
                }

                return new MkdirCommandRunner(ftpConnection, params.get(0));
            case "rm":
                if (params.size() != 1) {
                    throw new IllegalArgumentException("'rm' command should have a single argument");
                }

                return new RmCommandRunner(ftpConnection, params.get(0));
            case "rmdir":
                if (params.size() != 1) {
                    throw new IllegalArgumentException("'rmdir' command should have a single argument");
                }

                return new RmdirCommandRunner(ftpConnection, params.get(0));
            case "cp":
                if (params.size() != 2) {
                    throw new IllegalArgumentException("'cp' command should have two arguments");
                }

                return new CpCommandRunner(ftpConnection, params.get(0), params.get(1));
            case "mv":
                if (params.size() != 2) {
                    throw new IllegalArgumentException("'mv' command should have two arguments");
                }

                return new MvCommandRunner(ftpConnection, params.get(0), params.get(1));
            default:
                throw new IllegalArgumentException("Invalid command given: " + command);
        }
    }
}
