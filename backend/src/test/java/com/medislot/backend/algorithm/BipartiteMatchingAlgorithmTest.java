
package com.medislot.backend.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BipartiteMatchingAlgorithmTest {

    private final BipartiteMatchingAlgorithm algorithm =
            new BipartiteMatchingAlgorithm();

    // Test 1: Every request gets a separate slot
    @Test
    void testPerfectMatching() {

        List<List<Integer>> adjacency = List.of(
                List.of(0),
                List.of(1)
        );

        int[] result = algorithm.maximumMatching(
                2, 2, adjacency
        );

        assertArrayEquals(new int[]{0, 1}, result);
    }

    // Test 2: Algorithm must reassign an occupied slot
    @Test
    void testAugmentingPath() {

        List<List<Integer>> adjacency = List.of(
                List.of(0, 1),
                List.of(0)
        );

        int[] result = algorithm.maximumMatching(
                2, 2, adjacency
        );

        assertArrayEquals(new int[]{1, 0}, result);
    }

    // Test 3: No available appointment slots
    @Test
    void testNoAvailableSlots() {

        List<List<Integer>> adjacency = List.of(
                List.of(),
                List.of()
        );

        int[] result = algorithm.maximumMatching(
                2, 0, adjacency
        );

        assertArrayEquals(new int[]{-1, -1}, result);
    }

    // Test 4: Empty graph
    @Test
    void testEmptyGraph() {

        List<List<Integer>> adjacency = List.of();

        int[] result = algorithm.maximumMatching(
                0, 0, adjacency
        );

        assertArrayEquals(new int[]{}, result);
    }

    // Test 5: More requests than available slots
    @Test
    void testMoreRequestsThanSlots() {

        List<List<Integer>> adjacency = List.of(
                List.of(0),
                List.of(0),
                List.of(1)
        );

        int[] result = algorithm.maximumMatching(
                3, 2, adjacency
        );

        assertArrayEquals(new int[]{0, -1, 1}, result);
    }

    // Test 6: Negative vertex count
    @Test
    void testNegativeVertexCount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> algorithm.maximumMatching(
                        -1, 2, List.of()
                )
        );
    }

    // Test 7: Invalid adjacency size
    @Test
    void testInvalidAdjacencySize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> algorithm.maximumMatching(
                        2, 2, List.of(List.of(0))
                )
        );
    }

    // Test 8: Invalid appointment slot index
    @Test
    void testInvalidSlotIndex() {

        List<List<Integer>> adjacency = List.of(
                List.of(5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> algorithm.maximumMatching(
                        1, 2, adjacency
                )
        );
    }
}
