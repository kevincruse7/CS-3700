package cs3700.project3.model.routingtable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Factory class for creating routing tables.
 */
public class RoutingTableFactory {
    /**
     * Creates a routing table affiliated with the given autonomous system.
     *
     * @param as Autonomous system that created routing table should affiliate with.
     * @return Created routing table.
     */
    public static RoutingTable createRoutingTable(int as) {
        return new BasicRoutingTable(as, new HashMap<>(), new ArrayList<>());
    }
}
