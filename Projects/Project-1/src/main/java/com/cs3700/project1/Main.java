package com.cs3700.project1;

import com.cs3700.project1.guesser.BasicWordGuesser;
import com.cs3700.project1.guesser.WordGuesser;
import com.cs3700.project1.message.BasicMessageHandler;
import com.cs3700.project1.message.MessageHandler;
import com.cs3700.project1.model.GuessProgress;
import lombok.NonNull;

import java.io.IOException;
import java.util.Collections;

/** Application entry point and main program loop. */
public class Main {
    private final WordGuesser wordGuesser;
    private final MessageHandler messageHandler;

    /** Parse program arguments and start main program loop. */
    public static void main(String[] args) throws IOException {
        int current_arg_index = 0;

        int port = -1;
        boolean encrypted = false;

        // User specified a port
        if (args[current_arg_index].equals("-p")) {
            port = Integer.parseInt(args[current_arg_index + 1]);
            current_arg_index += 2;
        }

        // User specified an encrypted connection
        if (args[current_arg_index].equals("-s")) {
            encrypted = true;

            // Fallback to default encrypted port if port not specified
            if (port == -1) {
                port = Config.DEFAULT_ENCRYPTED_PORT;
            }

            current_arg_index++;
        }

        // Fallback to default unencrypted port if neither port nor encrypted connection specified
        if (port == -1) {
            port = Config.DEFAULT_UNENCRYPTED_PORT;
        }

        String hostname = args[current_arg_index++];
        String username = args[current_arg_index];

        new Main(port, encrypted, hostname, username).run();
    }

    public Main(int port, boolean encrypted, @NonNull String hostname, @NonNull String username) throws IOException {
        this.wordGuesser = new BasicWordGuesser();
        this.messageHandler = new BasicMessageHandler(port, encrypted, hostname, username);
    }

    /** Start main program loop. */
    public void run() throws IOException {
        // Send initial guess
        String guess = wordGuesser.findGuess(Collections.emptyList());
        GuessProgress guessProgress = messageHandler.guess(guess);

        // Send subsequent guesses until flag is retrieved
        while (guessProgress.getFlag() == null) {
            guess = wordGuesser.findGuess(guessProgress.getGuesses());
            guessProgress = messageHandler.guess(guess);
        }

        messageHandler.close();
        System.out.println(guessProgress.getFlag());
    }
}
