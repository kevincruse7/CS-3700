package cs3700.project6.model.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data class representing a {@code requestVote} message.
 */
@Data
@Jacksonized
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RequestVoteMessage extends Message {
    @Builder.Default
    String type = "requestVote";

    int term;
    int lastLogIndex;
    int lastLogTerm;
}
