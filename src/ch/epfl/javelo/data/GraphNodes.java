package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * represents the set of nodes contained in the JaVelo graph
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

/**
 * Constructor of the nodes of the graph.
 *
 * @param buffer the attributes of all the nodes of the graph
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * returns the total number of nodes
     *
     * @return the total number of nodes
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * returns the E coordinate of the node whose identity is given
     *
     * @param nodeId the identity of the considered node
     * @return the E coordinate of the node whose identity is given
     */
    public double nodeE(int nodeId) {
        int indexInBuffer = nodeId * NODE_INTS + OFFSET_E;
        return Q28_4.asDouble(buffer.get(indexInBuffer));
    }

    /**
     * returns the N coordinate of the node whose identity is given
     *
     * @param nodeId the identity of the considered node
     * @return the N coordinate of the node whose identity is given
     */
    public double nodeN(int nodeId) {
        int indexInBuffer = nodeId * NODE_INTS + OFFSET_N;
        return Q28_4.asDouble(buffer.get(indexInBuffer));
    }

    /**
     * returns the number of edges coming out of the node with given identity
     *
     * @param nodeId the identity of the considered node
     * @return the number of edges coming out of the node with given identity
     */
    public int outDegree(int nodeId) {
        int indexInBuffer = nodeId * NODE_INTS + OFFSET_OUT_EDGES;
        return Bits.extractUnsigned(buffer.get(indexInBuffer), 28, 4);
    }

    /**
     * returns the identity of the edgeIndex-th edge coming out of the node with given identity
     *
     * @param nodeId    the identity of the considered node
     * @param edgeIndex the index of the edge in the list containing all the edges coming out of the node
     * @return the identity of the edgeIndex-th edge coming out of the node with given identity
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int indexInBuffer = nodeId * NODE_INTS + OFFSET_OUT_EDGES;
        return Bits.extractUnsigned(buffer.get(indexInBuffer), 0, 28) + edgeIndex;
    }

}
