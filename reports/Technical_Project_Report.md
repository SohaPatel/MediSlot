# MEDISLOT
## Technical Project Report
### Advanced Algorithm – CIA 3

---

## 1. Introduction

MediSlot is a web-based healthcare appointment scheduling application designed to simplify patient registration, doctor discovery, appointment booking and appointment management.

The application uses Java and Spring Boot for backend processing and React.js for the frontend. It incorporates advanced algorithms to improve appointment allocation and patient email membership checking.

## 2. Problem Statement

Traditional appointment scheduling can result in scheduling conflicts, inefficient allocation of available slots and difficulties in managing patient appointments.

MediSlot addresses these challenges through algorithm-based appointment allocation, automated appointment management and preliminary email membership screening.

## 3. Objectives

- Simplify patient registration and login.
- Allow patients to discover doctors and book appointments.
- Prevent duplicate doctor-slot bookings.
- Support appointment history and cancellation.
- Improve appointment allocation using Bipartite Matching.
- Implement Bloom Filter for preliminary email membership checking.
- Evaluate algorithm performance against baseline approaches.

## 4. Technology Stack

| Component | Technology |
|---|---|
| Frontend | React.js, JavaScript, CSS |
| Backend | Java 17 |
| Framework | Spring Boot 3.4.5 |
| REST API | Spring Web |
| Input Validation | Jakarta Bean Validation |
| Password Hashing | BCrypt (Spring Security Crypto) |
| Algorithm Data Structures | BitSet, ConcurrentHashMap |
| Testing | JUnit 5, Spring Boot Test |
| Build Tool | Maven, npm, Vite |
| Version Control | Git and GitHub |

## 5. System Architecture

MediSlot follows a layered architecture consisting of the following components:

1. **Frontend Layer:** React.js provides patient registration, login, doctor discovery, appointment booking and appointment management interfaces.
2. **Controller Layer:** Spring Boot REST controllers receive HTTP requests, validate input and return responses.
3. **Service Layer:** Handles patient registration, password verification, appointment management and algorithm integration.
4. **Repository Layer:** Manages patient and appointment information using in-memory data structures.
5. **Algorithm Layer:** Contains Bipartite Matching and Bloom Filter implementations.

The frontend communicates with the backend through HTTP REST APIs.

## 6. Key Features

- Patient registration with input validation.
- Patient login with BCrypt password verification.
- Doctor listing and specialization-based discovery.
- Appointment booking.
- Patient appointment history.
- Appointment cancellation.
- Duplicate email checking.
- Algorithm-based appointment allocation.

## 7. Algorithms Used

### 7.1 Bipartite Matching

**Purpose:**

Allocate available appointment slots to appointment requests using maximum bipartite matching.

**Approach:**

- Appointment requests are represented as left-side vertices.
- Available appointment slots are represented as right-side vertices.
- Edges represent compatible request-slot combinations.
- DFS-based augmenting paths are used to find a maximum matching.
- Previously assigned slots can be reassigned when an augmenting path is found.

**Complexity Analysis:**

| Operation | Complexity |
|---|---|
| Time | O(V × E) |
| Space | O(V + E) |

Where V represents the total number of vertices and E represents the number of edges.

### 7.2 Bloom Filter

**Purpose:**

Provide preliminary membership checking for patient email registration.

**Approach:**

- Uses Java BitSet for compact bit storage.
- Multiple hash positions are calculated for each email.
- Insertion sets the corresponding bit positions.
- Lookup checks whether all required bit positions are set.
- Positive results are verified using an exact repository lookup.

**Complexity Analysis:**

| Operation | Complexity |
|---|---|
| Insertion | O(k) |
| Lookup | O(k) |
| Space | O(m) bits |

Where k represents the number of hash functions and m represents the number of bits.

A Bloom Filter can produce false positives but does not produce false negatives for successfully inserted elements under normal operation.

## 8. Testing

Both algorithm implementations were tested using JUnit.

| Algorithm | Test Cases | Passed | Failed |
|---|---:|---:|---:|
| Bipartite Matching | 8 | 8 | 0 |
| Bloom Filter | 8 | 8 | 0 |
| **Total** | **16** | **16** | **0** |

**Build verification:** `mvn clean test` completed successfully.

The frontend production build was also verified successfully using `npm run build`.

## 9. Performance Evaluation

### 9.1 Bipartite Matching vs Greedy Allocation

The Bipartite Matching algorithm was compared against a greedy allocation baseline at three input sizes.

| Input Size | Algorithm | Average Time (ms) | Matched |
|---:|---|---:|---:|
| 100 | Bipartite Matching | 0.1132 | 100 |
| 100 | Greedy | 0.0325 | 96 |
| 300 | Bipartite Matching | 0.3307 | 300 |
| 300 | Greedy | 0.1425 | 280 |
| 600 | Bipartite Matching | 1.1118 | 600 |
| 600 | Greedy | 0.2541 | 564 |

**Observation:**

Bipartite Matching achieved more successful assignments than the greedy baseline across all three tested input sizes. However, the greedy approach required less execution time.

This demonstrates the trade-off between allocation quality and execution speed.

### 9.2 Bloom Filter vs HashSet

The Bloom Filter implementation was evaluated against direct HashSet lookup.

| Input Size | Bloom + Exact Check (ms) | HashSet (ms) | False Positive Rate |
|---:|---:|---:|---:|
| 1,000 | 0.9013 | 0.2003 | 2.20% |
| 5,000 | 2.9528 | 0.3812 | 1.90% |
| 10,000 | 6.0818 | 0.4993 | 1.91% |

**Observation:**

The HashSet baseline was faster in these experiments. Bloom Filter screening followed by exact verification introduced additional processing overhead.

The Bloom Filter nevertheless demonstrates probabilistic membership screening and its ability to reduce unnecessary exact lookups when the workload and storage requirements justify its use.

*Note: Benchmark timings are local experimental measurements and may vary depending on hardware and execution conditions.*

## 10. Authentication and Security

MediSlot implements basic patient authentication.

- Passwords are hashed using BCrypt before storage.
- Login verifies submitted passwords against stored hashes.
- Duplicate patient emails are checked before registration.
- Input validation is performed using Jakarta Bean Validation.
- Password hashes are excluded from patient JSON responses.

**Current security limitations:**

- The application does not yet implement JWT-based authentication or secure server-side sessions.
- Backend authorization and protected API access require further development.
- Patient and appointment information is currently stored in memory rather than a persistent database.

## 11. Limitations

- Bloom Filter may produce false positives.
- Bloom Filter storage is currently in-memory.
- Patient and appointment records are not persistent across backend restarts.
- The application uses a simplified appointment scheduling dataset.
- Secure session management and backend authorization require further development.
- Performance measurements depend on the testing environment.

## 12. Individual Contributions

### Soha Patel

- Patient registration and frontend functionality.
- Patient appointment history.
- Appointment cancellation frontend.
- Bloom Filter implementation and integration.
- Algorithm complexity and performance documentation.
- Patient authentication frontend and backend implementation.

### Tanishka Shukla

- Bipartite Matching algorithm implementation.
- Appointment cancellation backend.
- Algorithm unit testing and performance benchmarking.
- Bloom Filter unit testing and benchmarking.
- Technical project documentation.

*Individual contributions should be verified against the actual Git commit history before submission.*

## 13. Conclusion

MediSlot demonstrates the practical application of advanced algorithms in healthcare appointment scheduling.

Bipartite Matching provides maximum matching between appointment requests and available slots, while Bloom Filter provides probabilistic email membership screening.

The project combines algorithmic concepts with a functioning full-stack web application, demonstrating the relationship between theoretical algorithm analysis and practical software development.

## 14. Future Enhancements

- Persistent database integration.
- JWT-based authentication and backend authorization.
- Appointment rescheduling.
- Email and SMS notifications.
- Improved doctor availability management.
- Cloud deployment.
- Performance optimization for larger datasets.

---

**MediSlot – Smart Healthcare Appointment Scheduling System**

Advanced Algorithm Project | CIA 3