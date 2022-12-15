package cs3700.project6.model.message;

import cs3700.project6.model.LogEntry;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Data class representing an {@code appendEntries} message.
 */
@Data
@Jacksonized
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppendEntriesMessage extends Message {
    @Builder.Default
    String type = "appendEntries";

    int term;
    int prevLogIndex;
    int prevLogTerm;
    List<LogEntry> entries;
    int leaderCommit;
}
