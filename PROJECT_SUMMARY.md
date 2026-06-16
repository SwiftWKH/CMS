# Clinic Management System - Development Baseline

## Architecture

```text
Presentation Layer
-> Java RMI
-> Application Layer
-> DAO Layer
-> External Database API
-> Cloud Database
```

Rules:

1. Clients never access the database directly.
2. All requests go through Java RMI.
3. Business logic resides on the server.
4. Storage access is performed only through DAO classes.
5. Reports are generated dynamically and are not stored.

## Core Entities

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

Reports are generated only.

## Persistent Objects

Stored:

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

## DAO Layer

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
-> JSON Payload
-> External Database API
-> Cloud Database
```

## Service Layer

### AuthService

```java
login()
logout()
checkPermission()
```

### PatientService

```java
updatePersonalInfo()
bookAppointment()
cancelAppointment()
viewAppointmentSchedule()
viewAppointmentHistory()
checkDoctorAvailability()
```

### ReceptionistService

```java
registerPatient()
updatePatientDetails()
createAppointment()
modifyAppointment()
cancelAppointment()
viewAppointmentSchedule()
```

### DoctorService

```java
viewAppointmentList()
viewMedicalHistory()
updateConsultationNotes()
manageAppointmentSchedule()
```

### ReportService

```java
generateMonthlyAppointmentReport()
generateDoctorConsultationReport()
generatePatientVisitSummary()
viewSystemStatistics()
```

## RMI Contract

Remote interface:

```java
ClinicRemoteInterface
```

Responsibilities:

```text
Define all remotely callable methods
Serve as the shared contract used by every client
Keep method signatures synchronized across the team
```

## Application Layer Structure

```text
src/brightcare/
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
    security/
        SessionManager.java
        PermissionChecker.java
        SSLConfig.java
    concurrency/
        AppointmentLockManager.java
    dao/
        UserAccountDAO.java
        PatientDAO.java
        DoctorDAO.java
        AppointmentDAO.java
        ConsultationNoteDAO.java
```

## Team Ownership

| Member | Ownership |
| --- | --- |
| Tiong | Database layer, DAO classes, storage API integration, JSON mapping, database schema |
| Leon | Patient module, patient UI, patient service, RMI contract maintenance |
| Amir | Doctor module, doctor UI, doctor service, concurrency implementation |
| Kai | Admin and security module, authentication, authorization, reports, session management, SSL/TLS |
| Chen | Receptionist module, receptionist UI, receptionist service, testing, integration validation |

## Development Order

1. Shared models
2. RMI contract
3. DAO layer
4. Services
5. Client GUIs
6. Concurrency
7. Security
8. Integration testing

## Non-Negotiable Rules

1. Clients never call the database API directly.
2. Services never bypass DAO classes.
3. Shared model classes must remain synchronized.
4. Only one version of `ClinicRemoteInterface` may exist.
5. Reports are generated, not persisted.
6. Concurrency and security are added after core functionality works.
