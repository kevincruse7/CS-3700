package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3700.project3.model.route.RouteUpdate;
import lombok.Data;

/**
 * Data class that represents a route update message.
 */
@Data
public class RouteUpdateMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "update";

    @JsonProperty("msg")
    RouteUpdate routeUpdate;
}
