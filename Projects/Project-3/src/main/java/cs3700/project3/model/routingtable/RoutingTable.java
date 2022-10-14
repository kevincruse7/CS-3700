package cs3700.project3.model.routingtable;

import cs3700.project3.model.route.RouteEntry;
import cs3700.project3.model.route.RouteUpdate;
import cs3700.project3.model.route.RouteWithdrawal;

import java.util.List;

public interface RoutingTable {
    void update(String peer, RouteUpdate routeUpdate);
    void withdraw(String peer, RouteWithdrawal routeWithdrawal);
    String nextHop(String dst);
    List<RouteEntry> dumpContents();
    int getAs();
}
