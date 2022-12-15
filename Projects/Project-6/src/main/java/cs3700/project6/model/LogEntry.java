package cs3700.project6.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data class representing an entry in a replica's log.
 */
@Data
@Jacksonized
@SuperBuilder
public class LogEntry {
    int term;
    String key;
    String value;
}
