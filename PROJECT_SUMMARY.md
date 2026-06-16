# Clinic Management System - Development Baseline

## Architecture

```text
Presentation Layer
    ↓
Java RMI
    ↓
Application Layer
    ↓
External Database API
    ↓
Cloud Database
```

Rules:

* Clients never access the database directly.
* All requests go through Java RMI.
* Business logic resides on the server.
* Storage access is performed only through DAO classes.

---

# Core Entities

## UserAccount

```java
int userId;
String username;
String passwordHash;
String role;
String status;
```

## Patient

```java
int patientId;
int userId;
String firstName;
String lastName;
String icPassportNo;
String contactNumber;
```

## Doctor

```java
int doctorId;
int userId;
String name;
String specialization;
String contactNumber;
```

## Appointment

```java
int appointmentId;
int patientId;
int doctorId;
LocalDate appointmentDate;
LocalTime appointmentTime;
String status;
String reason;
```

## ConsultationNote

```java
int noteId;
int appointmentId;
int doctorId;
String notes;
String diagnosis;
String prescription;
LocalDateTime createdAt;
```

---

# Persistent Objects

These objects must be stored.

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
```

Generated only:

```text
Report
```

Reports are not stored.

---

# DAO Layer

Required DAOs:

```text
UserAccountDAO
PatientDAO
DoctorDAO
AppointmentDAO
ConsultationNoteDAO
```

Storage communication:

```text
DAO
↓
JSON Payload
↓
External Database API
↓
Cloud Database
```

---

# Service Layer

## AuthService

```java
login()
logout()
checkPermission()
```

## PatientService

```java
updatePersonalInfo()
bookAppointment()
cancelAppointment()
viewAppointmentSchedule()
viewAppointmentHistory()
checkDoctorAvailability()
```

## ReceptionistService

```java
registerPatient()
updatePatientDetails()
createAppointment()
modifyAppointment()
cancelAppointment()
viewAppointmentSchedule()
```

## DoctorService

```java
viewAppointmentList()
viewMedicalHistory()
updateConsultationNotes()
manageAppointmentSchedule()
```

## ReportService

```java
generateMonthlyAppointmentReport()
generateDoctorConsultationReport()
generatePatientVisitSummary()
viewSystemStatistics()
```

---

# RMI Contract

Remote Interface:

```java
ClinicRemoteInterface
```

Responsibilities:

* Define all remotely callable methods.
* Shared contract used by every client.
* Method signatures must remain synchronized across the team.

---

# Application Layer Structure

```text
remote/
└── ClinicRemoteInterface.java

server/
├── ClinicServer.java
└── ClinicServerImplementation.java

service/
├── AuthService.java
├── PatientService.java
├── ReceptionistService.java
├── DoctorService.java
└── ReportService.java

security/
├── SessionManager.java
├── PermissionChecker.java
└── SSLConfig.java

concurrency/
└── AppointmentLockManager.java

dao/
├── UserAccountDAO.java
├── PatientDAO.java
├── DoctorDAO.java
├── AppointmentDAO.java
└── ConsultationNoteDAO.java

storage/
├── ApiClient.java
├── JsonMapper.java
└── StorageConfig.java
```

---

# Team Ownership

## Tiong

Database Layer

```text
DAO Classes
Storage API Integration
JSON Mapping
Database Schema
```

## Leon

Patient Module

```text
Patient UI
Patient Service
RMI Contract Maintenance
```

## Amir

Doctor Module

```text
Doctor UI
Doctor Service
Concurrency Implementation
```

## Kai

Admin & Security Module

```text
Authentication
Authorization
Reports
SSL/TLS
```

## Chen

Receptionist Module

```text
Receptionist UI
Receptionist Service
Testing
System Integration Validation
```

---

# Development Order

## Phase 1

Shared Models

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
```

## Phase 2

RMI Contract

```text
ClinicRemoteInterface
```

## Phase 3

DAO Layer

```text
Storage API
JSON Payloads
CRUD Operations
```

## Phase 4

Services

```text
Auth
Patient
Receptionist
Doctor
Report
```

## Phase 5

Client GUIs

```text
Patient
Receptionist
Doctor
Admin
```

## Phase 6

Concurrency

```text
Appointment Locking
Multi-client Access
```

## Phase 7

Security

```text
RBAC
Session Validation
SSL/TLS
```

---

# Non-Negotiable Rules

1. Clients never call the database API directly.
2. Services never bypass DAO classes.
3. Shared model classes must remain synchronized.
4. Only one version of ClinicRemoteInterface may exist.
5. Reports are generated, not persisted.
6. Concurrency and security are added after core functionality works.
