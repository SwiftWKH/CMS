# Clinic Management System Architecture

## 1. Overview

The system follows a three-tier distributed architecture using Java RMI.

```text
Presentation Layer
-> Java RMI
-> Application Layer
-> DAO Layer
-> External Database API
-> Cloud Database
```

Clients communicate with the centralized server through Java RMI. Clients do not access the database or external storage API directly.

## 2. Layers

### Presentation Layer

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

### Application Layer

Main components:

```text
RMI Registry
ClinicRemoteInterface
ClinicServerImplementation
Service classes
Security classes
Concurrency classes
DAO classes
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

### Data Layer

The data layer uses an external cloud database accessed through an API. Storage communication uses JSON payloads.

```text
DAO
-> JSON Payload
-> External Database API
-> Cloud Database
```

## 3. Application Components

### RMI Registry

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

### ClinicRemoteInterface

Location:

```text
src/brightcare/remote/ClinicRemoteInterface.java
```

Purpose:

```text
Defines all remote methods available to clients
Acts as the shared RMI contract
```

### ClinicServerImplementation

Location:

```text
src/brightcare/server/ClinicServerImplementation.java
```

Purpose:

```text
Implements ClinicRemoteInterface
Receives remote calls
Delegates work to service classes
```

### Service Layer

Location:

```text
src/brightcare/service/
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

### DAO Layer

Location:

```text
src/brightcare/dao/
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
Delete or deactivate data
Construct JSON payloads
Communicate with external database APIs
Parse API responses
```

DAO classes are responsible for converting Java objects into JSON payloads and communicating with the external database API.

## 4. Canonical Package Structure

```text
src/
    brightcare/
        model/
            UserAccount.java
            Patient.java
            Doctor.java
            Appointment.java
            ConsultationNote.java
            Report.java
        remote/
            ClinicRemoteInterface.java
        server/
            ClinicServer.java
            ClinicServerImplementation.java
        service/
            AuthService.java
            PatientService.java
            ReceptionistService.java
            DoctorService.java
            ReportService.java
        dao/
            UserAccountDAO.java
            PatientDAO.java
            DoctorDAO.java
            AppointmentDAO.java
            ConsultationNoteDAO.java
        security/
            PermissionChecker.java
            SessionManager.java
            SSLConfig.java
        concurrency/
            AppointmentLockManager.java
        client/
            patient/
            receptionist/
            doctor/
            admin/
```

## 5. Core Entities

### UserAccount

```java
int userId;
String username;
String passwordHash;
String role;
String status;
```

### Patient

```java
int patientId;
int userId;
String firstName;
String lastName;
String icPassportNo;
String contactNumber;
String medicalRecordId;
```

### Doctor

```java
int doctorId;
int userId;
String name;
String specialization;
String contactNumber;
```

### Appointment

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

### ConsultationNote

```java
int noteId;
int appointmentId;
int doctorId;
String notes;
String diagnosis;
String prescription;
LocalDateTime createdAt;
```

### Report

```java
String reportType;
String content;
LocalDateTime generatedAt;
```

Reports are generated only. They are not persisted.

## 6. Persistence Rules

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

## 7. Method Contract

### Auth

| Method | Parameters | Return Type |
| --- | --- | --- |
| `login` | `String username, String password` | `UserAccount` |
| `logout` | `int userId` | `boolean` |
| `checkPermission` | `int userId, String requiredRole` | `boolean` |

### Receptionist

| Method | Parameters | Return Type |
| --- | --- | --- |
| `registerPatient` | `Patient patient` | `Patient` |
| `updatePatientDetails` | `Patient patient` | `Patient` |
| `createAppointment` | `Appointment appointment` | `Appointment` |
| `modifyAppointment` | `Appointment appointment` | `Appointment` |
| `cancelAppointment` | `int appointmentId` | `Appointment` |
| `viewAppointmentSchedule` | `LocalDate date` | `List<Appointment>` |

### Patient

| Method | Parameters | Return Type |
| --- | --- | --- |
| `updatePersonalInfo` | `Patient patient` | `Patient` |
| `bookAppointment` | `Appointment appointment` | `Appointment` |
| `cancelAppointment` | `int appointmentId` | `Appointment` |
| `viewAppointmentSchedule` | `int patientId` | `List<Appointment>` |
| `viewAppointmentHistory` | `int patientId` | `List<Appointment>` |
| `checkDoctorAvailability` | `int doctorId, LocalDate date` | `List<LocalTime>` |

### Doctor

| Method | Parameters | Return Type |
| --- | --- | --- |
| `viewAppointmentList` | `int doctorId, LocalDate date` | `List<Appointment>` |
| `viewMedicalHistory` | `int patientId` | `List<ConsultationNote>` |
| `updateConsultationNotes` | `ConsultationNote note` | `ConsultationNote` |
| `manageAppointmentSchedule` | `int doctorId, LocalDate date, List<LocalTime> availableSlots` | `List<LocalTime>` |

### Admin / Report

| Method | Parameters | Return Type |
| --- | --- | --- |
| `generateMonthlyAppointmentReport` | `int month, int year` | `Report` |
| `generateDoctorConsultationReport` | `int doctorId, int month, int year` | `Report` |
| `generatePatientVisitSummary` | `int patientId` | `Report` |
| `viewSystemStatistics` | none | `String` |

## 8. Team Responsibility

| Member | Main Responsibility |
| --- | --- |
| Tiong | Database layer, DAO, storage API integration |
| Leon | Patient module, RMI contract maintenance |
| Amir | Doctor module, concurrency implementation |
| Kai | Admin module, reporting, authentication, authorization, session management, SSL/TLS |
| Chen | Receptionist module, testing, reliability, integration validation |

## 9. Development Order

1. Shared model classes
2. RMI interface
3. DAO and storage API layer
4. Service layer
5. Client GUI modules
6. Concurrency handling
7. Security handling
8. Integration testing

## 10. Demo Deployment

Basic demo setup:

```text
Laptop 1: RMI Server + Application Layer + Storage API access
Laptop 2: Patient Client
Laptop 3: Doctor Client
Laptop 4: Receptionist Client
Laptop 5: Admin Client
```

Advanced demo setup:

```text
Laptop 1: Cloud/external DB access
Laptop 2: RMI Server
Laptop 3-5: Client modules
```

Fallback should be the basic demo setup.

## 11. Final Architecture Rule

All modules must follow this flow:

```text
Client UI
-> ClinicRemoteInterface
-> ClinicServerImplementation
-> Service
-> DAO
-> External Database API
-> Cloud Database
```

Any implementation that bypasses this flow is incompatible with the project architecture.
