# MediSlot
### Smart Healthcare Appointment Scheduling System

MediSlot is a web-based healthcare appointment scheduling application that simplifies doctor discovery, appointment booking, and patient appointment management. It combines modern web technologies with advanced algorithms to improve appointment allocation and data processing.

---

## 1. Technology Stack

| Category | Technology | Purpose |
|---|---|---|
| Frontend | ![React](https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=react&logoColor=black) | User interface development |
| Frontend Tool | ![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat-square&logo=vite&logoColor=white) | Frontend development and build |
| Programming Language | ![Java 17](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white) | Backend development |
| Backend Framework | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) | REST API development |
| Build Tool | ![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white) | Dependency management and builds |
| Testing | ![JUnit](https://img.shields.io/badge/JUnit-25A162?style=flat-square&logo=junit5&logoColor=white) | Unit testing |
| Version Control | ![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white) | Source code management |
| Repository Hosting | ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white) | Project collaboration and hosting |

---

## 2. Project Overview

MediSlot provides a centralized platform where patients can:

- Register and manage their information.
- Browse doctors by specialization.
- Book appointments.
- View appointment history.
- Cancel confirmed appointments.

The project also demonstrates the practical implementation of advanced algorithms for appointment allocation and preliminary email membership screening.

## 3. Key Features

| Feature | Description |
|---|---|
| Patient Registration | Allows new patients to register with their information. |
| Doctor Listing | Displays available doctors and their specializations. |
| Appointment Booking | Enables patients to book appointments. |
| Appointment History | Displays previously booked appointments and their status. |
| Appointment Cancellation | Allows patients to cancel confirmed appointments. |
| Algorithmic Allocation | Uses Bipartite Matching to model appointment allocation. |
| Email Screening | Uses a Bloom Filter for preliminary email membership checks. |

## 4. System Architecture

```mermaid
flowchart TD
    A[Patient] --> B[React Frontend]
    B <-->|REST API| C[Spring Boot Backend]
    C --> D[Patient Management]
    C --> E[Appointment Management]
    D --> F[Bloom Filter]
    E --> G[Bipartite Matching]
    D --> H[API Response]
    E --> H
    H --> B
```

The frontend communicates with the backend through REST APIs. The backend handles patient and appointment operations and applies the relevant algorithmic logic.

## 5. Advanced Algorithms

### 5.1 Bipartite Matching

The project implements a DFS-based augmenting path approach to find maximum matching between appointment requests and available slots.

| Property | Complexity |
|---|---|
| Time Complexity | O(V × E) |
| Space Complexity | O(V + E) |
| Technique | DFS-based Augmenting Paths |

**Purpose:** To find a maximum number of valid assignments while ensuring that a slot is not assigned to multiple requests.

### 5.2 Bloom Filter

A Bloom Filter is a probabilistic data structure used for preliminary email membership screening. It uses multiple hash functions and a BitSet to represent possible membership.

| Operation | Complexity |
|---|---|
| Insertion | O(k) |
| Lookup | O(k) |
| Space | O(m) bits |

**Important:** A Bloom Filter can produce false positives. Therefore, positive membership results are verified using an exact repository lookup.

## 6. Project Structure

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
│   └── Algorithm_Analysis.md
│
├── reports/
│   ├── Project_Proposal.md
│   ├── Technical_Project_Report.md
│   └── screenshots/
│
└── README.md
```

## 7. Installation and Setup

### Prerequisites

| Software | Requirement |
|---|---|
| Java | Version 17 or later |
| Maven | Installed and configured |
| Node.js | Installed |
| npm | Installed |
| Git | Installed |

### Clone the Repository

```bash
git clone https://github.com/SohaPatel/MediSlot.git
cd MediSlot
```

### Backend Setup

The backend now uses MySQL with Flyway migrations. Start a local MySQL 8.4
instance with Docker Compose:

```bash
cp .env.example .env
# Edit .env and set strong local passwords before starting the database.
docker compose up -d mysql
```

Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (at least 32 random
bytes), and optionally `JWT_EXPIRATION_MS` in the backend process environment.
`backend/.env.example` lists the supported names. Do not commit real secrets.
The first backend start applies `backend/src/main/resources/db/migration` and
seeds the original doctor directory if it is empty.

For PowerShell, set the backend variables in the terminal that will run Maven:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/medislot?serverTimezone=UTC"
$env:DB_USERNAME = "medislot"
$env:DB_PASSWORD = "the same DB_PASSWORD used by Docker Compose"
$env:JWT_SECRET = "a locally generated secret of at least 32 bytes"
$env:DOCTOR_REGISTRATION_CODE = "a private doctor onboarding code"
$env:ADMIN_EMAIL = "admin@example.local"
$env:ADMIN_PASSWORD = "a strong admin password"
```

The first startup seeds the configured administrator account with a BCrypt
password hash. Doctor self-registration is gated by `DOCTOR_REGISTRATION_CODE`;
an administrator can also provision an inactive doctor profile for the doctor
to claim with that code.

```bash
cd backend
mvn clean test
mvn spring-boot:run
```

### Frontend Setup

Open a separate terminal:

```bash
cd frontend
npm install
```

## 8. Running the Application

The frontend and backend must run in separate terminals.

**Start the Backend:**

```bash
cd backend
mvn spring-boot:run
```

Backend URL: `http://localhost:8081`

**Start the Frontend:**

```bash
cd frontend
npm run dev
```

Frontend URL: `http://localhost:5173`

## 9. API Endpoints

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/patients/register` | Register a patient |
| GET | `/api/patients` | Retrieve patients |
| GET | `/api/appointments` | Retrieve appointments |
| GET | `/api/appointments/doctors` | Retrieve doctors |
| POST | `/api/appointments/book` | Book an appointment |
| GET | `/api/appointments/patient/{patientId}` | Retrieve patient appointment history |
| PATCH | `/api/appointments/{id}/cancel` | Cancel an appointment |
| PATCH | `/api/appointments/{id}/reschedule` | Reschedule an appointment |
| GET | `/api/appointments/{id}/confirmation.pdf` | Download an appointment confirmation |
| GET | `/api/appointments/recommendations` | Rank doctors for a requested specialty and slot |
| GET | `/api/appointments/search-suggestions?prefix=...` | Trie-based doctor and specialty suggestions |
| GET | `/api/appointments/search/date?date=YYYY-MM-DD` | Search the authenticated patient's appointments by date |
| POST/GET | `/api/appointments/waiting-list` | Join or view the patient's waiting-list entries |
| GET/PUT | `/api/patients/me` | View or update the authenticated patient's profile |
| GET/POST | `/api/patients/me/medical-history` | View or add the patient's private history |
| POST | `/api/doctors/register` | Register/claim a doctor account with onboarding code |
| POST | `/api/doctors/login` | Doctor login and JWT issuance |
| GET/PUT | `/api/doctors/me` | View or update doctor profile |
| PUT | `/api/doctors/me/availability` | Replace recurring available times |
| PUT | `/api/doctors/me/blocked-dates` | Set unavailable dates |
| GET | `/api/doctors/me/appointments` | View doctor-owned appointments by range |
| POST | `/api/admin/login` | Bootstrap-configured admin login |
| GET | `/api/admin/dashboard` | Admin appointment and account counts |
| GET | `/api/admin/analytics` | Historical daily, doctor, specialty, and peak-hour counts |

Patient login returns `{ patient, token, role }`. Send the token as a Bearer
token on protected API requests. The existing patient appointment listing and
cancellation endpoints are scoped to the authenticated patient's account;
administrative listing endpoints require the ADMIN role.

## 10. Testing

The backend uses JUnit for algorithm unit testing.

**Run the test suite:**

```bash
cd backend
mvn clean test
```

### Recorded Test Results

| Test Suite | Tests Passed | Failures | Errors |
|---|---:|---:|---:|
| Bipartite Matching | 8 | 0 | 0 |
| Bloom Filter | 8 | 0 | 0 |
| **Total** | **16** | **0** | **0** |

## 11. Performance Analysis

### 11.1 Bipartite Matching vs Greedy

| Input Size | Algorithm | Average Execution Time (ms) | Matched |
|---:|---|---:|---:|
| 100 | Bipartite Matching | 0.1132 | 100 |
| 100 | Greedy | 0.0325 | 96 |
| 300 | Bipartite Matching | 0.3307 | 300 |
| 300 | Greedy | 0.1425 | 280 |
| 600 | Bipartite Matching | 1.1118 | 600 |
| 600 | Greedy | 0.2541 | 564 |

**Observation:** Bipartite Matching achieved more matches in the recorded experiments, while Greedy had lower execution time.

### 11.2 Bloom Filter vs HashSet

| Input Size | Lookup Method | Average Execution Time (ms) |
|---:|---|---:|
| 1,000 | Bloom Filter + Exact Check | 0.9013 |
| 1,000 | Exact HashSet | 0.2003 |
| 5,000 | Bloom Filter + Exact Check | 2.9528 |
| 5,000 | Exact HashSet | 0.3812 |
| 10,000 | Bloom Filter + Exact Check | 6.0818 |
| 10,000 | Exact HashSet | 0.4993 |

**Observation:** Direct HashSet lookup was faster than Bloom Filter screening followed by an exact check in the recorded benchmark. Bloom Filters may be useful when they avoid more expensive external lookups.

*All benchmark results are specific to the local testing environment and workload.*

## 12. Future Enhancements

| Area | Proposed Enhancement |
|---|---|
| Database | Integration with persistent database storage |
| Security | Authentication and role-based authorization |
| Scheduling | Appointment rescheduling |
| Notifications | Email and SMS notifications |
| Availability | Improved doctor availability management |
| Deployment | Cloud-based deployment |
| Platform | Doctor and admin accounts, clinical workflows, and remaining advanced modules |

## 13. Team Contributions

| Team Member | Contributions |
|---|---|
| Soha Patel | Patient-facing frontend, appointment history, cancellation interface, Bloom Filter implementation and integration, algorithm analysis documentation |
| Tanishka Shukla | Bipartite Matching algorithm work, algorithm testing and benchmarking, Bloom Filter testing and benchmarking, technical documentation |

## 14. Project Documentation

| Document | Description |
|---|---|
| [Project Proposal](reports/Project_Proposal.md) | Project objectives, scope, and expected outcomes |
| [Technical Project Report](reports/Technical_Project_Report.md) | Technical implementation and project details |
| [Algorithm Analysis](docs/Algorithm_Analysis.md) | Algorithm complexity and performance analysis |
| [Screenshot Documentation](reports/screenshots/README.md) | Application and testing evidence |

---

<div align="center">

**MediSlot**

Smart Healthcare Appointment Scheduling System

Advanced Algorithm Project

</div>
