\# MediSlot



\### Smart Healthcare Appointment Scheduling System



MediSlot is a web-based healthcare appointment scheduling platform that enables patients to discover doctors, book appointments, manage appointment history, and cancel appointments. The project demonstrates the practical application of advanced algorithms in healthcare appointment management.



\---



\## Technology Stack



<table>

&#x20; <tr>

&#x20;   <td align="center" width="50%">

&#x20;     <h3>Frontend</h3>

&#x20;     <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge\&logo=react\&logoColor=black" alt="React"/>

&#x20;     <br/><br/>

&#x20;     <img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge\&logo=vite\&logoColor=white" alt="Vite"/>

&#x20;   </td>

&#x20;   <td align="center" width="50%">

&#x20;     <h3>Backend</h3>

&#x20;     <img src="https://img.shields.io/badge/Java\_17-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white" alt="Java 17"/>

&#x20;     <br/><br/>

&#x20;     <img src="https://img.shields.io/badge/Spring\_Boot-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white" alt="Spring Boot"/>

&#x20;   </td>

&#x20; </tr>

&#x20; <tr>

&#x20;   <td align="center">

&#x20;     <h3>Testing and Build</h3>

&#x20;     <img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge\&logo=junit5\&logoColor=white" alt="JUnit"/>

&#x20;     <br/><br/>

&#x20;     <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white" alt="Maven"/>

&#x20;   </td>

&#x20;   <td align="center">

&#x20;     <h3>Version Control</h3>

&#x20;     <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge\&logo=git\&logoColor=white" alt="Git"/>

&#x20;     <br/><br/>

&#x20;     <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge\&logo=github\&logoColor=white" alt="GitHub"/>

&#x20;   </td>

&#x20; </tr>

</table>



\---



\## Project Overview



The application provides a centralized platform for patients to:



\- Register and manage patient information.

\- Browse doctors by specialization.

\- Book appointments.

\- View appointment history.

\- Cancel confirmed appointments.



It also incorporates algorithmic techniques for appointment allocation and preliminary email membership screening.



\## Key Algorithms



\### 1. Bipartite Matching



Uses a DFS-based augmenting path approach to find maximum matching between appointment requests and available slots.



\- \*\*Time Complexity:\*\* O(V × E)

\- \*\*Space Complexity:\*\* O(V + E)



\### 2. Bloom Filter



A probabilistic data structure used for preliminary email membership screening. Positive results are checked against the exact repository because Bloom Filters can produce false positives.



\- \*\*Insertion:\*\* O(k)

\- \*\*Lookup:\*\* O(k)

\- \*\*Space:\*\* O(m) bits



\## System Architecture



```mermaid

flowchart TB

&#x20;   A\["Patient"] --> B\["React Frontend"]

&#x20;   B <-->|"REST API"| C\["Spring Boot Backend"]

&#x20;   C --> D\["Patient Management"]

&#x20;   C --> E\["Appointment Management"]

&#x20;   D --> F\["Bloom Filter"]

&#x20;   E --> G\["Bipartite Matching"]

&#x20;   D --> H\["API Response"]

&#x20;   E --> H

&#x20;   H --> B



&#x20;   style A fill:#e0f2fe,stroke:#0284c7,color:#0f172a

&#x20;   style B fill:#dbeafe,stroke:#2563eb,color:#0f172a

&#x20;   style C fill:#dcfce7,stroke:#16a34a,color:#0f172a

&#x20;   style D fill:#fef3c7,stroke:#d97706,color:#0f172a

&#x20;   style E fill:#fef3c7,stroke:#d97706,color:#0f172a

&#x20;   style F fill:#f3e8ff,stroke:#9333ea,color:#0f172a

&#x20;   style G fill:#f3e8ff,stroke:#9333ea,color:#0f172a

&#x20;   style H fill:#e0e7ff,stroke:#6366f1,color:#0f172a

```



\## Project Structure



```text

MediSlot/

│

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



\### Prerequisites



\- Java 17 or later

\- Maven

\- Node.js and npm

\- Git



\### Clone the Repository



```bash

git clone https://github.com/SohaPatel/MediSlot.git

cd MediSlot

```



\### Backend Setup



```bash

cd backend

mvn clean install

```



\### Frontend Setup



Open a separate terminal:



```bash

cd frontend

npm install

```



\## Running the Application



Run the backend:



```bash

cd backend

mvn spring-boot:run

```



Backend URL: `http://localhost:8081`



Run the frontend in a second terminal:



```bash

cd frontend

npm run dev

```



Frontend URL: `http://localhost:5173`



\## API Endpoints



| Method | Endpoint | Description |

|---|---|---|

| POST | `/api/patients/register` | Register a patient |

| GET | `/api/patients` | Retrieve patients |

| GET | `/api/appointments` | Retrieve appointments |

| GET | `/api/appointments/doctors` | Retrieve doctors |

| POST | `/api/appointments/book` | Book an appointment |

| GET | `/api/appointments/patient/{patientId}` | Retrieve patient appointments |

| PATCH | `/api/appointments/{id}/cancel` | Cancel an appointment |



\## Testing



Execute the backend test suite:



```bash

cd backend

mvn clean test

```



Recorded test results:



| Test Suite | Tests Passed |

|---|---:|

| Bipartite Matching | 8 |

| Bloom Filter | 8 |

| \*\*Total\*\* | \*\*16\*\* |



\## Performance Analysis



\### Bipartite Matching vs Greedy



| Input Size | Algorithm | Average Time (ms) | Matched |

|---:|---|---:|---:|

| 100 | Bipartite Matching | 0.1132 | 100 |

| 100 | Greedy | 0.0325 | 96 |

| 300 | Bipartite Matching | 0.3307 | 300 |

| 300 | Greedy | 0.1425 | 280 |

| 600 | Bipartite Matching | 1.1118 | 600 |

| 600 | Greedy | 0.2541 | 564 |



\### Bloom Filter vs HashSet



| Input Size | Lookup Method | Average Time (ms) |

|---:|---|---:|

| 1,000 | Bloom + exact check | 0.9013 |

| 1,000 | Exact HashSet | 0.2003 |

| 5,000 | Bloom + exact check | 2.9528 |

| 5,000 | Exact HashSet | 0.3812 |

| 10,000 | Bloom + exact check | 6.0818 |

| 10,000 | Exact HashSet | 0.4993 |



The recorded experiments show that Bipartite Matching achieved more matches than Greedy in the tested cases, while Greedy was faster. Direct HashSet lookup was faster than Bloom Filter screening followed by an exact check in the Bloom benchmark.



These results are specific to the local test environment and workload.



\## Future Enhancements



\- Persistent database integration.

\- Secure authentication and authorization.

\- Appointment rescheduling.

\- Email and SMS notifications.

\- Improved doctor availability management.

\- Cloud deployment.



\## Team Contributions



| Team Member | Contributions |

|---|---|

| Soha Patel | Patient-facing frontend, appointment history, cancellation interface, Bloom Filter implementation and integration, algorithm documentation |

| Tanishka Shukla | Bipartite Matching, algorithm testing and benchmarking, Bloom Filter tests and benchmarking, technical documentation |



\## Project Documentation



| Document | Description |

|---|---|

| \[Project Proposal](reports/Project\_Proposal.md) | Project objectives, scope and expected outcomes |

| \[Technical Project Report](reports/Technical\_Project\_Report.md) | Implementation and technical details |

| \[Algorithm Analysis](docs/Algorithm\_Analysis.md) | Complexity and performance analysis |

| \[Screenshot Documentation](reports/screenshots/README.md) | Demonstration and testing evidence |



\---



\*\*MediSlot — Smart Healthcare Appointment Scheduling System\*\*



\*Advanced Algorithm Project\*

