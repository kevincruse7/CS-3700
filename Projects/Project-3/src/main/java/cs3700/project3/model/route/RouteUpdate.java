package cs3700.project3.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Data class that represents the body of a route update message.
 */
@Data
public class RouteUpdate {
    @JsonProperty("network")
    String network;

    @JsonProperty("netmask")
    String netmask;

    @JsonProperty("localpref")
    Integer localPref;

    @JsonProperty("selfOrigin")
    Boolean selfOrigin;

    @JsonProperty("ASPath")
    List<Integer> asPath;

    @JsonProperty("origin")
    String origin;
}
