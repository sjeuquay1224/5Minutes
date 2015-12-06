package com.directions.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Async Task to access the Google Direction API and return the routing data.
 * Created by Furkan Tektas on 10/14/14.
 */
public class Routing extends AbstractRouting {

    private final TravelMode travelMode;
    private final RouteMode  routeMode;
    private final List<LatLng> waypoints;
    private final int avoidKinds;
    private final boolean optimize;

    private Routing(Builder builder) {
        super(builder.listener);
        this.travelMode = builder.travelMode;
        this.waypoints = builder.waypoints;
        this.avoidKinds = builder.avoidKinds;
        this.optimize = builder.optimize;
        this.routeMode = builder.routeMode;
    }

    protected String constructURL () {
        final StringBuffer stringBuffer = new StringBuffer(AbstractRouting.DIRECTIONS_API_URL);

        // origin
        final LatLng origin = waypoints.get(0);
        stringBuffer.append("origin=");
        stringBuffer.append(origin.latitude);
        stringBuffer.append(',');
        stringBuffer.append(origin.longitude);

        // destination
        final LatLng destination = waypoints.get(waypoints.size() - 1);
        stringBuffer.append("&destination=");
        stringBuffer.append(destination.latitude);
        stringBuffer.append(',');
        stringBuffer.append(destination.longitude);

        // travel
        stringBuffer.append("&mode=");
        stringBuffer.append(travelMode.getValue());

        // waypoints
        if (waypoints.size() > 2) {
            stringBuffer.append("&waypoints=");
            if(optimize)
                stringBuffer.append("optimize:true|");
            for (int i = 1; i < waypoints.size() - 1; i++) {
                final LatLng p = waypoints.get(i);
                stringBuffer.append("via:"); // we don't want to parse the resulting JSON for 'legs'.
                stringBuffer.append(p.latitude);
                stringBuffer.append(",");
                stringBuffer.append(p.longitude);
                stringBuffer.append("|");
            }
        }

        // avoid
        if (avoidKinds > 0) {
            stringBuffer.append("&avoid=");
            stringBuffer.append(AvoidKind.getRequestParam(avoidKinds));
        }

        if (routeMode == RouteMode.SHORTEST) {
            stringBuffer.append("&alternatives=true");
        }

        // sensor
        stringBuffer.append("&sensor=true");

        return stringBuffer.toString();
    }

    public static class Builder {

        private TravelMode travelMode;
        private RouteMode  routeMode;
        private List<LatLng> waypoints;
        private int avoidKinds;
        private RoutingListener listener;
        private boolean optimize;

        public Builder () {
            this.travelMode = TravelMode.DRIVING;
            this.routeMode = RouteMode.FASTEST;
            this.waypoints = new ArrayList<>();
            this.avoidKinds = 0;
            this.listener = null;
            this.optimize = false;
        }

        public Builder travelMode (TravelMode travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        public Builder routeMode (RouteMode routeMode) {
            this.routeMode = routeMode;
            return this;
        }

        public Builder waypoints (LatLng... points) {
            waypoints.clear();
            for (LatLng p : points) {
                waypoints.add(p);
            }
            return this;
        }

        public Builder waypoints (List<LatLng> waypoints) {
            this.waypoints = new ArrayList<>(waypoints);
            return this;
        }

        public Builder optimize(boolean optimize) {
            this.optimize = optimize;
            return this;
        }

        public Builder avoid (AvoidKind... avoids) {
            for (AvoidKind avoidKind : avoids) {
                this.avoidKinds |= avoidKind.getBitValue();
            }
            return this;
        }

        public Builder withListener (RoutingListener listener) {
            this.listener = listener;
            return this;
        }

        public Routing build () {
            if (this.waypoints.size() < 2) {
                throw new IllegalArgumentException("Must supply at least two waypoints to route between.");
            }
            if (this.waypoints.size() <= 2 && this.optimize) {
                throw new IllegalArgumentException("You need at least three waypoints to enable optimize");
            }
            return new Routing(this);
        }

    }

}
