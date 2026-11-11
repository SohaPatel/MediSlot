
package com.medislot.backend.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BipartiteMatchingBenchmark {

    private static final BipartiteMatchingAlgorithm algorithm =
            new BipartiteMatchingAlgorithm();

    private static final int[] SIZES = {100, 300, 600};
    private static final int EDGES_PER_REQUEST = 10;
    private static final int WARMUP_RUNS = 5;
    private static final int MEASURE_RUNS = 20;

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println(" MEDISLOT BIPARTITE MATCHING BENCHMARK");
        System.out.println("==============================================");

        System.out.printf(
                "%-12s %-20s %-20s %-15s%n",
                "Size", "Algorithm", "Average Time (ms)", "Matched"
        );

        for (int size : SIZES) {

            List<List<Integer>> graph = generateGraph(size);

            // Warm-up
            for (int i = 0; i < WARMUP_RUNS; i++) {
                algorithm.maximumMatching(size, size, graph);
                greedyMatching(size, graph);
            }

            long matchingTime = 0;
            long greedyTime = 0;

            int matchingCount = 0;
            int greedyCount = 0;

            // Measure maximum matching
            for (int i = 0; i < MEASURE_RUNS; i++) {

                long start = System.nanoTime();

                int[] result = algorithm.maximumMatching(
                        size, size, graph
                );

                long end = System.nanoTime();

                matchingTime += end - start;
                matchingCount = countMatched(result);
            }

            // Measure greedy baseline
            for (int i = 0; i < MEASURE_RUNS; i++) {

                long start = System.nanoTime();

                int[] result = greedyMatching(size, graph);

                long end = System.nanoTime();

                greedyTime += end - start;
                greedyCount = countMatched(result);
            }

            double averageMatching =
                    matchingTime / (double) MEASURE_RUNS / 1_000_000;

            double averageGreedy =
                    greedyTime / (double) MEASURE_RUNS / 1_000_000;

            System.out.printf(
                    "%-12d %-20s %-20.4f %-15d%n",
                    size, "Bipartite Matching",
                    averageMatching, matchingCount
            );

            System.out.printf(
                    "%-12d %-20s %-20.4f %-15d%n",
                    size, "Greedy",
                    averageGreedy, greedyCount
            );

            System.out.println("----------------------------------------------");
        }

        System.out.println("Benchmark completed.");
    }

    // Generate reproducible synthetic appointment graphs
    private static List<List<Integer>> generateGraph(int size) {

        List<List<Integer>> adjacency = new ArrayList<>();

        Random random = new Random(42);

        for (int left = 0; left < size; left++) {

            List<Integer> slots = new ArrayList<>();

            for (int right = 0; right < size; right++) {
                slots.add(right);
            }

            Collections.shuffle(slots, random);

            List<Integer> selectedSlots = new ArrayList<>(
                    slots.subList(0, EDGES_PER_REQUEST)
            );

            adjacency.add(selectedSlots);
        }

        return adjacency;
    }

    // Greedy baseline: never reassigns an occupied slot
    private static int[] greedyMatching(
            int leftCount,
            List<List<Integer>> adjacency) {

        int[] result = new int[leftCount];

        Arrays.fill(result, -1);

        boolean[] occupied = new boolean[leftCount];

        for (int left = 0; left < leftCount; left++) {

            for (int right : adjacency.get(left)) {

                if (!occupied[right]) {

                    occupied[right] = true;
                    result[left] = right;
                    break;
                }
            }
        }

        return result;
    }

    private static int countMatched(int[] result) {

        int count = 0;

        for (int slot : result) {
            if (slot != -1) {
                count++;
            }
        }

        return count;
    }
}
