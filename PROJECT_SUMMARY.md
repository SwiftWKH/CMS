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

Shared model fields must match `Contract Table.xlsx`.

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
int reportId;
String reportType;
int generatedBy;
LocalDateTime generatedAt;
String filePath;
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

## Local Testing Boundary

Kai's module may use mock data before teammate-owned modules are ready.

Allowed local-only locations:

```text
test/
local/
mock-data/
sandbox/
```

Rules:

1. Production code belongs in `src/`.
2. Mock payloads belong in ignored local folders.
3. Production services should use replaceable collaborators instead of directly depending on mock classes.
4. Local mock data is not part of the teammate handoff.

## Local Derby Bypass

Current integrated testing may use Derby until the final external API endpoint is ready.

```text
jdbc:derby://localhost:1527/BRIGHTCARE_DB
app / app
database/brightcare_schema.sql
database/brightcare_seed.sql
```

Derby-backed DAOs are a temporary DAO-layer adapter. The intended final swap is:

```text
Derby DAO internals
-> External API DAO internals
```

The following should remain stable during that swap:

```text
Swing views
Controllers
Gateway interfaces
ClinicRemoteInterface methods
ClinicServerImplementation delegation
Service method behavior
```

Generated reports are runtime files under `generated-reports/` and are not database records.

Seed credentials:

```text
admin1 / admin123
doc01  / doctor123
rec01  / receptionist123
pat01  / patient123
doc02  / doctor123
pat02  / patient123
```

## Hospital API Status

Current API:

```text
https://192.168.137.1:7230/hospital
```

Data source mode:

```text
Default: API first with Derby fallback
-Dbrightcare.data.source=api
-Dbrightcare.data.source=derby
```

Verified:

```text
/doctor
/patient
/appointment
/consultation
```

Not currently available:

```text
user/auth/login API endpoints
```

Authentication and Admin user management still use Derby `USER_ACCOUNT` until a user-account API contract is supplied.

## Export Boundary

Default exports are implementation-only puzzle pieces. Include only the selected component implementation, NetBeans `.java/.form` pairs, required controllers/gateways, minimal compile skeletons, and minimal NetBeans/Git recognition files.

Exclude:

```text
docs
diagrams
spreadsheets
test/
local/
mock-data/
sandbox/
build/
dist/
.class files
unselected teammate modules
```

Zip naming:

```text
CMS-[Admin].zip
CMS-[Admin][Receptionist].zip
CMS-[Admin][Receptionist][Doctor][Patient].zip
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

Runtime transport:

```text
Java RMI over TCP
Default port: 1099
Service name: BrightCareClinicService
Client host property: brightcare.rmi.host
Client port property: brightcare.rmi.port
Logs: logs/brightcare.log
```

Optional SSL-RMI:

```text
brightcare.rmi.ssl=true
```

Requires standard Java SSL keystore/truststore properties. Disabled by default.

Concurrency:

```text
SessionManager synchronizes session operations.
AppointmentLockManager guards doctor/date/time appointment slots.
```

UDP:

```text
Not implemented. Backlog unless required by rubric.
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
            controller/LoginController.java
            controller/NavigationController.java
            view/LoginFrame.java
            view/LoginFrame.form
            view/MainMenuFrame.java
            view/MainMenuFrame.form
            view/AccessDeniedDialog.java
            view/AccessDeniedDialog.form
            view/SessionExpiredDialog.java
            view/SessionExpiredDialog.form
        admin/
            AdminClient.java
            controller/AdminController.java
            view/AdminFrame.java
            view/AdminFrame.form
            view/CreateUserFrame.java
            view/CreateUserFrame.form
            view/ViewUsersFrame.java
            view/ViewUsersFrame.form
            view/DisableUserFrame.java
            view/DisableUserFrame.form
            view/MonthlyAppointmentReportFrame.java
            view/MonthlyAppointmentReportFrame.form
            view/DoctorConsultationReportFrame.java
            view/DoctorConsultationReportFrame.form
            view/PatientVisitSummaryFrame.java
            view/PatientVisitSummaryFrame.form
            view/SystemStatisticsFrame.java
            view/SystemStatisticsFrame.form
            view/ActiveSessionsFrame.java
            view/ActiveSessionsFrame.form
        patient/
            PatientClient.java
            controller/PatientController.java
            view/PatientFrame.java
            view/PatientFrame.form
        doctor/
            DoctorClient.java
            controller/DoctorController.java
            view/DoctorFrame.java
            view/DoctorFrame.form
        receptionist/
            ReceptionistClient.java
            controller/ReceptionistController.java
            view/ReceptionistFrame.java
            view/ReceptionistFrame.form
```

Client UI classes use NetBeans-compatible Swing `JFrame` screens with paired `.form` files and controller classes. UI classes must not contain business rules or direct database/storage access.

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
