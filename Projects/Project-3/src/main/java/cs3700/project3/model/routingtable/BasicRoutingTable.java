package cs3700.project3.model.routingtable;

import cs3700.project3.Config;
import cs3700.project3.model.route.RouteEntry;
import cs3700.project3.model.route.RouteUpdate;
import cs3700.project3.model.route.RouteWithdrawal;
import cs3700.project3.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of a routing table.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicRoutingTable implements RoutingTable {
    @NonNull
    private final Integer as;

    @NonNull
    private final Map<String, List<RouteUpdate>> peerRouteUpdatesMap;

    @NonNull
    private final List<RouteEntry> routeEntries;

    @Override
    public void update(@NonNull String peer, @NonNull RouteUpdate routeUpdate) {
        // Track route update in saved list
        peerRouteUpdatesMap.putIfAbsent(peer, new ArrayList<>());
        peerRouteUpdatesMap.get(peer).add(routeUpdate);

        updateWithoutTracking(peer, routeUpdate);
    }

    @Override
    public void withdraw(@NonNull String peer, @NonNull RouteWithdrawal routeWithdrawal) {
        // Remove specified route update from list
        final List<RouteUpdate> peerRouteUpdates = peerRouteUpdatesMap.get(peer);
        for (int i = 0; i < peerRouteUpdates.size(); ++i) {
            final RouteUpdate routeUpdate = peerRouteUpdates.get(i);
            if (routeWithdrawal.getNetwork().equals(routeUpdate.getNetwork())
                && routeWithdrawal.getNetmask().equals(routeUpdate.getNetmask())
            ) {
                peerRouteUpdates.remove(i);
                break;
            }
        }

        // Regenerate route entries from new list of route updates
        routeEntries.clear();
        for (final String peerKey : peerRouteUpdatesMap.keySet()) {
            for (final RouteUpdate routeUpdate : peerRouteUpdatesMap.get(peerKey)) {
                updateWithoutTracking(peerKey, routeUpdate);
            }
        }
    }

    @Override
    public String nextHop(@NonNull String dst) {
        RouteEntry bestRoute = null;

        for (RouteEntry routeEntry : routeEntries) {
            if (Util.getPrefixBitsFrom(dst, routeEntry.getNetmask())
                != Util.getPrefixBitsFrom(routeEntry.getNetwork(), routeEntry.getNetmask())
            ) {
                continue;
            }

            if (bestRoute == null) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare netmasks
            final int netmaskComparison = Integer.compareUnsigned(
                Util.getBitsFrom(routeEntry.getNetmask()),
                Util.getBitsFrom(bestRoute.getNetmask())
            );
            if (netmaskComparison < 0) {
                continue;
            }
            if (netmaskComparison > 0) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare local preferences
            if (routeEntry.getLocalPref() < bestRoute.getLocalPref()) {
                continue;
            }
            if (routeEntry.getLocalPref() > bestRoute.getLocalPref()) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare self origins
            if (!routeEntry.getSelfOrigin() && bestRoute.getSelfOrigin()) {
                continue;
            }
            if (routeEntry.getSelfOrigin() && !bestRoute.getSelfOrigin()) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare length of AS paths
            if (routeEntry.getAsPath().size() > bestRoute.getAsPath().size()) {
                continue;
            }
            if (routeEntry.getAsPath().size() < bestRoute.getAsPath().size()) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare origins
            final int originComparison = Config.ORIGIN_RANKINGS.get(routeEntry.getOrigin())
                - Config.ORIGIN_RANKINGS.get(bestRoute.getOrigin());
            if (originComparison > 0) {
                continue;
            }
            if (originComparison < 0) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare peer addresses
            if (routeEntry.getPeer().compareTo(bestRoute.getPeer()) < 0) {
                bestRoute = routeEntry;
            }
        }

        return bestRoute != null ? bestRoute.getPeer() : "";
    }

    @Override
    public List<RouteEntry> dumpContents() {
        return Collections.unmodifiableList(routeEntries);
    }

    @Override
    public int getAs() {
        return as;
    }

    private void updateWithoutTracking(@NonNull String peer, @NonNull RouteUpdate routeUpdate) {
        final RouteEntry newRouteEntry = new RouteEntry();
        newRouteEntry.setNetwork(routeUpdate.getNetwork());
        newRouteEntry.setNetmask(routeUpdate.getNetmask());
        newRouteEntry.setPeer(peer);
        newRouteEntry.setLocalPref(routeUpdate.getLocalPref());
        newRouteEntry.setSelfOrigin(routeUpdate.getSelfOrigin());
        newRouteEntry.setAsPath(routeUpdate.getAsPath());
        newRouteEntry.setOrigin(routeUpdate.getOrigin());

        // Insert new route entry into its sorted table position
        int i;
        final int newRouteNetworkBits = Util.getBitsFrom(newRouteEntry.getNetwork());
        for (i = 0; i < routeEntries.size(); ++i) {
            final RouteEntry currentRouteEntry = routeEntries.get(i);

            // Replace old route if it already exists
            if (peer.equals(currentRouteEntry.getPeer())
                && newRouteEntry.getNetwork().equals(currentRouteEntry.getNetwork())
                && newRouteEntry.getNetmask().equals(currentRouteEntry.getNetmask())
            ) {
                routeEntries.set(i, newRouteEntry);
                break;
            }

            final int currentRouteNetworkBits = Util.getBitsFrom(currentRouteEntry.getNetwork());
            if (Integer.compareUnsigned(newRouteNetworkBits, currentRouteNetworkBits) <= 0) {
                routeEntries.add(i, newRouteEntry);
                break;
            }
        }
        if (i == routeEntries.size()) {
            routeEntries.add(newRouteEntry);
        }

        // Aggregate routes, if possible
        for (i = 0; i < routeEntries.size() - 1; ++i) {
            final RouteEntry currentRouteEntry = routeEntries.get(i);
            final RouteEntry nextRouteEntry = routeEntries.get(i + 1);

            // Don't aggregate routes that have mismatching metadata
            if (!currentRouteEntry.getPeer().equals(nextRouteEntry.getPeer())
                || !currentRouteEntry.getLocalPref().equals(nextRouteEntry.getLocalPref())
                || !currentRouteEntry.getSelfOrigin().equals(nextRouteEntry.getSelfOrigin())
                || !currentRouteEntry.getAsPath().equals(nextRouteEntry.getAsPath())
                || !currentRouteEntry.getOrigin().equals(nextRouteEntry.getOrigin())
            ) {
                continue;
            }

            final int currentRouteNetmaskBits = Util.getBitsFrom(currentRouteEntry.getNetmask());
            final int nextRouteNetmaskBits = Util.getBitsFrom(nextRouteEntry.getNetmask());
            final int currentRouteNetmaskBitCount = Integer.bitCount(currentRouteNetmaskBits);
            final int nextRouteNetmaskBitCount = Integer.bitCount(nextRouteNetmaskBits);

            // Don't aggregate routes with different netmasks
            if (currentRouteNetmaskBitCount != nextRouteNetmaskBitCount) {
                continue;
            }

            final int currentRouteNetworkBits = Util.getBitsFrom(currentRouteEntry.getNetwork()) & currentRouteNetmaskBits;
            final int nextRouteNetworkBits = Util.getBitsFrom(nextRouteEntry.getNetwork()) & nextRouteNetmaskBits;
            final int currentRoutePrefixBits = currentRouteNetworkBits >> 32 - currentRouteNetmaskBitCount;
            final int nextRoutePrefixBits = nextRouteNetworkBits >> 32 - nextRouteNetmaskBitCount;

            // Only aggregate routes where the last network bit differs
            if ((currentRoutePrefixBits ^ nextRoutePrefixBits) != 1) {
                continue;
            }

            final int aggregatedRouteNetmaskBits = currentRouteNetmaskBits << 1;
            final String aggregatedRouteNetmask = Util.getQuadFrom(aggregatedRouteNetmaskBits);
            final String aggregatedRouteNetwork = Util.getQuadFrom(
                currentRouteNetworkBits & aggregatedRouteNetmaskBits
            );

            final RouteEntry aggregatedRouteEntry = new RouteEntry();
            aggregatedRouteEntry.setNetwork(aggregatedRouteNetwork);
            aggregatedRouteEntry.setNetmask(aggregatedRouteNetmask);
            aggregatedRouteEntry.setPeer(currentRouteEntry.getPeer());
            aggregatedRouteEntry.setLocalPref(currentRouteEntry.getLocalPref());
            aggregatedRouteEntry.setSelfOrigin(currentRouteEntry.getSelfOrigin());
            aggregatedRouteEntry.setAsPath(currentRouteEntry.getAsPath());
            aggregatedRouteEntry.setOrigin(currentRouteEntry.getOrigin());

            // Aggregate routes
            routeEntries.set(i, aggregatedRouteEntry);
            routeEntries.remove(i + 1);

            // Start reprocessing from previous element in case more aggregation can be done
            i = Math.max(i - 2, -1);
        }
    }
}
