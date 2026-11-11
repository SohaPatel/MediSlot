
package com.medislot.backend.algorithm;

import java.util.BitSet;

public class BloomFilter {

    private final int size;
    private final int hashCount;
    private final BitSet bitSet;

    public BloomFilter(int size, int hashCount) {

        if (size <= 0 || hashCount <= 0) {
            throw new IllegalArgumentException(
                    "Size and hash count must be positive."
            );
        }

        this.size = size;
        this.hashCount = hashCount;
        this.bitSet = new BitSet(size);
    }

    // Add an email to the Bloom Filter
    public synchronized void add(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Value cannot be empty."
            );
        }

        for (int i = 0; i < hashCount; i++) {
            int index = getHash(value, i);
            bitSet.set(index);
        }
    }

    // Check whether an email might exist
    public synchronized boolean mightContain(String value) {

        if (value == null || value.isBlank()) {
            return false;
        }

        for (int i = 0; i < hashCount; i++) {

            int index = getHash(value, i);

            if (!bitSet.get(index)) {
                return false;
            }
        }

        return true;
    }

    // Generate multiple hash positions
    private int getHash(String value, int iteration) {

        long hash1 = value.hashCode();

        long hash2 = 0xcbf29ce484222325L;

        for (int i = 0; i < value.length(); i++) {
            hash2 ^= value.charAt(i);
            hash2 *= 0x100000001b3L;
        }

        long combinedHash = hash1 + iteration * hash2;

        return (int) Math.floorMod(combinedHash, (long) size);
    }

    public int getSize() {
        return size;
    }

    public int getHashCount() {
        return hashCount;
    }
}
