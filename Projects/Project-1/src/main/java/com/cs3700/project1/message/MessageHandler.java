package com.cs3700.project1.message;

import com.cs3700.project1.model.GuessProgress;

import java.io.Closeable;
import java.io.IOException;

public interface MessageHandler extends Closeable {
    GuessProgress guess(String word) throws IOException;
}
