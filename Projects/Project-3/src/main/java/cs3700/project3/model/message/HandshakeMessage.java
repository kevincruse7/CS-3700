package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3700.project3.util.EmptyObject;
import lombok.Data;

/**
 * Data class that represents a handshake message.
 */
@Data
public class HandshakeMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "handshake";

    @JsonProperty("msg")
    Object msg = new EmptyObject();
}
