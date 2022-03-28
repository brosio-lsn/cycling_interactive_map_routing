package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * represents a single route
 *
 * @author Ambroise AIGUEPERSE (341890)
 * @author Louis ROCHE (345620)
 */

final public class SingleRoute implements Route{

    /**
     * the list containing the edges of the route
     */
    private final List<Edge> edges;

    /**
     * array where the index is the index of a node of the route, and the value is the position of this node along the route, in meters
     */
    private final double[] nodesDistanceTable;

    /**
     * constructor of SingleRoute
     * @param edges the list containing the edges of the route
     */
    public SingleRoute (List<Edge> edges){
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges=List.copyOf(edges);
        nodesDistanceTable=this.createNodesDistanceTable();
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double length=0;
        for(Edge e : edges) length+=e.length();
        return length;
    }

    @Override
    public List<Edge> edges() {
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points= new ArrayList<PointCh>();
        for(Edge e : edges) points.add(e.fromPoint());
        points.add(edges.get(edges.size()-1).toPoint());
        return List.copyOf(points);
    }

    @Override
    public PointCh pointAt(double position) {
        //this treatment is only done here because elswhere is already done
        position = position>this.length()? length() : (position<0? 0: position);
        int finalIndex= binarySearchIndex(position);
        return edges.get(finalIndex).pointAt(position-nodesDistanceTable[finalIndex]);
    }

    @Override
    public int nodeClosestTo(double position) {
        int finalIndex= binarySearchIndex(position);
        Edge edge = edges.get(finalIndex);
        return ((position-nodesDistanceTable[finalIndex])/edge.length()<=0.5 ? edge.fromNodeId() : edge.toNodeId());

    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePoint = RoutePoint.NONE;
        for(int i =0; i<edges.size();++i){
            Edge edge = edges.get(i);
            double positionOnEdge = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            PointCh p = edge.pointAt(positionOnEdge);
            routePoint =routePoint.min(p, nodesDistanceTable[i]+positionOnEdge, point.distanceTo(p));
        }
        return routePoint;
    }

    @Override
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length());
        int finalIndex= binarySearchIndex(clampedPosition);
        return (edges.get(finalIndex).elevationAt(clampedPosition-nodesDistanceTable[finalIndex])) ;
    }

    /**
     * fills the attribute nodesDistanceTable (array where the index is the index of a node of the route, and the value is the position of this node along the route, in meters)
     * @return  nodesDistanceTable filled
     */
    private double[] createNodesDistanceTable(){
        double[] nodesDistanceTable= new double[edges.size()+1];
        double lengthSum=0;
        for(int i=0; i<edges.size();++i){
            lengthSum+=edges.get(i).length();
            nodesDistanceTable[i+1]=lengthSum;
        };
        return nodesDistanceTable;
    }

    /**
     * returns the index of the starting node of the edge containing
     * the given position on the route according to the binary search performed on the attribute nodesDistanceTable
     * @param position the position searched by the binary search
     * @return the index of the starting node of the edge containing the given position on the route
     */
    private int binarySearchIndex (double position){
        int binaryIndex= Arrays.binarySearch(nodesDistanceTable, position);
        int finalIndex;
        if(binaryIndex == nodesDistanceTable.length-1) finalIndex=nodesDistanceTable.length-2;
        else if(binaryIndex >=0) finalIndex = binaryIndex;
        else if(binaryIndex == -nodesDistanceTable.length-1) finalIndex=nodesDistanceTable.length-2;
        else if(binaryIndex<-1) finalIndex=-binaryIndex-2;
        else finalIndex =0;
        return finalIndex;
    }
}


