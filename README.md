# EduTrack: Enterprise Student Attendance & Academic Performance System

**EduTrack** is a full-stack, enterprise-grade academic management system built with **Spring Boot 3 (Java 17)**, **JPA/Hibernate**, **Security + JWT**, **React + Vite**, **Vanilla CSS Design Tokens**, and **Flyway Database Migrations**.

---

## 🚀 Key Features by Role

### 👑 Admin
- **User & Roster Management**: Paginated, sorted, and server-side searchable list views for Students, Teachers, and Subjects.
- **Bulk CSV Import**: Import faculty and student rosters directly via CSV upload.
- **Audit Log Trail**: Append-only transactional event log capturing all login attempts, attendance edits, mark changes, password resets, role updates, and leave decisions with actor details, timestamps, and IP tracking.
- **Academic Calendar**: Manage working days, holidays, and exam-free dates with automatic attendance validation.
- **Announcements**: Broadcast site-wide announcements to all students and faculty.
- **Actuator Health & Metrics**: Monitoring via `/actuator/health`.

### 👨‍🏫 Faculty & Department Heads (HOD)
- **Attendance Marking**: 7-day policy window enforced, automatic working-days percentage calculation.
- **Rotating QR Check-in**: Open live 30-second rotating signed QR code sessions with optional 100m location geofence validation.
- **Attendance History Matrix**: Monthly grid visualization of student attendance.
- **Defaulters Tracking**: Configurable threshold (e.g. < 75%) with one-click CSV export.
- **Marks Evaluation & Publishing**: Record Mid, Assignment, and Final exam scores. Toggle visibility (Published/Unpublished) with weighted total calculation.
- **Dispute & Leave Approvals**: Single-review guard enforcement for attendance correction requests and leave applications (skipping weekends/holidays and appending conflict remarks).
- **At-Risk Early Warning Panel**: Rule-based 0-100 risk score with High/Medium/Low indicators and specific recommendation triggers.

### 🎓 Student
- **Dashboard Overview**: Overall attendance %, weighted average marks %, SGPA estimate, and enrolled subject cards.
- **QR Check-in**: Scan or enter rotating QR tokens to mark attendance in real time.
- **Bunk Calculator**: Calculate how many upcoming classes can be safely missed while staying above target threshold (e.g. 75%), or how many must be attended if below target.
- **Leave Application & Attachments**: Apply for leaves with PDF/JPG/PNG file upload (validated server-side, max 2MB, stored securely outside web root).
- **Attendance Disputes**: Submit correction requests for incorrect attendance markings.
- **PDF Report Cards & Certificates**: Download official performance report cards and eligibility certificates.
- **Parent Share Link**: Generate expiring, revocable read-only portal access links for parents.

### 👨‍👩‍👧 Parent & Guardian (Public Read-Only)
- **Parent Portal**: Expiring, rate-limited public view (`/parent/:token`) displaying overall attendance percentage, subject breakdown, and published examination marks only.

---

## 🔑 Demo Login Credentials

| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full system control & audit logs |
| **HOD (CS Dept)** | `cshod` | `cshod123` | CS Department Head |
| **Teacher** | `teacher1` | `teacher123` | Faculty member assigned to subjects |
| **Student** | `student1` | `student123` | Student profile (CS Dept, Sem 1) |

---

## 🛠️ Quick Start & Local Execution

### Prerequisites
- **Java 17 JDK**
- **Node.js 20+**
- **Maven** (or Maven Wrapper `.\mvnw`)
- **Docker & Docker Compose** (Optional for containerized run)

### Running Locally (H2 Profile - Development)

1. **Backend**:
   ```bash
   cd backend
   .\mvnw clean spring-boot:run
   ```
   *The backend will start at `http://localhost:8080` with H2 in-memory DB and demo data seeded.*

2. **Frontend**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   *The frontend will start at `http://localhost:5173`.*

---

## 🐳 Running with Docker Compose (MySQL Profile)

To spin up the complete production-ready stack (MySQL 8.0, Spring Boot Backend, Nginx Frontend PWA):

```bash
docker-compose up --build
```

- **Frontend Application**: `http://localhost`
- **Backend API**: `http://localhost:8080`
- **Actuator Health**: `http://localhost:8080/actuator/health`

---

## 📖 API Documentation & Swagger

- **Swagger UI Interactive Docs**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Schema**: `http://localhost:8080/v3/api-docs`
- **Postman Collection**: `Postman_Collection.json` (Included in root folder)

---

## 🧪 Verification & Testing Commands

### Backend Maven Tests
```bash
cd backend
.\mvnw test
```
*Executes all 17 unit and integration tests including security isolation, working days calculation, correction single-review guard, weighted totals, and bunk calculator edge cases.*

### Frontend Production Build
```bash
cd frontend
npm run build
```
*Compiles Vite production bundle with zero warnings/errors.*

---

## ⚡ Technical Architecture Highlights

- **JWT Authentication Filter**: Stateless authentication with `MUST_CHANGE_PASSWORD` state block.
- **Design Tokens**: Standardized CSS variables for light/dark themes, glassmorphic UI, responsive layouts, and 360px mobile viewport support.
- **i18n Multi-Language Support**: Static UI text translated across English, Telugu, and Hindi.
- **PWA Service Worker**: Installable application shell with offline attendance draft fallback (`localStorage` with auto-sync retry).
