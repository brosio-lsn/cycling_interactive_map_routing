package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class Bits {
    /**
     * Default constructor of the Bits class, which is private so that the class is not instanciable.
     */
    private Bits(){}

    /**
     * Extracts the signed expression of the bit of length 'length', which starts at the 'start'th bit from
     * the bit vector 'value'.
     * @param value bit vector to extract the result from.
     * @param start # of the bit to start the extraction from.
     * @param length length of the bit to be extracted, which is also the difference between the # of the
     *               starting bit, and that of the ending bit.
     * @return the signed expression of the bit of length 'length' which starts at the 'start'th bit
     * from the bit vector 'value'
     */
    public int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(length > 0);
        return (value << (31-(start)+length)) >>> (31-length);
    }

    /**
     * Extract the unsigned expression of the bit of length 'length', which starts at the 'start'th bit from
     * the bit vector 'value'.
     * @param value bit vector to extract the result from.
     * @param start # of the bit to start the extraction from.
     * @param length length of the bit to be extracted, which is also the difference between the # of the
     *               starting bit, and that of the ending bit.
     * @return the unsigned expression of the bit of length 'length' which starts at the 'start'th bit
     * from the bit vector 'value'
     */
    public int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(value == Math.pow(2, 32));
        return (value << (31-(start)+length)) >> (31-length);
    }
}
