package com.cs3700.project2.command;

import java.io.Closeable;

/**
 * Represents a controller that runs a specified application command.
 */
public interface CommandRunner extends Runnable, Closeable {
}
