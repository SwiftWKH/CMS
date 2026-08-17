# BrightCare Codebase Explanation

This document explains how the current BrightCare Clinic Management System codebase is organized and how the main runtime flows connect. It is written as a practical walkthrough for teammates, testing, and presentation preparation.

## System Purpose

BrightCare is a Java NetBeans Swing clinic management system using Java RMI between clients and a central server. The server owns business logic and persistence access. The clients are desktop UI modules for different user roles.

Supported roles:

```text
ADMIN
DOCTOR
RECEPTIONIST
PATIENT
```

The intended architecture is:

```text
Swing Client UI
-> Client Controller
-> Client Gateway
-> ClinicRemoteInterface
-> ClinicServerImplementation
-> Service
-> DAO
-> External Hospital API
-> Cloud Database
```

Clients should not access the database or HTTP API directly.

## Project Shape

Main production code lives under:

```text
src/brightcare/
```

Important packages:

```text
client/       Swing UI, controllers, and client-side gateways
remote/       Shared RMI interface
server/       RMI server launcher and remote implementation
service/      Business logic layer
dao/          Derby/API persistence layer
model/        Shared serializable DTO/model classes
security/     Auth, session, permission, and SSL helpers
concurrency/  Appointment locking support
util/         Shared logging
```

NetBeans project files live at the root:

```text
build.xml
manifest.mf
nbproject/
```

## Client Layer

Client code lives in:

```text
src/brightcare/client/
```

Each role module follows the same general structure:

```text
RoleClient.java
controller/RoleController.java
view/RoleFrame.java
view/RoleFrame.form
```

The `.form` files are NetBeans GUI Builder metadata. They must stay paired with their matching `.java` frame files.

### Common Client

Common login/navigation code lives in:

```text
src/brightcare/client/common/
```

Key files:

```text
CommonClient.java
LoginController.java
NavigationController.java
LoginFrame.java
LoginFrame.form
```

`CommonClient` starts the application at the login screen. The login screen can connect to an RMI host, authenticate a user, then route the user based on role.

### Gateways

Client gateway classes live in:

```text
src/brightcare/client/gateway/
```

Gateway interfaces hide the remote call details from Swing frames:

```text
AuthenticationGateway
AdminGateway
DoctorGateway
PatientGateway
ReceptionistGateway
```

RMI-backed implementations call `ClinicRemoteInterface`:

```text
RmiAuthenticationGateway
RmiAdminGateway
RmiDoctorGateway
RmiPatientGateway
RmiReceptionistGateway
```

Unavailable gateway classes provide safe fallback behavior when a remote connection is missing. They are not intended to replace the real server during final testing.

## RMI Contract

The shared remote interface is:

```text
src/brightcare/remote/ClinicRemoteInterface.java
```

This is the contract between clients and server. Any method added here requires both sides to be recompiled and the running server/client processes restarted.

Examples of remote responsibilities:

```text
login / logout
view users / create users / update users / disable users
register patients / update patient details
create / modify / cancel appointments
view appointment schedules
view doctors
save consultation notes
generate reports
view active sessions and session history
```

## Server Layer

Server code lives in:

```text
src/brightcare/server/
```

Key files:

```text
ClinicServer.java
ClinicServerImplementation.java
```

`ClinicServer` starts the RMI registry/service binding. `ClinicServerImplementation` implements `ClinicRemoteInterface` and delegates work into the service layer.

The server is the central point for:

```text
Authentication
Role checks
Session tracking
Report generation
Appointment operations
Patient, doctor, receptionist, and admin workflows
```

## Service Layer

Services live in:

```text
src/brightcare/service/
```

Important services:

```text
AuthService.java
AdminService.java
PatientService.java
ReceptionistService.java
DoctorService.java
ReportService.java
```

Services contain business rules and should be the main place for validation, authorization, and coordination. UI classes should not perform server-side business logic.

Examples:

```text
AuthService       Validates login credentials and creates sessions
AdminService      Handles user management operations
PatientService    Handles profile and booking actions
ReceptionistService Handles patient registration and appointment management
DoctorService     Handles appointment lists, history, and consultation notes
ReportService     Generates dynamic reports
```

## DAO And Data Access

DAO classes live in:

```text
src/brightcare/dao/
```

Main DAO files:

```text
UserAccountDAO.java
PatientDAO.java
DoctorDAO.java
AppointmentDAO.java
ConsultationNoteDAO.java
HospitalApiClient.java
HospitalJsonMapper.java
DataSourceConfig.java
DerbyConnectionFactory.java
```

The current data strategy supports the external hospital API as the preferred data source, with Derby as a local fallback where implemented.

Important rule:

```text
Client UI must not call DAO or HTTP API classes directly.
```

### External API Mapping

`HospitalApiClient` performs HTTP requests.

`HospitalJsonMapper` translates API JSON into Java model objects and translates model objects back into API payloads.

Current API field examples:

```text
doctorID
doctorName
special
patientID
patientName
appointmentID
role_id
```

The code treats role IDs and user IDs separately. For example, appointment booking needs `doctorID`, not the login `user_id`.

## Model Layer

Shared models live in:

```text
src/brightcare/model/
```

Main models:

```text
UserAccount
UserProfileInput
Patient
Doctor
Appointment
ConsultationNote
Report
ActiveSessionInfo
```

These classes are simple serializable data carriers used across RMI.

Important identity distinction:

```text
UserAccount.userId  Login/session identity
UserAccount.roleId  Linked role table ID
Patient.patientId   Patient table identity
Doctor.doctorId     Doctor table identity
```

Do not blindly use `userId` as `patientId` or `doctorId`.

## Security And Sessions

Security code lives in:

```text
src/brightcare/security/
```

Key files:

```text
PermissionChecker.java
SessionManager.java
SSLConfig.java
```

Responsibilities:

```text
PermissionChecker  Role-based access checks
SessionManager     Active session and session history tracking
SSLConfig          RMI SSL/TLS setup and keystore handling
```

The system supports SSL/TLS for RMI. Demo-friendly SSL behavior is configured so teammates can connect without manually regenerating keys for every IP change.

## Concurrency

Concurrency support lives in:

```text
src/brightcare/concurrency/AppointmentLockManager.java
```

This is used to protect appointment-related operations from conflicting simultaneous access during multi-user testing.

## Role Module Summary

### Admin

Package:

```text
src/brightcare/client/admin/
```

Admin responsibilities:

```text
User management
Reports
System statistics
Active sessions
Session history
Logout
```

User management is consolidated into a single admin page for viewing, creating, updating, and disabling users.

Reports are generated dynamically and are not persisted.

### Receptionist

Package:

```text
src/brightcare/client/receptionist/
```

Receptionist responsibilities:

```text
Patient registration
Patient update
Appointment creation
Appointment modification/cancellation
Daily schedule viewing/filtering
Logout
```

Appointment Management uses separate tabs:

```text
Create Appointment  Uses patient and doctor dropdowns
Update / Cancel     Uses table selection to autofill editable fields
```

### Patient

Package:

```text
src/brightcare/client/patient/
```

Patient responsibilities:

```text
Profile management
Appointment booking
Appointment cancellation
Active schedule
History logs
Logout
```

The appointment page uses a doctor dropdown loaded from real doctor records. This avoids typing invalid doctor IDs that would fail database foreign-key checks.

### Doctor

Package:

```text
src/brightcare/client/doctor/
```

Doctor responsibilities:

```text
View own appointments
View patient medical history
Create consultation notes
Logout
```

The consultation note page includes an appointment table at the top. Selecting a row autofills:

```text
Appointment ID
Patient ID
Doctor ID
```

The ID fields are read-only to reduce mismatched consultation submissions.

## Startup Flow

Typical demo flow:

```text
1. Start ClinicServer.
2. Start CommonClient.
3. Enter server host in LoginFrame if testing over LAN.
4. Login with a user account.
5. NavigationController opens the correct role frame.
```

For LAN testing, the client must point to the server machine IP/host. The RMI host can be configured through the login screen rather than editing VM arguments manually.

## Logging

Logging helper:

```text
src/brightcare/util/BrightCareLogger.java
```

Logs are written under:

```text
logs/
```

Logs are useful for tracing:

```text
RMI lookup
Login attempts
Remote method calls
API/database failures
Doctor appointment loading
Consultation note saves
```

`logs/` should not be included in teammate exports.

## Export Rules

Exports are implementation snapshots for teammate integration.

Include:

```text
src/
nbproject/ without private/
build.xml
manifest.mf
.gitignore
.gitattributes
config/ssl keystore/certificate files when needed
```

Exclude:

```text
docs
test/
import/
build/
dist/
logs/
generated-reports/
nbproject/private/
mock-data/
sandbox/
local/
```

Current integrated export naming pattern:

```text
CMS-[Admin][Receptionist][Doctor][Patient]-Snapshot-YYYYMMDD-HHMMSS.zip
```

## Common Development Notes

When changing `ClinicRemoteInterface`, always recompile and restart both server and client.

When editing Swing screens, keep matching `.java` and `.form` files together.

When API data looks wrong, compare role table IDs against user table `role_id` values before changing frontend logic.

When an appointment or consultation fails, check whether the submitted `patientID`, `doctorID`, or `appointmentID` actually exists in the external API/database.

When a UI button appears missing on another machine, first check screen size/layout scaling. A component may be pushed off-screen rather than absent from code.
