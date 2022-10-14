package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
