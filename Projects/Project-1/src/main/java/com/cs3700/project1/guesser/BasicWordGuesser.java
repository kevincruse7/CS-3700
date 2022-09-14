package com.cs3700.project1.guesser;

import com.cs3700.project1.Config;
import com.cs3700.project1.model.Guess;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class BasicWordGuesser implements WordGuesser {
    private final Random random;
    private final List<String> wordList;

    public BasicWordGuesser() throws IOException {
        this.random = new Random();
        this.wordList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/project1-words.txt"))
        ))) {
            String word = bufferedReader.readLine();

            while (word != null) {
                wordList.add(word);
                word = bufferedReader.readLine();
            }
        }
    }

    @Override
    public String findGuess(@NonNull List<Guess> previousGuesses) {
        char[] exactMatches = new char[Config.WORD_LENGTH];
        Set<Character> inexactMatches = new HashSet<>();

        for (Guess guess : previousGuesses) {
            for (int index = 0; index < Config.WORD_LENGTH; index++) {
                switch (guess.getMarks().get(index)) {
                    case 0:
                        break;
                    case 1:
                        inexactMatches.add(guess.getWord().charAt(index));
                        break;
                    case 2:
                        exactMatches[index] = guess.getWord().charAt(index);
                        break;
                }
            }
        }

        List<String> reducedWordList = wordList.stream().filter(word -> {
            for (int index = 0; index < Config.WORD_LENGTH; index++) {
                if (exactMatches[index] > 0 && word.charAt(index) != exactMatches[index]) {
                    return false;
                }
            }

            for (char inexactMatch : inexactMatches) {
                if (word.indexOf(inexactMatch) == -1) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());

        return reducedWordList.get(random.nextInt(reducedWordList.size()));
    }
}
