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
