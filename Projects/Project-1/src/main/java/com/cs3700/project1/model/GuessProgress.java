package com.cs3700.project1.model;

import com.cs3700.project1.model.message.RetryOrByeMessage;
import lombok.Value;

import java.util.List;

@Value
public class GuessProgress {
    String flag;
    List<Guess> guesses;

    public static GuessProgress from(RetryOrByeMessage retryOrByeMessage) {
        return new GuessProgress(retryOrByeMessage.getFlag(), retryOrByeMessage.getGuesses());
    }
}
