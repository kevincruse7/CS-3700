package com.cs3700.project1.model.message;

import com.cs3700.project1.model.Guess;
import lombok.Data;

import java.util.List;

@Data
public class RetryOrByeMessage {
    String type;
    String id;
    String flag;
    List<Guess> guesses;
}
