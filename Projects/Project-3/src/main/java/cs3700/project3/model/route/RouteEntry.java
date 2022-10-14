package cs3700.project3.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RouteEntry {
    @JsonProperty("network")
    String network;

    @JsonProperty("netmask")
    String netmask;

    @JsonProperty("peer")
    String peer;

    @JsonProperty("localpref")
    Integer localPref;

    @JsonProperty("ASPath")
    List<Integer> asPath;

    @JsonProperty("selfOrigin")
    Boolean selfOrigin;

    @JsonProperty("origin")
    String origin;
}
