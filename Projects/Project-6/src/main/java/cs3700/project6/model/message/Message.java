package cs3700.project6.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Data class representing a basic message.
 */
@Data
@SuperBuilder
public abstract class Message {
    @JsonProperty("src")
    String source;

    @JsonProperty("dst")
    String destination;

    String leader;

    @JsonProperty("MID")
    String messageID;
}
