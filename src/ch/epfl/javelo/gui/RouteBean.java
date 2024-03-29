package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * Computes the route between the waypoints on the map
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class RouteBean {
    /**
     * routeComputer to use to compute the itineraries between the multiple waypoints.
     */
    private final RouteComputer routeComputer;

    /**
     * list of waypoints to compute the itinerary between.
     */
    private final ObservableList<WayPoint> waypoints;
    /**
     * itinerary linking all the waypoints on the map.
     */
    private final ObjectProperty<Route> route;
    /**
     * highlighted position draw by a circle on the itinerary
     */
    private final DoubleProperty highlightedPosition;
    /**
     * elevationProfile of the route.
     */
    private final ObjectProperty<ElevationProfile> elevationProfile;
    /**
     * Cache used to avoid repetitive computations of the best itineraries between two points.
     */
    private final Map<Pair<Integer, Integer>, Route> bestRouteCache;
    /**
     * List representing the multiple itineraries linking the multiple waypoints on the map.
     */
    private final List<Route> theRoutes;
    /**
     * initial size of the bestRouteCache
     */
    private final static int INITIAL_CAPACITY = 30;

    /**
     * default load factor of any LinkedHashMap -> to be used in the constructor of bestRouteCache.
     */
    private final static float LOAD_FACTOR = 0.75f;
    /**
     * determines whether any iterator accesses the elements in a LinkedHashMap by their reversed order of access (true here)
     */
    private final static boolean ELDEST_ACCESS = true;
    /**
     * Max step length of the elevation profile computer
     */
    private static final int MAX_STEP_LENGTH = 5;


    /**
     * constructor of the RouteBean class, creates a bean which is used for observation purposes.
     *
     * @param routeComputer RouteComputer which will be used to compute the route, which will be later on distributed as
     *                      an observable value.
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        this.bestRouteCache = new LinkedHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, ELDEST_ACCESS);
        this.route = new SimpleObjectProperty<>();
        this.waypoints = FXCollections.observableArrayList();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.theRoutes = new ArrayList<>();
        installListeners();
    }

    /**
     * returns the highlightedPositionProperty of this routeBean.
     *
     * @return the highlightedPositionProperty of this routeBean.
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Returns the position on the route of the highlighted point.
     *
     * @return the position on the route of the highlighted point.
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Sets the highlighted position on the route to the given position.
     *
     * @param position position on the route to set the highlighted position at.
     */
    public void setHighlightedPosition(double position) {
        if (route.get() != null) highlightedPosition.set(Math2.clamp(0, position, route.get().length()));
    }

    /**
     * Returns the itinerary between all the waypoints in the list of waypoints. It is on read only so that it cannot
     * be accessed from the exterior, and no mischievous computations can be made.
     *
     * @return the itinerary between all the waypoints
     */
    public ReadOnlyObjectProperty<Route> route() {
        return route;
    }

    /**
     * Returns the elevationProfile of the route linking all the waypoints in the list of waypoints. It
     * is on read only so that it cannot be accessed from the exterior, and no mischievous computations can be made.
     *
     * @return the elevationProfile of the route linking all the waypoints in the list of waypoints.
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return elevationProfile;
    }

    /**
     * returns the observable list of waypoints on the route
     *
     * @return the observable list of waypoints on the route
     */
    public ObservableList<WayPoint> getWaypoints() {
        return waypoints;
    }

    /**
     * returns the index of the non-empty segment at the given position.
     *
     * @param position position to compute the index of the non-empty segment at.
     * @return the index of the non-empty segment at the given position.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * installs the listener on the list of waypoints, and makes it so the program reacts
     * to any change it might have.
     */
    private void installListeners() {
        waypoints.addListener((ListChangeListener<WayPoint>) c -> updateRoute());
    }

    /**
     * updates the route of this routeBean when the list of waypoint changes. I.e. some waypoint changed position,
     * a waypoint has been added or removed.
     */
    private void updateRoute() {
        theRoutes.clear();
        if (waypoints.size() >= 2) {
            for (int i = 0; i < waypoints.size() - 1; i++) {
                int nodeIdOfFirstWaypoint = waypoints.get(i).closestNodeId();
                int nodeIdOfSecondWaypoint = waypoints.get(i + 1).closestNodeId();
                Pair<Integer, Integer> pairOfWaypoints = new Pair<>(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint);
                if (!(nodeIdOfFirstWaypoint == nodeIdOfSecondWaypoint)) {
                    if (!(bestRouteCache.containsKey(pairOfWaypoints))) {
                        Route bestRouteBetween = routeComputer.bestRouteBetween(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint);
                        if (bestRouteBetween == null) {
                            nullifyProperties();
                            return;
                        }
                        bestRouteCache.put(pairOfWaypoints, bestRouteBetween);
                        if (bestRouteCache.size() == INITIAL_CAPACITY) {
                            bestRouteCache.remove(bestRouteCache.keySet().iterator().next());
                        }
                    }
                    theRoutes.add(bestRouteCache.get(pairOfWaypoints));
                }
            }
            route.set(new MultiRoute(theRoutes));
            computeElevationProfile();
        } else {
            nullifyProperties();
        }
    }

    /**
     * Nullifies the properties of this bean in case the list of waypoints cannot compute a route.
     */
    private void nullifyProperties() {
        theRoutes.clear();
        route.setValue(null);
        elevationProfile.set(null);
    }

    /**
     * Computes the elevation profile of the updated route.
     */
    private void computeElevationProfile() {
        elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
    }
}
