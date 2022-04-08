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
    /**
     * {@inheritDoc}
     * @param position : position of the point to compute the index of segment at.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public double length() {
        double length=0;
        for(Edge e : edges) length+=e.length();
        return length;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points= new ArrayList<>();
        for(Edge e : edges) points.add(e.fromPoint());
        points.add(edges.get(edges.size()-1).toPoint());
        //do not return a copy here because PointCh is immuable
        return points;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, this.length());
        int finalIndex= binarySearchIndex(position);
        return edges.get(finalIndex).pointAt(position-nodesDistanceTable[finalIndex]);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeClosestTo(double position) {
        int finalIndex= binarySearchIndex(position);
        Edge edge = edges.get(finalIndex);
        double positionVSEgdeLengthRatio = (position-nodesDistanceTable[finalIndex])/edge.length();
        return (positionVSEgdeLengthRatio<=0.5 ? edge.fromNodeId() : edge.toNodeId());

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePoint = RoutePoint.NONE;
        for(int i =0; i<edges.size();++i){
            Edge edge = edges.get(i);
            double positionOnEdge = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            PointCh pointOnEdge = edge.pointAt(positionOnEdge);
            routePoint =routePoint.min(pointOnEdge, nodesDistanceTable[i]+positionOnEdge, point.distanceTo(pointOnEdge));
        }
        return routePoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length());
        int finalIndex= binarySearchIndex(clampedPosition);
        Edge edgeAtPosition = edges.get(finalIndex);
        double positionOnEdge=clampedPosition-nodesDistanceTable[finalIndex];
        return edgeAtPosition.elevationAt(positionOnEdge);
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
        }
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
        //adapting the final index depending on the result of the binarySearch
        if(binaryIndex == nodesDistanceTable.length-1 || binaryIndex == -nodesDistanceTable.length-1) finalIndex=nodesDistanceTable.length-2;
        else if(binaryIndex >=0) finalIndex = binaryIndex;
        else if(binaryIndex<-1) finalIndex=-binaryIndex-2;
        else finalIndex =0;
        return finalIndex;
    }
}


