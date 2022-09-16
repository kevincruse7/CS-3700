package com.cs3700.project1.model;

import lombok.Data;

import java.util.List;

/** Simple data object for a Wordle guess and its corresponding character matches. */
@Data
public class Guess {
    String word;
    List<Integer> marks;
}
