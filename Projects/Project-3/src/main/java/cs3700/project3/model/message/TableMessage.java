package cs3700.project3.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3700.project3.model.route.RouteEntry;
import lombok.Data;

import java.util.List;

/**
 * Data class that represents a table message.
 */
@Data
public class TableMessage {
    @JsonProperty("src")
    String src;

    @JsonProperty("dst")
    String dst;

    @JsonProperty("type")
    String type = "table";

    @JsonProperty("msg")
    List<RouteEntry> routeEntries;
}
