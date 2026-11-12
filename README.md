\# MediSlot

\### Smart Healthcare Appointment Scheduling System



A web-based application that simplifies doctor discovery, appointment booking, and patient appointment management using modern web technologies and advanced algorithms.



\---



\## Technology Stack



<table>

<tr>

<td align="center" width="50%">



\### Frontend



<img src="https://img.shields.io/badge/React-2026-61DAFB?style=for-the-badge\&logo=react\&logoColor=black" />



<img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge\&logo=vite\&logoColor=white" />



</td>

<td align="center" width="50%">



\### Backend



<img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white" />



<img src="https://img.shields.io/badge/Spring\_Boot-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white" />



</td>

</tr>

<tr>

<td align="center">



\### Testing and Build



<img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge\&logo=junit5\&logoColor=white" />



<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white" />



</td>

<td align="center">



\### Version Control



<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge\&logo=git\&logoColor=white" />



<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge\&logo=github\&logoColor=white" />



</td>

</tr>

</table>



\---



\## Project Overview



MediSlot provides a centralized platform where patients can register, explore doctors, book appointments, view appointment history, and cancel confirmed appointments.



The project also demonstrates the application of advanced algorithms to appointment allocation and preliminary email membership screening.



\## Key Features



\- Patient registration and management.

\- Doctor listing by specialization.

\- Appointment booking.

\- Patient appointment history.

\- Appointment cancellation.

\- Algorithm-based appointment allocation.

\- Bloom Filter-based email membership screening.



\## System Architecture



```mermaid

flowchart TD

&#x20;   A\[Patient] --> B\[React Frontend]

&#x20;   B -->|REST API Requests| C\[Spring Boot Backend]



&#x20;   C --> D\[Patient Management]

&#x20;   C --> E\[Appointment Management]



&#x20;   E --> F\[Bipartite Matching]

&#x20;   D --> G\[Bloom Filter]



&#x20;   D --> H\[API Response]

&#x20;   E --> H

&#x20;   H --> B

&#x20;   B --> I\[Patient Interface]

```



\## Advanced Algorithms



\### 1. Bipartite Matching



Uses a DFS-based augmenting path approach to find maximum matching between appointment requests and available slots.



\- Time Complexity: `O(V × E)`

\- Space Complexity: `O(V + E)`



\### 2. Bloom Filter



A probabilistic data structure used for preliminary email membership screening. Positive results are verified through an exact repository lookup because Bloom Filters can produce false positives.



\- Insertion: `O(k)`

\- Lookup: `O(k)`

\- Space: `O(m)` bits



\## Project Structure



```text

MediSlot/

├── backend/

│   ├── src/

│   │   ├── main/

│   │   └── test/

│   └── pom.xml

│

├── frontend/

│   ├── src/

│   └── package.json

│

├── docs/

│   └── Algorithm\_Analysis.md

│

├── reports/

│   ├── Project\_Proposal.md

│   ├── Technical\_Project\_Report.md

│   └── screenshots/

│

└── README.md

```



\## Installation and Setup



\*\*Prerequisites:\*\* Java 17+, Maven, Node.js, npm, and Git.



Clone the repository:



```bash

git clone https://github.com/SohaPatel/MediSlot.git

cd MediSlot

```



Install backend dependencies:



```bash

cd backend

mvn clean install

```



Install frontend dependencies in a separate terminal:



```bash

cd frontend

npm install

```



\## Running the Application



\*\*Backend:\*\*



```bash

cd backend

mvn spring-boot:run

```



Backend: `http://localhost:8081`



\*\*Frontend:\*\*



```bash

cd frontend

npm run dev

```



Frontend: `http://localhost:5173`



\## API Endpoints



| Method | Endpoint | Description |

|---|---|---|

| POST | `/api/patients/register` | Register patient |

| GET | `/api/patients` | Retrieve patients |

| GET | `/api/appointments` | Retrieve appointments |

| GET | `/api/appointments/doctors` | Retrieve doctors |

| POST | `/api/appointments/book` | Book appointment |

| GET | `/api/appointments/patient/{patientId}` | Patient appointment history |

| PATCH | `/api/appointments/{id}/cancel` | Cancel appointment |



\## Testing



Run the backend test suite:



```bash

cd backend

mvn clean test

```



Recorded test results:



| Test Suite | Passed |

|---|---:|

| Bipartite Matching | 8 |

| Bloom Filter | 8 |

| \*\*Total\*\* | \*\*16\*\* |



\## Performance Analysis



\### Bipartite Matching vs Greedy



| Input Size | Algorithm | Time (ms) | Matched |

|---:|---|---:|---:|

| 100 | Bipartite Matching | 0.1132 | 100 |

| 100 | Greedy | 0.0325 | 96 |

| 300 | Bipartite Matching | 0.3307 | 300 |

| 300 | Greedy | 0.1425 | 280 |

| 600 | Bipartite Matching | 1.1118 | 600 |

| 600 | Greedy | 0.2541 | 564 |



\### Bloom Filter vs HashSet



| Input Size | Method | Time (ms) |

|---:|---|---:|

| 1,000 | Bloom + exact check | 0.9013 |

| 1,000 | Exact HashSet | 0.2003 |

| 5,000 | Bloom + exact check | 2.9528 |

| 5,000 | Exact HashSet | 0.3812 |

| 10,000 | Bloom + exact check | 6.0818 |

| 10,000 | Exact HashSet | 0.4993 |



These are local experimental results. Bipartite Matching achieved more matches in the tested cases, while Greedy was faster. Direct HashSet lookup was faster than Bloom Filter screening followed by an exact check in this benchmark.



\## Future Enhancements



\- Persistent database integration.

\- Secure authentication and authorization.

\- Appointment rescheduling.

\- Email and SMS notifications.

\- Improved doctor availability management.

\- Cloud deployment.



\## Team Contributions



\*\*Soha Patel\*\*

\- Patient-facing frontend functionality.

\- Appointment history and cancellation interface.

\- Bloom Filter implementation and integration.

\- Algorithm analysis documentation.



\*\*Tanishka Shukla\*\*

\- Bipartite Matching algorithm work.

\- Algorithm testing and benchmarking.

\- Bloom Filter unit testing and benchmarking.

\- Technical project documentation.



\## Documentation



\- \[Project Proposal](reports/Project\_Proposal.md)

\- \[Technical Project Report](reports/Technical\_Project\_Report.md)

\- \[Algorithm Analysis](docs/Algorithm\_Analysis.md)

\- \[Screenshot Evidence](reports/screenshots/README.md)



\---



<div align="center">



\*\*MediSlot — Smart Healthcare Appointment Scheduling System\*\*



Advanced Algorithm Project



</div>

