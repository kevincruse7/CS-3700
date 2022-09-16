package com.cs3700.project1.message;

import com.cs3700.project1.model.GuessProgress;

import java.io.Closeable;
import java.io.IOException;

/** Handler for sending word guesses and retrieving match responses from Wordle server. */
public interface MessageHandler extends Closeable {
    /** Send word guess to server and retrieve match response. */
    GuessProgress guess(String word) throws IOException;
}
