package cs3700.project3.model.routingtable;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutingTableFactory {
    public static RoutingTable createRoutingTable(int as) {
        return new BasicRoutingTable(as, new HashMap<>(), new ArrayList<>());
    }
}
