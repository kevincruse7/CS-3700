package com.cs3700.project1.model;

import lombok.Data;

import java.util.List;

@Data
public class Guess {
    String word;
    List<Integer> marks;
}
