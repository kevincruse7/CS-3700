package cs3700.project3.model.routingtable;

import cs3700.project3.model.route.RouteEntry;
import cs3700.project3.model.route.RouteUpdate;
import cs3700.project3.model.route.RouteWithdrawal;

import java.util.List;

/**
 * Represents a routing table capable of updating entries, withdrawing entries, and finding routes for received packets.
 */
public interface RoutingTable {
    /**
     * Adds or replaces the given route in the routing table, aggregating entries if possible.
     *
     * @param peer Address of peer that sent the update.
     * @param routeUpdate Route to add or replace.
     */
    void update(String peer, RouteUpdate routeUpdate);

    /**
     * Withdraws the given route from the routing table, disaggregating entries if necessary.
     *
     * @param peer Address of peer that requested the withdrawal.
     * @param routeWithdrawal Route to withdraw.
     */
    void withdraw(String peer, RouteWithdrawal routeWithdrawal);

    /**
     * Determines the next hop address for reaching the given destination.
     *
     * @param dst Address of the final destination.
     * @return Address of the next hop, or the empty string if no route was found.
     */
    String nextHop(String dst);

    /**
     * Provides the list of entries in this routing table.
     *
     * @return List of entries in this routing table.
     */
    List<RouteEntry> dumpContents();

    /**
     * Returns the autonomous system this routing table is affiliated with.
     *
     * @return Autonomous system this routing table is affiliated with.
     */
    int getAs();
}
