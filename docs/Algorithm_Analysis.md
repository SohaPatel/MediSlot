
# MediSlot – Algorithm Analysis

## 1. Introduction

MediSlot uses two important algorithms to improve
the efficiency of patient appointment scheduling
and patient email validation.

1. Bipartite Matching Algorithm
2. Bloom Filter Algorithm

---

## 2. Bipartite Matching Algorithm

### Purpose

The Bipartite Matching Algorithm assigns available
appointment slots to doctors while avoiding
conflicting bookings.

### Working

- Doctors are represented as left-side vertices.
- Available appointment slots are represented as
  right-side vertices.
- Edges represent available doctor-slot combinations.
- The algorithm uses DFS-based augmenting paths
  to find a maximum matching.

### Time Complexity

Using the DFS-based augmenting path approach:

O(V * E)

Where:
- V = Number of vertices.
- E = Number of edges.

### Space Complexity

O(V + E)

### Advantages

- Finds maximum possible matching.
- Avoids unnecessary slot conflicts.
- Provides better allocation than a simple greedy
  approach in cases where reassignment is required.

---

## 3. Bloom Filter Algorithm

### Purpose

The Bloom Filter helps quickly check whether a
patient email might already exist in the system.

### Working

- Uses a BitSet to store hashed positions.
- Multiple hash functions generate bit positions.
- Adding an email sets the corresponding bits.
- Checking an email verifies whether all required
  bits are set.

### Time Complexity

- Insertion: O(k)
- Lookup: O(k)

Where k is the number of hash functions.

### Space Complexity

O(m)

Where m is the number of bits in the filter.

### Advantages

- Space-efficient probabilistic membership checking.
- Fast lookup operations.
- Allows false positives but no false negatives
  for values previously inserted, assuming no
  deletion or filter corruption.

### Limitation

A positive Bloom Filter result does not guarantee
that an email exists. An exact database or repository
check is still required.

---

## 4. Performance Evaluation

MediSlot includes separate benchmark programs
for both algorithms.

### Bipartite Matching Benchmark

Tested input sizes:
- 100
- 300
- 600

The benchmark compares maximum matching against
a greedy allocation approach.

### Bloom Filter Benchmark

Tested input sizes:
- 1,000
- 5,000
- 10,000

The benchmark measures lookup performance and
false-positive rates.

### Important Observation

The benchmark results are specific to the test
environment and should not be treated as universal
performance guarantees.

---

## 5. Conclusion

The Bipartite Matching Algorithm improves
appointment slot allocation by finding maximum
possible matches.

The Bloom Filter provides a memory-efficient
preliminary check for duplicate patient emails.

Together, these algorithms demonstrate the
application of advanced data structures and
algorithms in a healthcare appointment scheduling
system.
