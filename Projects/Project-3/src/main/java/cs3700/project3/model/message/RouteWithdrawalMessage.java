package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3700.project3.model.route.RouteWithdrawal;
import lombok.Data;

import java.util.List;

/**
 * Data class that represents a route withdrawal message.
 */
@Data
public class RouteWithdrawalMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "withdraw";

    @JsonProperty("msg")
    List<RouteWithdrawal> routeWithdrawals;
}
