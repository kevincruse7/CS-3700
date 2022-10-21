package cs3700.project3.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data class that represents the body of a route withdrawal message.
 */
@Data
public class RouteWithdrawal {
    @JsonProperty("network")
    String network;

    @JsonProperty("netmask")
    String netmask;
}
