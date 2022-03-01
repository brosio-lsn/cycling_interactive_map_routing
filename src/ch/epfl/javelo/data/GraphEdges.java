package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevation) {
    /**
     * returns whether the given edge's orientation goes in the opposite direction of the OSM way which it provides from
     *
     * @param edgeId index of the edge. It is multiplied by 10 because there are 10 attributes per edge in edgesBuffer.
     * @return whether the edge's orientation goes in the opposite direction of the OSM way which it provides from
     * True if so, false otherwise.
     */
    public boolean isInverted(int edgeId) {
        return (edgesBuffer.getInt(edgeId * 10 + 1) < 0);
    }

    /**
     * returns the id of the target node of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge to extract its destination from.
     * @return the id of the target node of the Edge at edgeId.
     */
    public int targetNodeId(int edgeId) {
        return Bits.extractUnsigned(edgesBuffer.getInt(edgeId * 10 + 1), 0, 31);
    }

    /**
     * returns the length of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the length to compute the length of.
     * @return the length of the edge whose id is edgeId.
     */
    public double length(int edgeId) {
        return (Q28_4.asDouble(edgesBuffer.getShort(edgeId * 10 + 5)));
    }

    /**
     * returns the elevation gain of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge to extract the gain of elevation from.
     * @return the elevation gain of the edge whose id is edgeId.
     */
    public double elevationGain(int edgeId) {
        return edgesBuffer.getShort(edgeId * 10 + 7);
    }

    /**
     * returns whether the given edge, whose id is edgeId, has a profile
     *
     * @param edgeId id of the edge.
     * @return whether the edge whose id is edgeId has a profile.
     */
    public boolean hasProfile(int edgeId) {
        return (Bits.extractUnsigned(profileIds.get(edgeId), 30, 1) != 0);
    }

    /**
     * returns the samples of the OSM profiles of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge.
     * @return the samples of OSM profiles of the edge whose id is edgeId.
     */

    public float[] profileSamples(int edgeId) {
        int nbOfProfiles = (int)Math.ceil(length(edgeId)/2) + 1;
        int type = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        int profileId = Bits.extractSigned(profileIds.get(edgeId), 0, 30);
        float[] samples = new float[nbOfProfiles];
        switch (type) {
            case 0 :
                return new float []{};
            case 1 :
                for (int i = 0; i < nbOfProfiles; i++) {
                    samples[i] = elevation.get(profileId+i);
                }
                return samples;
            case 2:
                samples[0] = elevation.get(profileId);
                for (int i = 1; i < nbOfProfiles; i++) {
                    samples[i] = (float)Q28_4.asDouble(Bits.extractUnsigned(elevation.get(profileId+i), 0, 8));
                }
                return samples;
            case 3:
                samples[0] = elevation.get(profileId);
                for (int i = 1; i < nbOfProfiles; i++) {
                    samples[i] = (float)Q28_4.asDouble(Bits.extractUnsigned(elevation.get(profileId+i), 0, 4));
                }
                return samples;
        }
        return samples;
    }

    /**
     * returns the identity of the set of attributes attached to the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge to return the index from.
     * @return the identity of the set of attributes attached to the given edge, whose id is edgeId.
     */
    public int attributesIndex(int edgeId) {
        return edgesBuffer.getShort(edgeId * 10 + 9);
    }
}
