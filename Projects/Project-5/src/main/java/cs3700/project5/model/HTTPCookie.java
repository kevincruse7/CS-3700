package cs3700.project5.model;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data class that represents an HTTP cookie.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class HTTPCookie {
    @NonNull
    private final Map<String, String> cookieValues;

    public HTTPCookie() {
        this(new HashMap<>());
    }

    /**
     * Set the given named variable to the given value in this cookie.
     *
     * @param name Name of variable to set.
     * @param value Value to set variable to.
     */
    public void set(@NonNull String name, @NonNull String value) {
        cookieValues.put(name, value);
    }

    @Override
    public String toString() {
        return cookieValues.entrySet()
            .stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("; "));
    }
}
