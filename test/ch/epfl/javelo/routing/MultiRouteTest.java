package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {

    static void coucou(){

    }
    @Test
    void indexOfSegmentAt() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(5, finale.indexOfSegmentAt(5500));

    }

    @Test
    void length() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(6000, finale.length());
    }

    @Test
    void edges() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        List<Edge> ouaisouais = finale.edges();
      //  assertTrue(finale.edges().contains(new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
       //         new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), 1000, Functions.sampled(new float[] {1f, 1f}, 1000))));
    }

    @Test
    void points() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertTrue(finale.points().contains(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N))
                && finale.points().contains(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20)));
        assertEquals(2, finale.points().size());
    }

    @Test
    void pointAt() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), finale.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N+20), finale.pointAt(finale.length()));
    }

    @Test
    void elevationAt() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(1f, finale.elevationAt(1000));
    }

    @Test
    void nodeClosestTo() throws IOException {

        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 2, 1, routeGraph.edgeTargetNodeId(2));
        List<Edge> edges = List.of(edge1);
        Route coucou = new SingleRoute(edges);
        Route salut = new SingleRoute(List.of(edge2));
        List<Route> listeRoute = List.of(coucou, salut);
        MultiRoute multiRoute = new MultiRoute(listeRoute);
        assertEquals(0, multiRoute.nodeClosestTo(10));
        assertEquals(1, multiRoute.nodeClosestTo(60));
        assertEquals(2, multiRoute.nodeClosestTo(150));


    }

    @Test
    void pointClosestTo() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 2, 1, routeGraph.edgeTargetNodeId(2));
        List<Edge> edges = List.of(edge1);
        Route coucou = new SingleRoute(edges);
        Route salut = new SingleRoute(List.of(edge2));
        List<Route> listeRoute = List.of(coucou, salut);
        MultiRoute multiRoute = new MultiRoute(listeRoute);
        System.out.println(multiRoute.pointClosestTo(new PointCh(2549213, 1166183.5625)));
        System.out.println(multiRoute.pointClosestTo(new PointCh(2549278.75, 1166253)));
    }
}