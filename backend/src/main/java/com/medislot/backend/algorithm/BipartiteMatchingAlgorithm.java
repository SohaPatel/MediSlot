
package com.medislot.backend.algorithm;

import java.util.Arrays;
import java.util.List;

public class BipartiteMatchingAlgorithm {

    /**
     * Finds a maximum matching between left-side requests
     * and right-side available appointment slots.
     *
     * Returns an array where:
     * index = request number
     * value = assigned slot number
     * -1 = no slot assigned
     */
    public int[] maximumMatching(
            int leftCount,
            int rightCount,
            List<List<Integer>> adjacency) {

        if (leftCount < 0 || rightCount < 0) {
            throw new IllegalArgumentException(
                    "Vertex counts cannot be negative."
            );
        }

        if (adjacency == null || adjacency.size() != leftCount) {
            throw new IllegalArgumentException(
                    "Adjacency list must contain one list per request."
            );
        }

        int[] rightToLeft = new int[rightCount];
        Arrays.fill(rightToLeft, -1);

        for (int left = 0; left < leftCount; left++) {

            if (adjacency.get(left) == null) {
                throw new IllegalArgumentException(
                        "Request adjacency list cannot be null."
                );
            }

            boolean[] visitedRight = new boolean[rightCount];

            tryAugment(
                    left,
                    adjacency,
                    visitedRight,
                    rightToLeft,
                    rightCount
            );
        }

        int[] leftToRight = new int[leftCount];
        Arrays.fill(leftToRight, -1);

        for (int right = 0; right < rightCount; right++) {
            if (rightToLeft[right] != -1) {
                leftToRight[rightToLeft[right]] = right;
            }
        }

        return leftToRight;
    }

    private boolean tryAugment(
            int left,
            List<List<Integer>> adjacency,
            boolean[] visitedRight,
            int[] rightToLeft,
            int rightCount) {

        for (int right : adjacency.get(left)) {

            if (right < 0 || right >= rightCount) {
                throw new IllegalArgumentException(
                        "Invalid appointment slot index: " + right
                );
            }

            if (visitedRight[right]) {
                continue;
            }

            visitedRight[right] = true;

            if (rightToLeft[right] == -1 ||
                    tryAugment(
                            rightToLeft[right],
                            adjacency,
                            visitedRight,
                            rightToLeft,
                            rightCount
                    )) {

                rightToLeft[right] = left;
                return true;
            }
        }

        return false;
    }
}
