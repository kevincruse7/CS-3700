package com.cs3700.project2;

/**
 * Configuration options for the application.
 */
public interface Config {
    int FTP_DEFAULT_COMMAND_PORT = 21;
    int FTP_ERROR_STATUS_LOWER_BOUND = 400;
    int FTP_TRANSFER_BLOCK_SIZE_BYTES = 1048576;
    int IP_LENGTH_BYTES = 4;
}
