# MediSlot – Smart Healthcare Appointment Scheduling System

## 1. Introduction

Healthcare facilities often face difficulties managing doctor appointments efficiently. Manual appointment scheduling can result in overlapping bookings, inefficient use of available time slots, long waiting times, and difficulty managing patient appointments.

MediSlot is a smart healthcare appointment scheduling system designed to simplify the appointment booking process. It allows patients to register, log in, view available doctors, book appointments, view appointment history, and cancel appointments.

The system uses algorithmic techniques to improve appointment allocation and patient email validation. It combines a React.js frontend with a Java Spring Boot backend.

## 2. Problem Statement

Traditional appointment scheduling methods may not efficiently allocate available doctor time slots. When multiple patients request appointments, a suitable allocation method is needed to avoid conflicts and maximize the number of appointments that can be scheduled.

Additionally, checking whether a patient's email may already exist can involve repeated searches through stored patient records.

MediSlot addresses these problems using algorithmic techniques for appointment allocation and preliminary email membership checking.

## 3. Objectives

The main objectives of MediSlot are:

1. Develop a user-friendly healthcare appointment scheduling application.
2. Allow patients to register and log in.
3. Provide doctor and appointment information to patients.
4. Implement Bipartite Matching to allocate appointment requests to available slots.
5. Implement a Bloom Filter for preliminary patient email membership checking.
6. Allow patients to view their appointment history and cancel appointments.
7. Evaluate algorithm performance using different input sizes and baseline comparisons.

## 4. Proposed Algorithms

### 4.1 Bipartite Matching Algorithm

Bipartite Matching is used for appointment-slot allocation.

The problem is represented as a bipartite graph:
- One set of vertices represents appointment requests.
- The other set represents available appointment slots.
- An edge connects a request to a slot when that slot is suitable for the request.

The system uses a DFS-based augmenting-path approach to find a maximum matching. This helps allocate the maximum possible number of compatible appointment requests to available slots.

**Time Complexity:** O(V × E)

**Space Complexity:** O(V + E)

A greedy allocation approach is used as a baseline for performance comparison.

### 4.2 Bloom Filter

A Bloom Filter is a probabilistic data structure used for preliminary email membership checking during patient registration.

MediSlot uses:
- A BitSet with 10,000 bits.
- Five hash functions.

The Bloom Filter checks whether an email is possibly present in the existing patient records.

- If the filter reports that an email is definitely absent, registration can proceed to the normal save operation.
- If the filter reports that an email may be present, the system performs an exact repository lookup before rejecting registration.

This exact check prevents false positives from incorrectly rejecting a new patient.

**Time Complexity:** O(k) per insertion or lookup, where k is the number of hash functions.

**Space Complexity:** O(m) bits, where m is the filter size.

A HashSet-based membership check is used as a baseline for comparison.

## 5. Technology Stack

| Component | Technology |
|---|---|
| Frontend | React.js, JavaScript, CSS |
| Backend | Java 17, Spring Boot |
| Build Tool | Maven |
| API Communication | REST APIs |
| Version Control | Git and GitHub |
| Testing | JUnit and Maven |

The current application uses in-memory data structures for storing patient and appointment information during execution.

## 6. Expected Outcomes

The expected outcomes of MediSlot are:

1. A working healthcare appointment scheduling application.
2. Successful patient registration and login.
3. Efficient allocation of compatible appointment requests using Bipartite Matching.
4. Preliminary email membership checking using a Bloom Filter.
5. Appointment history and cancellation functionality.
6. Correctness testing of both algorithms.
7. Performance comparisons against Greedy Matching and HashSet-based membership checking.

The project will demonstrate how advanced algorithms and probabilistic data structures can be applied to a real-world healthcare scheduling problem.

## 7. Conclusion

MediSlot demonstrates the practical application of advanced algorithms in healthcare appointment scheduling. Bipartite Matching helps maximize compatible appointment allocations, while the Bloom Filter provides preliminary email membership checking during registration.

By integrating these algorithms into a Java Spring Boot backend and React.js frontend, the project provides a working demonstration of algorithmic problem-solving in a real-world application.

## 8. Team Members

- Soha Patel
- Tanishka Shukla
