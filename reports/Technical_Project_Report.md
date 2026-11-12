

\# MEDISLOT

\## Technical Project Report

\### Advanced Algorithm – CIA 3



\## 1. Introduction



MediSlot is a healthcare appointment scheduling

application that allows patients to register,

search for doctors, book appointments and manage

their appointment history.



The application uses Java and Spring Boot for

backend processing and React for the frontend.



\## 2. Problem Statement



Traditional appointment scheduling can result

in scheduling conflicts, inefficient allocation

and difficulty managing patient appointments.



MediSlot addresses these problems through

automated appointment scheduling and efficient

patient email membership checking.



\## 3. Objectives



\- Simplify patient registration.

\- Allow patients to book appointments.

\- Prevent duplicate doctor-slot bookings.

\- Support appointment cancellation.

\- Improve appointment allocation using

&#x20; Bipartite Matching.

\- Use Bloom Filter for preliminary email

&#x20; membership checking.



\## 4. Technology Stack



| Component | Technology |

|---|---|

| Frontend | React.js |

| Backend | Java 17 |

| Framework | Spring Boot 3.4.5 |

| Build Tool | Maven |

| Testing | JUnit 5 |

| Version Control | Git and GitHub |



\## 5. System Architecture



The application follows a three-layer architecture.



1\. Frontend: Collects user input and displays results.

2\. REST API: Processes requests and validates input.

3\. Algorithm and service layer: Executes application

&#x20;  logic and algorithm implementations.



Frontend communicates with the backend using HTTP

REST APIs.



\## 6. Algorithms Used



\### 6.1 Bipartite Matching



Purpose:

Allocate available appointment slots to doctors

using maximum bipartite matching.



Approach:

\- Doctors are represented as left-side vertices.

\- Appointment slots are represented as right-side

&#x20; vertices.

\- Edges indicate available doctor-slot combinations.

\- DFS-based augmenting paths find a maximum matching.



Time Complexity:

O(V × E)



Space Complexity:

O(V + E)



\### 6.2 Bloom Filter



Purpose:

Provide preliminary membership checking for

patient email registration.



Approach:

\- Uses a BitSet.

\- Multiple hash positions are calculated.

\- Insertion sets corresponding bits.

\- Lookup checks whether all required bits are set.



Time Complexity:

Insertion: O(k)

Lookup: O(k)



Space Complexity:

O(m)



Where k is the number of hash functions and m is

the number of bits.



A positive result means possibly present.

An exact repository check is still necessary.



\## 7. Testing



Bipartite Matching:

\- 8 JUnit test cases.

\- All 8 passed.



Bloom Filter:

\- 8 JUnit test cases.

\- All 8 passed.



Total:

16 tests passed, 0 failures, 0 errors.



\## 8. Performance Evaluation



\### 8.1 Bipartite Matching



Input sizes:

100, 300 and 600.



The algorithm was compared against a greedy

allocation baseline.



Insert the actual benchmark results from

BipartiteMatchingBenchmark.java.



\### 8.2 Bloom Filter



| Input Size | Bloom + Exact Check (ms) | HashSet (ms) | False Positive |

|---:|---:|---:|---:|

| 1000 | 0.9013 | 0.2003 | 2.20% |

| 5000 | 2.9528 | 0.3812 | 1.90% |

| 10000 | 6.0818 | 0.4993 | 1.91% |



Observation:

The HashSet baseline was faster in this experiment.

The Bloom Filter provides probabilistic membership

screening, but its usefulness depends on the cost

of the authoritative lookup and workload.



\## 9. Limitations



\- Bloom Filter may produce false positives.

\- The current Bloom Filter is in-memory.

\- Performance depends on input distribution.

\- The application uses a simplified appointment

&#x20; scheduling dataset.

\- Persistent database storage and authentication

&#x20; require further development.



\## 10. Individual Contributions



Soha Patel:

\- Patient registration frontend.

\- Patient appointment history.

\- Bloom Filter implementation and integration.

\- Algorithm complexity documentation.

\- Appointment cancellation frontend.



Tanishka Shukla:

\- Bipartite Matching algorithm.

\- Appointment cancellation backend.

\- Unit testing and performance benchmarking.

\- Bloom Filter benchmarking and unit tests.



Verify individual contributions against the

actual Git commit history before submission.



\## 11. Conclusion



MediSlot demonstrates the practical application

of advanced algorithms in healthcare scheduling.



Bipartite Matching supports appointment allocation,

while Bloom Filter provides probabilistic email

membership screening.



The project combines algorithmic concepts with

a functioning web application.



