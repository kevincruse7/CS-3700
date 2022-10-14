package cs3700.project3.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RouteWithdrawal {
    @JsonProperty("network")
    String network;

    @JsonProperty("netmask")
    String netmask;
}
