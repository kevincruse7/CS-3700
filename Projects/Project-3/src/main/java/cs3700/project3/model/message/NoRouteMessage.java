package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3700.project3.util.EmptyObject;
import lombok.Data;

/**
 * Data class that represents a no route message.
 */
@Data
public class NoRouteMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "no route";

    @JsonProperty("msg")
    Object msg = new EmptyObject();
}
