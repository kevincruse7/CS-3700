package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3700.project3.util.EmptyObject;
import lombok.Data;

/**
 * Data class that represents a dump message.
 */
@Data
public class DumpMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "dump";

    @JsonProperty("msg")
    Object msg = new EmptyObject();
}
