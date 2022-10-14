package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "data";

    @JsonProperty("msg")
    Object msg;
}
