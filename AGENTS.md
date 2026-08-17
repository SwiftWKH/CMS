# AGENTS.md

## Purpose

This file defines the working rules for Codex while developing Kai Hin's part of the BrightCare Clinic Management System.

This repository is a temporary development workspace. It does not represent the entire group project. Code from this workspace will later be integrated into a shared NetBeans Java project.

## Kai Hin Scope

Generate code only for:

```text
AuthService
PermissionChecker
SessionManager
ReportService
SSL/TLS utilities
Admin module logic
Security DTOs
Authentication helpers
Report generation helpers
Admin-related utilities
```

Do not generate code for:

```text
Patient UI
Doctor UI
Receptionist UI
PatientService
DoctorService
ReceptionistService
DAO implementations
Database schema
External API implementation
```

Those areas belong to other team members unless the user explicitly asks otherwise.

Exception: project-wide UI skeletons may be created when the user requests structure changes. For out-of-scope modules, create only empty or minimal compilable NetBeans/Swing shells such as `*Client`, `controller/*Controller`, and paired `view/*Frame.java` plus `view/*Frame.form`. Do not add module-specific UI behavior or business logic for other members.

## Required Architecture Flow

All implementation must preserve this flow:

```text
Client
-> ClinicRemoteInterface
-> ClinicServerImplementation
-> Service
-> DAO
-> External Database API
-> Cloud Database
```

Rules:

1. Clients never communicate directly with storage.
2. Business logic belongs inside services.
3. DAO classes handle persistence and external API communication.
4. Reports are generated dynamically and are not persisted.
5. Security checks occur in the application layer.
6. Concurrency handling occurs in the application layer.

Do not invent an alternative architecture.

## NetBeans Compatibility

All generated code must be easy to copy into a standard NetBeans Java project.

Do not use:

```text
Spring Boot
Hibernate
Lombok
Gradle-specific features
Maven-specific plugins
Dependency injection frameworks
Java modules
Reflection-heavy solutions
```

Prefer:

```text
Plain Java
Java RMI
Serializable classes
Standard collections
Simple package structures
Explicit constructors
Explicit getters and setters
```

For GUI structure, prefer plain Swing classes compatible with NetBeans:

```text
JFrame screens in view packages
Paired .java and .form files for NetBeans GUI Builder screens
Controller classes in controller packages
No business logic inside JFrame classes
No direct database/storage access from UI classes
No direct client calls to server-side service classes
```

Shared login/navigation/session screens belong under:

```text
src/brightcare/client/common/
```

Client-to-server adapter interfaces and RMI implementations belong under:

```text
src/brightcare/client/gateway/
```

Gateways may call `ClinicRemoteInterface`. Swing frames and controllers must not instantiate server-side services directly.

## Shared Artifacts

Assume these shared model classes exist:

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
Report
```

Do not regenerate duplicate versions unless explicitly requested.

Assume the shared remote contract exists:

```text
ClinicRemoteInterface
```

All remote methods use:

```java
throws RemoteException
```

Do not modify method signatures unless explicitly requested.

## User ID And Role ID Rule

Treat login/account IDs and role-record IDs as separate concepts:

```text
UserAccount.userId      Authentication/session identity
UserAccount.roleId      Role-record identity supplied by /user.role_id
Patient.patientId       Patient module identity
Doctor.doctorId         Doctor module identity
```

Do not blindly pass `UserAccount.userId` into patient or doctor operations. First resolve the role record ID from the API payload. The current API supplies this as `/user.role_id`; use that for Patient and Doctor routing while keeping `userId` for authentication, session tracking, and logout.

Temporary compatibility fallback is allowed only when `role_id` is missing:

```text
P2 -> patientID 2
D3 -> doctorID 3
```

This fallback must remain easy to remove and must not override an explicit `role_id`.

## Contract Table Rule

`Contract Table.xlsx` is the source of truth for:

```text
Team responsibility
Shared model fields
Java data types
ClinicRemoteInterface method names
Method parameters
Method return types
```

Before editing shared models or remote-facing methods, check the contract table. If older docs conflict with the spreadsheet, follow the spreadsheet and update the docs/code to match.

Do not add fields to shared model classes unless the field exists in `Contract Table.xlsx` or the user explicitly confirms a team-approved contract change.

## Kai Hin Responsibilities

### Authentication

Primary class:

```text
AuthService
```

Responsibilities:

```text
Login
Logout
Credential validation
Session creation
Session termination
```

### Authorization

Primary class:

```text
PermissionChecker
```

Responsibilities:

```text
Role validation
Permission checking
Access control
```

Supported roles:

```text
ADMIN
DOCTOR
RECEPTIONIST
PATIENT
```

### Session Management

Primary class:

```text
SessionManager
```

Responsibilities:

```text
Active sessions
Session expiry
Session validation
```

Keep the implementation lightweight and understandable for an academic project.

### Reporting

Primary class:

```text
ReportService
```

Responsibilities:

```text
Monthly appointment reports
Doctor consultation reports
Patient visit summaries
System statistics
```

Reports are generated, returned, and displayed. Reports are not persisted, stored, or versioned.

Do not create:

```text
ReportDAO
ReportRepository
ReportTable
```

unless explicitly instructed.

### SSL/TLS Support

Primary class:

```text
SSLConfig
```

Keep SSL/TLS utilities practical for Java RMI and suitable for a university project.

## Expected Package Focus

Kai Hin implementation should normally land in:

```text
src/brightcare/service/AuthService.java
src/brightcare/service/ReportService.java
src/brightcare/security/PermissionChecker.java
src/brightcare/security/SessionManager.java
src/brightcare/security/SSLConfig.java
```

## Local Testing and Mock Data

Kai's module may need out-of-scope data to test authentication, authorization, reports, and session behavior before other teammates finish their modules.

Use this separation:

```text
src/        Production source code and teammate handoff code
test/       Optional test harness code
local/      Ignored local scratch files and fake datasets
mock-data/  Ignored local mock API/database payloads
sandbox/    Ignored experiments
```

Rules:

1. Do not put fake database records directly in `src/`.
2. Do not make production classes depend on mock-only classes.
3. Keep local fake payloads in ignored folders.
4. Use constructor parameters or simple interfaces when production services need replaceable data access.
5. Keep production code compatible with real DAO/API collaborators later.
6. Before handoff, verify Kai's export does not include `local/`, `mock-data/`, or `sandbox/`.

## Teammate Export Rule

When the user asks for an export, default to an implementation-only puzzle-piece export unless the user says otherwise.

Export only the code that teammates need to integrate Kai's current piece:

```text
Kai-owned implementation files
NetBeans .java/.form UI pairs for Kai-owned screens
Client controllers and gateways needed by Kai's screens
Minimal shared model, remote, or server skeletons required for compilation
Shared RMI SSL development stores under `config/ssl/`
Minimal NetBeans project recognition files
Minimal Git recognition files
```

Do not include:

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
nbproject/private/
other teammate modules unless they are explicitly part of the integrated component set
```

The export should allow teammates to copy Kai's piece into the shared NetBeans project without working around local-only files, mock data, or unrelated module scaffolding.

Use this zip naming convention:

```text
CMS-[Admin].zip
CMS-[Admin][Receptionist].zip
CMS-[Admin][Receptionist][Doctor][Patient].zip
```

Bracket tags identify which user-facing components are included in that export. Add tags only for components that are actually included and integration-ready.

## Coding Preferences

Prefer:

```text
Simple Java
Readable code
Explicit logic
Clear class responsibilities
Small helper classes only when useful
```

Avoid:

```text
Overengineering
Unnecessary abstractions
Complex design patterns
Framework dependencies
```

When multiple solutions are possible:

1. Choose the option most compatible with NetBeans.
2. Choose the option easiest for teammates to understand.
3. Choose the option easiest to integrate into the final shared project.

## Current Development Priority

1. `AuthService`
2. `PermissionChecker`
3. `SessionManager`
4. `ReportService`
5. `SSLConfig`
6. RMI integration

When uncertain, prioritize Kai Hin's assigned responsibilities over other project components.
