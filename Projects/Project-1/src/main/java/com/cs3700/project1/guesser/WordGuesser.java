package com.cs3700.project1.guesser;

import com.cs3700.project1.model.Guess;
import lombok.NonNull;

import java.util.List;

/** Wordle word guess generator. */
public interface WordGuesser {
    /** Generate a new guess given the results of previous guesses. */
    String findGuess(@NonNull List<Guess> previousGuesses);
}
