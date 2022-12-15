package cs3700.project6;

import lombok.NonNull;

import java.util.Random;

/**
 * Utility functions for the application.
 */
public class Util {
    /**
     * Generate a random message ID.
     *
     * @param random Random object to use when generating the ID.
     * @return Random message ID.
     */
    public static String generateMessageIDFrom(@NonNull Random random) {
        return Integer.toString(random.nextInt(Integer.MAX_VALUE), Character.MAX_RADIX);
    }
}
