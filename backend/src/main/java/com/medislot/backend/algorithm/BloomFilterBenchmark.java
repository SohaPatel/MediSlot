
package com.medislot.backend.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BloomFilterBenchmark {

    private static final int[] SIZES = {1000, 5000, 10000};
    private static final int HASH_COUNT = 7;
    private static final int WARMUP_RUNS = 5;
    private static final int MEASURE_RUNS = 20;

    public static void main(String[] args) {

        System.out.println("====================================================");
        System.out.println(" MEDISLOT BLOOM FILTER PERFORMANCE BENCHMARK");
        System.out.println("====================================================");

        System.out.printf(
                "%-12s %-23s %-20s%n",
                "Input Size", "Lookup Method", "Average Time (ms)"
        );

        for (int size : SIZES) {
            runBenchmark(size);
        }

        System.out.println("Benchmark completed.");
    }

    private static void runBenchmark(int size) {

        // Generate deterministic test data
        Set<String> registeredEmails = new HashSet<>();

        for (int i = 0; i < size; i++) {
            registeredEmails.add("patient" + i + "@medislot.com");
        }

        // Configure Bloom Filter based on input size
        int filterSize = Math.max(1024, size * 10);

        BloomFilter bloomFilter =
                new BloomFilter(filterSize, HASH_COUNT);

        // Insert all registered emails
        for (String email : registeredEmails) {
            bloomFilter.add(email);
        }

        // Generate existing and non-existing email queries
        List<String> queries = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            queries.add("patient" + i + "@medislot.com");
            queries.add("unknown" + i + "@medislot.com");
        }

        // Shuffle queries reproducibly
        java.util.Collections.shuffle(queries, new Random(42));

        // Warm-up both methods
        for (int i = 0; i < WARMUP_RUNS; i++) {
            bloomLookup(queries, bloomFilter, registeredEmails);
            exactLookup(queries, registeredEmails);
        }

        long bloomTotalTime = 0;
        long exactTotalTime = 0;

        // Benchmark Bloom Filter with exact verification
        for (int i = 0; i < MEASURE_RUNS; i++) {

            long start = System.nanoTime();

            bloomLookup(
                    queries,
                    bloomFilter,
                    registeredEmails
            );

            long end = System.nanoTime();

            bloomTotalTime += end - start;
        }

        // Benchmark exact HashSet lookup
        for (int i = 0; i < MEASURE_RUNS; i++) {

            long start = System.nanoTime();

            exactLookup(queries, registeredEmails);

            long end = System.nanoTime();

            exactTotalTime += end - start;
        }

        double bloomAverage =
                bloomTotalTime / (double) MEASURE_RUNS / 1_000_000;

        double exactAverage =
                exactTotalTime / (double) MEASURE_RUNS / 1_000_000;

        // Measure false positives separately
        int falsePositives = 0;
        int absentQueries = 0;

        for (String email : queries) {

            if (!registeredEmails.contains(email)) {

                absentQueries++;

                if (bloomFilter.mightContain(email)) {
                    falsePositives++;
                }
            }
        }

        double falsePositiveRate =
                absentQueries == 0 ? 0 :
                (falsePositives * 100.0) / absentQueries;

        System.out.printf(
                "%-12d %-23s %-20.4f%n",
                size,
                "Bloom + exact check",
                bloomAverage
        );

        System.out.printf(
                "%-12d %-23s %-20.4f%n",
                size,
                "Exact HashSet",
                exactAverage
        );

        System.out.println(
                "False positives: " + falsePositives
                + " / " + absentQueries
        );

        System.out.printf(
                "False positive rate: %.4f%%%n",
                falsePositiveRate
        );

        System.out.println("----------------------------------------------------");
    }

    // Bloom Filter followed by authoritative exact verification
    private static int bloomLookup(
            List<String> queries,
            BloomFilter bloomFilter,
            Set<String> registeredEmails) {

        int found = 0;

        for (String email : queries) {

            if (bloomFilter.mightContain(email)) {

                if (registeredEmails.contains(email)) {
                    found++;
                }
            }
        }

        return found;
    }

    // Baseline: direct exact membership lookup
    private static int exactLookup(
            List<String> queries,
            Set<String> registeredEmails) {

        int found = 0;

        for (String email : queries) {

            if (registeredEmails.contains(email)) {
                found++;
            }
        }

        return found;
    }
}
