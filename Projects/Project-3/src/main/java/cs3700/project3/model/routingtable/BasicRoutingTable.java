package cs3700.project3.model.routingtable;

import cs3700.project3.model.route.RouteEntry;
import cs3700.project3.model.route.RouteUpdate;
import cs3700.project3.model.route.RouteWithdrawal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BasicRoutingTable implements RoutingTable {
    private static final Map<String, Integer> ORIGIN_RANKINGS = Map.of(
        "IGP", 0,
        "EGP", 1,
        "UNK", 2
    );

    @NonNull
    private final Integer as;

    @NonNull
    private final Map<String, List<RouteUpdate>> peerRouteUpdatesMap;

    @NonNull
    private final List<RouteEntry> routeEntries;

    private static int getPrefixFrom(@NonNull String network, @NonNull String netmask) {
        return getBitsFrom(network) & getBitsFrom(netmask);
    }

    private static int getBitsFrom(@NonNull String quad) {
        final Integer[] quadBytes = Arrays.stream(quad.split("\\."))
            .map(Integer::parseInt)
            .toArray(Integer[]::new);

        int quadBits = 0;

        // Coalesce quad bytes into single bit-string
        for (int i = 0; i < 4; ++i) {
            quadBits += quadBytes[i] << 8 * (3 - i);
        }

        return quadBits;
    }

    private static String getQuadFrom(int bits) {
        final StringBuilder quadBuilder = new StringBuilder();
        quadBuilder.append(bits >> 24 & 255);

        for (int i = 2; i >= 0; --i) {
            quadBuilder.append('.');
            quadBuilder.append(bits >> 8 * i & 255);
        }

        return quadBuilder.toString();
    }

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
            if (getPrefixFrom(dst, routeEntry.getNetmask())
                != getPrefixFrom(routeEntry.getNetwork(), routeEntry.getNetmask())
            ) {
                continue;
            }

            if (bestRoute == null) {
                bestRoute = routeEntry;
                continue;
            }

            final int netmaskComparison = Integer.compareUnsigned(
                getBitsFrom(routeEntry.getNetmask()),
                getBitsFrom(bestRoute.getNetmask())
            );

            // Compare netmasks
            if (netmaskComparison < 0) {
                continue;
            } else if (netmaskComparison > 0) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare local preferences
            if (routeEntry.getLocalPref() < bestRoute.getLocalPref()) {
                continue;
            } else if (routeEntry.getLocalPref() > bestRoute.getLocalPref()) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare self origins
            if (!routeEntry.getSelfOrigin() && bestRoute.getSelfOrigin()) {
                continue;
            } else if (routeEntry.getSelfOrigin() && !bestRoute.getSelfOrigin()) {
                bestRoute = routeEntry;
                continue;
            }

            // Compare length of AS paths
            if (routeEntry.getAsPath().size() > bestRoute.getAsPath().size()) {
                continue;
            } else if (routeEntry.getAsPath().size() < bestRoute.getAsPath().size()) {
                bestRoute = routeEntry;
                continue;
            }

            final int originComparison = ORIGIN_RANKINGS.get(routeEntry.getOrigin())
                - ORIGIN_RANKINGS.get(bestRoute.getOrigin());

            // Compare origins
            if (originComparison > 0) {
                continue;
            } else if (originComparison < 0) {
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

        int i;
        final int newRouteNetworkBits = getBitsFrom(newRouteEntry.getNetwork());

        // Insert new route entry into its sorted table position
        for (i = 0; i < routeEntries.size(); ++i) {
            final RouteEntry currentRouteEntry = routeEntries.get(i);

            if (peer.equals(currentRouteEntry.getPeer())
                && newRouteEntry.getNetwork().equals(currentRouteEntry.getNetwork())
                && newRouteEntry.getNetmask().equals(currentRouteEntry.getNetmask())
            ) {
                routeEntries.set(i, newRouteEntry);
                break;
            }

            final int currentRouteNetworkBits = getBitsFrom(currentRouteEntry.getNetwork());

            if (Integer.compareUnsigned(newRouteNetworkBits, currentRouteNetworkBits) <= 0) {
                routeEntries.add(i, newRouteEntry);
                break;
            }
        }

        if (i == routeEntries.size()) {
            routeEntries.add(newRouteEntry);
        }

        // Aggregate entries, if possible
        for (i = 0; i < routeEntries.size() - 1; ++i) {
            final RouteEntry currentRouteEntry = routeEntries.get(i);
            final RouteEntry nextRouteEntry = routeEntries.get(i + 1);

            if (!currentRouteEntry.getPeer().equals(nextRouteEntry.getPeer())
                || !currentRouteEntry.getLocalPref().equals(nextRouteEntry.getLocalPref())
                || !currentRouteEntry.getSelfOrigin().equals(nextRouteEntry.getSelfOrigin())
                || !currentRouteEntry.getAsPath().equals(nextRouteEntry.getAsPath())
                || !currentRouteEntry.getOrigin().equals(nextRouteEntry.getOrigin())
            ) {
                continue;
            }

            final int currentRouteNetmaskBits = getBitsFrom(currentRouteEntry.getNetmask());
            final int nextRouteNetmaskBits = getBitsFrom(nextRouteEntry.getNetmask());

            final int currentRouteNetmaskBitCount = Integer.bitCount(currentRouteNetmaskBits);
            final int nextRouteNetmaskBitCount = Integer.bitCount(nextRouteNetmaskBits);

            if (currentRouteNetmaskBitCount != nextRouteNetmaskBitCount) {
                continue;
            }

            final int currentRouteNetworkBits = getBitsFrom(currentRouteEntry.getNetwork()) & currentRouteNetmaskBits;
            final int nextRouteNetworkBits = getBitsFrom(nextRouteEntry.getNetwork()) & nextRouteNetmaskBits;

            final int currentRoutePrefixBits = currentRouteNetworkBits >> 32 - currentRouteNetmaskBitCount;
            final int nextRoutePrefixBits = nextRouteNetworkBits >> 32 - nextRouteNetmaskBitCount;

            if ((currentRoutePrefixBits ^ nextRoutePrefixBits) != 1) {
                continue;
            }

            final int aggregatedRouteNetmaskBits = currentRouteNetmaskBits << 1;
            final String aggregatedRouteNetmask = getQuadFrom(aggregatedRouteNetmaskBits);
            final String aggregatedRouteNetwork = getQuadFrom(currentRouteNetworkBits & aggregatedRouteNetmaskBits);

            final RouteEntry aggregatedRouteEntry = new RouteEntry();
            aggregatedRouteEntry.setNetwork(aggregatedRouteNetwork);
            aggregatedRouteEntry.setNetmask(aggregatedRouteNetmask);
            aggregatedRouteEntry.setPeer(currentRouteEntry.getPeer());
            aggregatedRouteEntry.setLocalPref(currentRouteEntry.getLocalPref());
            aggregatedRouteEntry.setSelfOrigin(currentRouteEntry.getSelfOrigin());
            aggregatedRouteEntry.setAsPath(currentRouteEntry.getAsPath());
            aggregatedRouteEntry.setOrigin(currentRouteEntry.getOrigin());

            routeEntries.set(i, aggregatedRouteEntry);
            routeEntries.remove(i + 1);

            i = Math.max(i - 2, -1);
        }
    }
}
