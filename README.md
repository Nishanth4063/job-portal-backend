# 🚀 Smart Job Portal – Full-Stack Application

A full-stack recruitment platform where **Recruiters** post jobs and **Candidates** discover and apply for them — with JWT-based authentication, role-based routing, PDF resume upload, and a live auto-refreshing recruiter dashboard.

Built with **Java 17 · Spring Boot 3 · Spring Security · SQL Server** on the backend and **Angular 17+ · TypeScript · RxJS** on the frontend.

---

## 📌 Project Overview

```
Candidate Registers ──> BCrypt Password Hashing ──> JWT Token Issued
         │
         ▼
Browse Job Listings ──> Upload PDF Resume ──> Apply for Job
         │
         ▼
Recruiter Dashboard ──> RxJS Auto-Refresh (5s) ──> Accept / Reject Applications
```

---

## ✨ Features

### 🔐 Authentication & Security
- JWT-based stateless authentication (register / login)
- BCrypt password encryption
- Custom JWT filter validates token on every request
- Session persisted in `localStorage` (token, role, userId)
- Auth guard protects all private routes
- JWT HTTP Interceptor auto-attaches Bearer token to every API call

### 👥 Role-Based Architecture
- **CANDIDATE** — Browse jobs, upload PDF resume, apply, track application status
- **RECRUITER** — Post jobs, view incoming applications, accept or reject candidates
- Role-based routing after login: CANDIDATE → `/jobs`, RECRUITER → `/recruiter`
- URL-level security: `/candidate/:id` validates route param against session `userId`

### 💼 Job Module
- Recruiters post jobs (title, location, description)
- View all active job listings
- Search jobs by title and location

### 📝 Application Module
- Candidates apply with PDF resume upload (`multipart/form-data`)
- Applications saved with default `PENDING` status
- Candidates view their own application history with live status updates
- Recruiters view all applications across their posted jobs
- Recruiters update application status (ACCEPTED / REJECTED)

### 📊 Recruiter Dashboard
- View all jobs posted by the logged-in recruiter
- View all incoming candidate applications
- **Auto-refresh every 5 seconds** using RxJS `interval` — no manual reload needed

### ⚠️ Exception Handling
- Global exception handler using `@ControllerAdvice`
- Structured JSON error responses for: duplicate email, invalid token, unauthorized access

---

## 🖥️ Application Screenshots

### 1. Login Page
![Login](assets/Login.png)

### 2. Register Page
![Register](assets/Register.png)

### 3. Job Listings (Candidate View)
![Jobs](assets/Jobs.png)

### 4. Application Status Tracking (Candidate Dashboard)
![Application Status](assets/Application_Status.png)

### 5. Recruiter – Post a Job
![Job Post](assets/Job_Post.png)

### 6. Recruiter – Incoming Applications
![Job Status](assets/Job_Status.png)

---

## 🛠 Tech Stack

### Backend
| Technology | Usage |
|---|---|
| Java 17 | Core language |
| Spring Boot 3 | Backend framework |
| Spring Security | Authentication & Authorization |
| JWT | Stateless token-based auth |
| Spring Data JPA | ORM / Database operations |
| SQL Server (T-SQL) | Relational database |
| BCrypt | Password encryption |
| Maven | Build tool |
| Postman | API testing |

### Frontend
| Technology | Usage |
|---|---|
| Angular 17+ | Frontend framework (Standalone Components) |
| TypeScript | Strongly typed language |
| RxJS | Reactive API calls, auto-polling with `interval` |
| Angular Router | Route protection with `authGuard` |
| Angular Reactive Forms | Form validation |
| SCSS | Styling |
| JWT Interceptor | Auto-attaches Bearer token to every HTTP request |

---

## 📂 Project Structure

```
job-portal/
├── assets/                              → UI screenshots
├── job-portal-backend/
│   ├── uploads/resumes/                 → Uploaded PDF resumes stored here
│   └── jobportal/
│       └── src/main/java/com/nishanth/jobportal/
│           ├── config/          → SecurityConfig.java
│           ├── controller/      → AuthController, JobController, ApplicationController, UserController
│           ├── service/         → UserService, JobService, ApplicationService
│           ├── repository/      → UserRepository, JobRepository, ApplicationRepository
│           ├── entity/          → User, Job, Application
│           ├── dto/             → AuthResponse, JobResponseDTO, LoginRequest
│           ├── enums/           → Role (CANDIDATE, RECRUITER, ADMIN)
│           ├── security/        → JwtUtils, JwtAuthenticationFilter
│           └── exception/       → GlobalExceptionHandler, ErrorResponse
│
└── job-portal-frontend/
    └── src/app/
        ├── components/
        │   ├── login/                → Login form with role-based redirect
        │   ├── register/             → Register with role selection
        │   ├── job-list/             → Browse, search, upload resume, apply
        │   ├── recruiter-dashboard/  → Post jobs, view & update applications
        │   └── candidate-dashboard/  → View personal application history
        ├── services/
        │   ├── auth/auth.service.ts  → Register, login, session helpers
        │   └── job/job.service.ts    → All job and application API calls
        ├── guards/
        │   └── auth-guard.ts         → Protects private routes
        ├── interceptors/
        │   └── jwt-interceptor.ts    → Attaches JWT token to every HTTP request
        └── models/
            ├── user.ts
            ├── job.ts
            └── application.ts
```

---

## 🗄 Database Design

### 👤 users
| Column | Type | Description |
|---|---|---|
| id | INT PK | Auto-generated ID |
| name | VARCHAR(255) | User display name |
| email | VARCHAR(255) UNIQUE | Login credential |
| password | VARCHAR(255) | BCrypt encrypted |
| role | VARCHAR(50) | CANDIDATE / RECRUITER / ADMIN |

### 💼 jobs
| Column | Type | Description |
|---|---|---|
| id | INT PK | Auto-generated ID |
| title | VARCHAR(255) | Job title |
| location | VARCHAR(255) | Job location |
| description | TEXT | Job description |
| user_id | INT FK → users(id) | Recruiter who posted |
| posted_by_name | VARCHAR(255) | Recruiter name |

### 📝 applications
| Column | Type | Description |
|---|---|---|
| id | INT PK | Auto-generated ID |
| user_id | INT FK → users(id) | Candidate ID |
| job_id | INT FK → jobs(id) | Job applied for |
| resume_path | VARCHAR(500) | Uploaded PDF path |
| status | VARCHAR(50) | PENDING / ACCEPTED / REJECTED |

---

## 🔗 API Endpoints

### 🔐 Auth
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### 💼 Jobs
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/jobs/create/{userId}` | RECRUITER |
| GET | `/api/jobs/all` | Authenticated |
| GET | `/api/jobs/search?title=&location=` | Authenticated |
| GET | `/api/jobs/recruiter/{recruiterId}` | RECRUITER |

### 📝 Applications
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/applications/apply/{userId}/{jobId}` | CANDIDATE |
| GET | `/api/applications/candidate/{userId}` | CANDIDATE |
| GET | `/api/applications/recruiter/{recruiterId}` | RECRUITER |
| PUT | `/api/applications/{id}/status?status=&employerId=` | RECRUITER |

---

## ▶️ How to Run

### 1️⃣ Clone the repository
```bash
git clone https://github.com/Nishanth4063/job-portal-backend.git
cd job-portal
```

### 2️⃣ Database setup (SQL Server)
```sql
CREATE DATABASE JobPortalDB;
```

### 3️⃣ Configure backend (`application.properties`)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=JobPortalDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

### 4️⃣ Run the backend
```bash
cd job-portal-backend/jobportal
mvn spring-boot:run
```
Backend runs at: `http://localhost:8080`

### 5️⃣ Run the frontend
```bash
cd job-portal-frontend
npm install
ng serve
```
Frontend runs at: `http://localhost:4200`

---

## 📬 Sample Requests

### Register
```json
POST /api/auth/register
{
  "name": "Nishanth",
  "email": "nishanth@gmail.com",
  "password": "secure123",
  "role": "CANDIDATE"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "nishanth@gmail.com",
  "role": "CANDIDATE",
  "id": 1
}
```

---

## 📚 What I Learned

- JWT authentication with Spring Security and stateless session management
- Role-Based Access Control (RBAC) with Java Enums
- Angular standalone component architecture with route protection
- JWT HTTP Interceptor for automatic token attachment on every request
- Multipart file upload (PDF resume) from Angular to Spring Boot
- RxJS `interval` for background auto-polling without page reload
- Global exception handling with `@ControllerAdvice`
- Foreign key relationships and cascade behavior in SQL Server

---

## 👨‍💻 Author

**Nishanth K** — Java Backend Developer  
[GitHub](https://github.com/Nishanth4063) | [Email](mailto:nishanth.sks2003@gmail.com)