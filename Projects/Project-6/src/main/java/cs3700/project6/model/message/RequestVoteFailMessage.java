package cs3700.project6.model.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data class representing a {@code requestVoteFail} message.
 */
@Data
@Jacksonized
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RequestVoteFailMessage extends Message {
    @Builder.Default
    String type = "requestVoteFail";

    int term;
}
