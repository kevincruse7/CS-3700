package com.cs3700.project1.guesser;

import com.cs3700.project1.model.Guess;
import lombok.NonNull;

import java.util.List;

public interface WordGuesser {
    String findGuess(@NonNull List<Guess> previousGuesses);
}
