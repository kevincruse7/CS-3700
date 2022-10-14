package cs3700.project3.model.routingtable;

import cs3700.project3.model.route.RouteEntry;
import cs3700.project3.model.route.RouteUpdate;
import cs3700.project3.model.route.RouteWithdrawal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.*;

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
    private final List<RouteEntry> routeEntries;

    // TODO: Implement aggregation
    @Override
    public void update(@NonNull String peer, @NonNull RouteUpdate routeUpdate) {
        final RouteEntry newRouteEntry = new RouteEntry();

        newRouteEntry.setNetwork(routeUpdate.getNetwork());
        newRouteEntry.setNetmask(routeUpdate.getNetmask());
        newRouteEntry.setPeer(peer);
        newRouteEntry.setLocalPref(routeUpdate.getLocalPref());
        newRouteEntry.setAsPath(routeUpdate.getAsPath());
        newRouteEntry.setSelfOrigin(routeUpdate.getSelfOrigin());
        newRouteEntry.setOrigin(routeUpdate.getOrigin());

        final int newRouteEntryPrefix = getPrefixFrom(newRouteEntry.getNetwork(), newRouteEntry.getNetmask());
        int i;

        // Update route entry if it already exists in the routing table
        for (i = 0; i < routeEntries.size(); ++i) {
            final RouteEntry currentRouteEntry = routeEntries.get(i);

            if (newRouteEntryPrefix == getPrefixFrom(currentRouteEntry.getNetwork(), currentRouteEntry.getNetmask())) {
                routeEntries.set(i, newRouteEntry);
                break;
            }
        }

        // Otherwise, add new route entry to the routing table
        if (i == routeEntries.size()) {
            routeEntries.add(newRouteEntry);
        }
    }

    // TODO: Implement disaggregation
    @Override
    public void withdraw(@NonNull String peer, @NonNull RouteWithdrawal routeWithdrawal) {
        final String routeWithdrawalNetwork = routeWithdrawal.getNetwork();
        final String routeWithdrawalNetmask = routeWithdrawal.getNetmask();

        int i;

        for (i = 0; i < routeEntries.size(); ++i) {
            final RouteEntry routeEntry = routeEntries.get(i);

            if (routeEntry.getNetwork().equals(routeWithdrawalNetwork)
                && routeEntry.getNetmask().equals(routeWithdrawalNetmask)
                && peer.equals(routeEntry.getPeer())
            ) {
                routeEntries.remove(i);
                break;
            }
        }

        if (i == routeEntries.size()) {
            throw new IllegalArgumentException(
                String.format("Route not found in the routing table: %s, %s", peer, routeWithdrawal)
            );
        }
    }

    @Override
    public String nextHop(@NonNull String dst) {
        RouteEntry bestRoute = null;

        for (RouteEntry routeEntry : routeEntries) {
            if (getPrefixFrom(dst, routeEntry.getNetmask())
                == getPrefixFrom(routeEntry.getNetwork(), routeEntry.getNetmask())
            ) {
                if (bestRoute == null) {
                    bestRoute = routeEntry;
                    continue;
                }

                final int netmaskComparison = Integer.compareUnsigned(
                    getQuadBitsFrom(routeEntry.getNetmask()),
                    getQuadBitsFrom(bestRoute.getNetmask())
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

    private static int getPrefixFrom(@NonNull String network, @NonNull String netmask) {
        return getQuadBitsFrom(network) & getQuadBitsFrom(netmask);
    }

    private static int getQuadBitsFrom(@NonNull String quad) {
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
}
