package cs3700.project3.util;

import cs3700.project3.Config;
import lombok.NonNull;

import java.util.Arrays;

/**
 * Utility class containing convenience functions.
 */
public class Util {
    /**
     * Given the address of the next hop, determines what our source IP address should be.
     *
     * @param nextHop Address of the next hop along the route.
     * @return Our source IP address.
     */
    public static String getSrcAddressFrom(@NonNull String nextHop) {
        return nextHop.substring(0, nextHop.length() - 1) + Config.SOURCE_ADDRESS_LEAST_SIGNIFICANT_BYTE;
    }

    /**
     * Calculates the prefix bit-string for the given network address and netmask.
     *
     * @param network Network address to use.
     * @param netmask Netmask to use.
     * @return Calculated prefix bits.
     */
    public static int getPrefixBitsFrom(@NonNull String network, @NonNull String netmask) {
        return getBitsFrom(network) & getBitsFrom(netmask);
    }

    /**
     * Calculates the bit-string of the given quad notation string.
     *
     * @param quad Quad notation string to use.
     * @return Calculated bit-string.
     */
    public static int getBitsFrom(@NonNull String quad) {
        final Integer[] quadBytes = Arrays.stream(quad.split("\\."))
            .map(Integer::parseInt)
            .toArray(Integer[]::new);

        int quadBits = 0;

        // Coalesce quad bytes into single bit-string
        for (int i = 0; i < 4; ++i) {
            quadBits += quadBytes[i] << 8 * (3 - i);
        }

        return quadBits;
    }

    /**
     * Calculates the quad notation string from the given bit-string.
     *
     * @param bits Bit-string to use.
     * @return Calculated quad notation string.
     */
    public static String getQuadFrom(int bits) {
        final StringBuilder quadBuilder = new StringBuilder();
        quadBuilder.append(bits >> 24 & 255);

        for (int i = 2; i >= 0; --i) {
            quadBuilder.append('.');
            quadBuilder.append(bits >> 8 * i & 255);
        }

        return quadBuilder.toString();
    }
}
