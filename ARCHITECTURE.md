# ARCHITECTURE.md

# Clinic Management System Architecture

## 1. Architecture Overview

The system follows a three-tier distributed architecture.

```text
Presentation Layer
→ Java RMI
→ Application Layer
→ DAO Layer
→ External Database API
→ Cloud Database
```

The system uses Java RMI for communication between clients and the centralized server.

Clients do not access the database directly.

---

## 2. Three-Tier Layers

## Presentation Layer

Client programs:

```text
Patient Client
Doctor Client
Receptionist Client
Admin Client
```

Responsibilities:

```text
Display UI
Collect user input
Call remote RMI methods
Display server responses
```

Not responsible for:

```text
Database access
Business rules
Security decisions
Concurrency control
```

---

## Application Layer

Main components:

```text
RMI Registry
ClinicRemoteInterface
ClinicServerImplementation
Service Classes
Security Classes
Concurrency Classes
DAO Classes
```

Responsibilities:

```text
Receive remote client requests
Validate data
Check permissions
Apply business rules
Handle concurrency
Call DAO classes
Return results to clients
```

---

## Data Layer

The data layer uses an external cloud database accessed through an API.

Storage communication uses JSON payloads.

```text
DAO
→ JSON Payload
→ External Database API
→ Cloud Database
```

---

# 3. Application Layer Components

## RMI Registry

Started by:

```text
ClinicServer.java
```

Purpose:

```text
Expose the remote service name to clients
Allow clients to locate ClinicService
```

Example service name:

```text
ClinicService
```

---

## ClinicRemoteInterface

Located in:

```text
remote/ClinicRemoteInterface.java
```

Purpose:

```text
Defines all remote methods available to clients
Acts as the shared RMI contract
```

---

## ClinicServerImplementation

Located in:

```text
server/ClinicServerImplementation.java
```

Purpose:

```text
Implements ClinicRemoteInterface
Receives remote calls
Delegates work to service classes
```

---

## Service Layer

Located in:

```text
service/
```

Services:

```text
AuthService
PatientService
ReceptionistService
DoctorService
ReportService
```

Purpose:

```text
Business logic
Validation
Role checks
Coordination between DAOs
```

---

## DAO Layer

Located in:

```text
dao/
```

DAOs:

```text
UserAccountDAO
PatientDAO
DoctorDAO
AppointmentDAO
ConsultationNoteDAO
```

Purpose:

```text
Save data
Update data
Fetch data
Delete/deactivate data
Construct JSON payloads
Communicate with external database APIs
Parse API responses
```

## Canonical Package Structure

```text
src/
└── brightcare/
    ├── model/
    │   ├── UserAccount.java
    │   ├── Patient.java
    │   ├── Doctor.java
    │   ├── Appointment.java
    │   ├── ConsultationNote.java
    │   └── Report.java
    │
    ├── remote/
    │   └── ClinicRemoteInterface.java
    │
    ├── server/
    │   ├── ClinicServer.java
    │   └── ClinicServerImplementation.java
    │
    ├── service/
    │   ├── AuthService.java
    │   ├── PatientService.java
    │   ├── ReceptionistService.java
    │   ├── DoctorService.java
    │   └── ReportService.java
    │
    ├── dao/
    │   ├── UserAccountDAO.java
    │   ├── PatientDAO.java
    │   ├── DoctorDAO.java
    │   ├── AppointmentDAO.java
    │   └── ConsultationNoteDAO.java
    │
    ├── security/
    │   ├── PermissionChecker.java
    │   ├── SessionManager.java
    │   └── SSLConfig.java
    │
    ├── concurrency/
    │   └── AppointmentLockManager.java
    │
    └── client/
        ├── patient/
        ├── receptionist/
        ├── doctor/
        └── admin/
```

DAO classes are responsible for converting Java objects into JSON payloads and communicating with the external database API.


# 4. Core Entities

## UserAccount

```java
int userId;
String username;
String passwordHash;
String role;
String status;
```

---

## Patient

```java
int patientId;
int userId;
String firstName;
String lastName;
String icPassportNo;
String contactNumber;
String medicalRecordId;
```

---

## Doctor

```java
int doctorId;
int userId;
String name;
String specialization;
String contactNumber;
```

---

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

Status values:

```text
BOOKED
CANCELLED
COMPLETED
```

---

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

## Report

```java
String reportType;
String content;
LocalDateTime generatedAt;
```

Report is generated only.

It is not persisted.

---

# 5. Persistent Objects

Stored objects:

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
```

Not stored:

```text
Report
```

---

# 6. Method Contract

## Auth

| Method            | Parameters                         | Return Type   |
| ----------------- | ---------------------------------- | ------------- |
| `login`           | `String username, String password` | `UserAccount` |
| `logout`          | `int userId`                       | `boolean`     |
| `checkPermission` | `int userId, String requiredRole`  | `boolean`     |

---

## Receptionist

| Method                    | Parameters                | Return Type         |
| ------------------------- | ------------------------- | ------------------- |
| `registerPatient`         | `Patient patient`         | `Patient`           |
| `updatePatientDetails`    | `Patient patient`         | `Patient`           |
| `createAppointment`       | `Appointment appointment` | `Appointment`       |
| `modifyAppointment`       | `Appointment appointment` | `Appointment`       |
| `cancelAppointment`       | `int appointmentId`       | `Appointment`       |
| `viewAppointmentSchedule` | `LocalDate date`          | `List<Appointment>` |

---

## Patient

| Method                    | Parameters                     | Return Type         |
| ------------------------- | ------------------------------ | ------------------- |
| `updatePersonalInfo`      | `Patient patient`              | `Patient`           |
| `bookAppointment`         | `Appointment appointment`      | `Appointment`       |
| `cancelAppointment`       | `int appointmentId`            | `Appointment`       |
| `viewAppointmentSchedule` | `int patientId`                | `List<Appointment>` |
| `viewAppointmentHistory`  | `int patientId`                | `List<Appointment>` |
| `checkDoctorAvailability` | `int doctorId, LocalDate date` | `List<LocalTime>`   |

---

## Doctor

| Method                      | Parameters                                                     | Return Type              |
| --------------------------- | -------------------------------------------------------------- | ------------------------ |
| `viewAppointmentList`       | `int doctorId, LocalDate date`                                 | `List<Appointment>`      |
| `viewMedicalHistory`        | `int patientId`                                                | `List<ConsultationNote>` |
| `updateConsultationNotes`   | `ConsultationNote note`                                        | `ConsultationNote`       |
| `manageAppointmentSchedule` | `int doctorId, LocalDate date, List<LocalTime> availableSlots` | `List<LocalTime>`        |

---

## Admin / Report

| Method                             | Parameters                          | Return Type |
| ---------------------------------- | ----------------------------------- | ----------- |
| `generateMonthlyAppointmentReport` | `int month, int year`               | `Report`    |
| `generateDoctorConsultationReport` | `int doctorId, int month, int year` | `Report`    |
| `generatePatientVisitSummary`      | `int patientId`                     | `Report`    |
| `viewSystemStatistics`             | `none`                              | `String`    |

---

# 7. Team Responsibility

| Member | Main Responsibility                                               |
| ------ | ----------------------------------------------------------------- |
| Tiong  | Database layer, DAO, storage API integration                      |
| Leon   | Patient module, RMI contract maintenance                          |
| Amir   | Doctor module, concurrency implementation                         |
| Kai    | Admin module, reporting, authentication, authorization, session management, SSL/TLS
| Chen   | Receptionist module, testing, reliability, integration validation |

---

# 8. Development Order

1. Shared model classes
2. RMI interface
3. DAO and storage API layer
4. Service layer
5. Client GUI modules
6. Concurrency handling
7. Security handling
8. Integration testing

---

# 9. Demo Deployment

Basic demo setup:

```text
Laptop 1:
RMI Server + Application Layer + Storage API access

Laptop 2:
Patient Client

Laptop 3:
Doctor Client

Laptop 4:
Receptionist Client

Laptop 5:
Admin Client
```

Advanced demo setup:

```text
Laptop 1:
Cloud/external DB access

Laptop 2:
RMI Server

Laptop 3-5:
Client modules
```

Fallback should be the basic demo setup.

---

# 10. Final Architecture Rule

All modules must follow this flow:

```text
Client UI
→ ClinicRemoteInterface
→ ClinicServerImplementation
→ Service
→ DAO
→ External Database API
→ Cloud Database
```

Any implementation that bypasses this flow is considered incompatible with the project architecture.
