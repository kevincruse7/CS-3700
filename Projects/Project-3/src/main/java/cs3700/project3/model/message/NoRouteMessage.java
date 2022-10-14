package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
