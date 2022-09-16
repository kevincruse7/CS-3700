package com.cs3700.project1.model.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Simple data object for a 'guess' message. */
@Data
@NoArgsConstructor
public class GuessMessage {
    String type = "guess";
    String id;
    String word;

    public GuessMessage(String id, String word) {
        this.id = id;
        this.word = word;
    }
}
