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

UI structure:

```text
Swing JFrame view
-> module controller
-> RMI/authentication gateway
-> ClinicRemoteInterface
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

### Local Derby Adapter

For integration testing before the final external API endpoint is available, DAO classes may temporarily use Derby:

```text
DAO
-> Derby JDBC
-> Local BRIGHTCARE_DB
```

This is an implementation detail inside the DAO layer only. It must not change client, controller, RMI, server, or service responsibilities. When the final API is ready, replace the Derby-backed DAO/provider implementation with the API-backed implementation while preserving the same service and remote method behavior.

Derby scripts live in:

```text
database/brightcare_schema.sql
database/brightcare_seed.sql
```

Reports remain generated runtime artifacts. They are not stored in Derby and no `ReportDAO` or `REPORT` table should be introduced.

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

Current implementation service name:

```text
BrightCareClinicService
```

RMI uses TCP transport. Clients can connect to a remote server by setting:

```text
brightcare.rmi.host
brightcare.rmi.port
```

Remote-call logs are written to `logs/brightcare.log` and include method names, key IDs, dates/times, and client host where available.

SSL-RMI is enabled by default using shared development stores:

```text
config/ssl/brightcare-rmi-keystore.p12
config/ssl/brightcare-rmi-truststore.p12
```

Default store password:

```text
brightcare
```

`SSLConfig` auto-loads these files. Use `brightcare.rmi.ssl=false` on both server and clients only when troubleshooting plain RMI.

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

Appointment booking/update paths use `AppointmentLockManager` to protect doctor/date/time slots during concurrent RMI calls. This is intended to prevent two clients from booking the same doctor slot at the same time.

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

During the local Derby bypass, DAO classes perform equivalent CRUD through JDBC. The class boundary stays the same so the final API can replace Derby without rewriting UI or service code.

### Hospital API Adapter

The current API base URL is:

```text
https://192.168.137.1:7230/hospital
```

The application defaults to API-first DAO access with Derby fallback:

```text
-Dbrightcare.data.source=api
-Dbrightcare.data.source=derby
```

Verified API resource endpoints:

```text
/doctor
/user
/patient
/appointment
/consultation
/appwcon
```

`/user` is the final user-account data endpoint currently supplied by the team. `AuthService`, `AdminService`, and `UserAccountDAO` use `/user` first, then Derby as the local integration fallback. No separate `/auth` or `/login` endpoint is currently used.

### User To Role Identity Mapping

Login/account IDs and role-record IDs are not interchangeable.

```text
/user.user_id          Authentication/session identity
/user.role_id          Patient/doctor role-record identity from the user endpoint
/patient.patientID     Patient module identity
/doctor.doctorID       Doctor module identity
```

The current API design is:

```text
USER.role_id points to the matching role record for the account role
```

After login, routing should resolve:

```text
user_id + role + role_id -> role module opens with role_id
```

Example:

```text
john logs in -> /user.user_id is 11 -> /user.role_id is 8 -> Patient page uses patientID 8
```

The Patient/Doctor pages must receive the role record ID for role operations, while logout/session handling must continue using `user_id`. Username-based inference such as `P2 -> patientID 2` and `D3 -> doctorID 3` is now legacy fallback only when `/user.role_id` is missing.

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
            gateway/
                AuthenticationGateway.java
                AdminGateway.java
                RmiAuthenticationGateway.java
                RmiAdminGateway.java
                UserSummary.java
                SessionSummary.java
            common/
                CommonClient.java
                controller/
                    LoginController.java
                    NavigationController.java
                view/
                    LoginFrame.java
                    LoginFrame.form
                    MainMenuFrame.java
                    MainMenuFrame.form
                    AccessDeniedDialog.java
                    AccessDeniedDialog.form
                    SessionExpiredDialog.java
                    SessionExpiredDialog.form
            patient/
                PatientClient.java
                controller/
                    PatientController.java
                view/
                    PatientFrame.java
                    PatientFrame.form
            receptionist/
                ReceptionistClient.java
                controller/
                    ReceptionistController.java
                view/
                    ReceptionistFrame.java
                    ReceptionistFrame.form
            doctor/
                DoctorClient.java
                controller/
                    DoctorController.java
                view/
                    DoctorFrame.java
                    DoctorFrame.form
            admin/
                AdminClient.java
                controller/
                    AdminController.java
                view/
                    AdminFrame.java
                    AdminFrame.form
                    CreateUserFrame.java
                    CreateUserFrame.form
                    ViewUsersFrame.java
                    ViewUsersFrame.form
                    DisableUserFrame.java
                    DisableUserFrame.form
                    MonthlyAppointmentReportFrame.java
                    MonthlyAppointmentReportFrame.form
                    DoctorConsultationReportFrame.java
                    DoctorConsultationReportFrame.form
                    PatientVisitSummaryFrame.java
                    PatientVisitSummaryFrame.form
                    SystemStatisticsFrame.java
                    SystemStatisticsFrame.form
                    ActiveSessionsFrame.java
                    ActiveSessionsFrame.form
```

Client package rules:

```text
*Client.java       Starts the module UI
controller/*.java Coordinates UI events and RMI calls
view/*Frame.java  NetBeans/Swing JFrame logic shell
view/*Frame.form  NetBeans GUI Builder metadata
```

Each JFrame must keep its `.java` and `.form` pair with the same base name. Controllers may call `ClinicRemoteInterface` or small gateway interfaces that will be backed by RMI. JFrame classes should not contain database logic, service logic, direct service calls, or direct storage access.

Shared screens that are not tied to one user role belong in `client/common`.

Gateway classes in `client/gateway` isolate Swing controllers from RMI lookup and remote exceptions. Client code must not instantiate server-side service classes directly.

## 5. Core Entities

The field lists in this section must match `Contract Table.xlsx`.

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
int reportId;
String reportType;
int generatedBy;
LocalDateTime generatedAt;
String filePath;
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

## 7. Local Testing Boundary

Production architecture must not be changed just to support local testing.

Use this boundary:

```text
src/        Production application code
test/       Optional test harness code
local/      Ignored local-only files
mock-data/  Ignored fake API/database payloads
sandbox/    Ignored experiments
```

Local mock data can be used to test Kai's features before the DAO layer and teammate modules are ready, but production classes should not depend directly on mock-only classes or files.

Recommended pattern:

```text
Service
-> constructor parameter or simple collaborator interface
-> real DAO later, mock collaborator in tests
```

This keeps Kai's module testable without exporting fake out-of-scope data to teammates.

## 8. Method Contract

The method names, parameters, and return types in this section must match `Contract Table.xlsx`.

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

## 9. Team Responsibility

| Member | Main Responsibility |
| --- | --- |
| Tiong | Database layer, DAO, storage API integration |
| Leon | Patient module, RMI contract maintenance |
| Amir | Doctor module, concurrency implementation |
| Kai | Admin module, reporting, authentication, authorization, session management, SSL/TLS |
| Chen | Receptionist module, testing, reliability, integration validation |

## 10. Development Order

1. Shared model classes
2. RMI interface
3. DAO and storage API layer
4. Service layer
5. Client GUI modules
6. Concurrency handling
7. Security handling
8. Integration testing

## 11. Demo Deployment

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

## 12. Final Architecture Rule

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

## 13. TCP/UDP Position

TCP is covered by Java RMI. UDP is not implemented in the current system and should remain a backlog item unless required by the final rubric.
