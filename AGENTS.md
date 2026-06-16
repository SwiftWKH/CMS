# AGENTS.md

## Project Context

This repository is a temporary development workspace for Kai Hin's portion of a Distributed Clinic Management System.

The final implementation will be integrated into a shared NetBeans project with contributions from multiple team members.

This repository does NOT represent the entire system. It represents Kai Hin's assigned responsibilities within the system.

---

# System Overview

Architecture:

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

General responsibilities:

```text
Patient Module
Receptionist Module
Doctor Module
Admin & Security Module
Database Layer
```

Kai Hin owns:

```text
Admin Module
Authentication
Authorization
Reporting
Security
```

Assume other modules are being developed by other team members.

---

# NetBeans Compatibility Requirement

All generated code will eventually be transferred into a NetBeans Java project.

Do NOT use:

```text
Spring Boot
Hibernate
Lombok
Gradle-specific features
Maven-specific plugins
Dependency Injection frameworks
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
Explicit getters/setters
```

Code should be easy to copy into a standard NetBeans project without modification.

---

# Current Repository Scope

Generate code ONLY for:

```text
AuthService
PermissionChecker
SessionManager
ReportService
SSL/TLS Utilities
Admin Module Logic
```

Allowed support classes:

```text
Security DTOs
Authentication helpers
Report generation helpers
Admin-related utilities
```

Do NOT generate:

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

Assume these are owned by other team members.

---

# Shared Architecture Rules

The following flow must always be respected:

```text
Client
→ ClinicRemoteInterface
→ ClinicServerImplementation
→ Service
→ DAO
→ Storage API
→ Cloud Database
```

Rules:

1. Clients must never communicate directly with storage.
2. Business logic belongs inside services.
3. DAO classes handle persistence.
4. Reports are generated dynamically.
5. Security checks occur in the application layer.
6. Concurrency handling occurs in the application layer.

Do not invent alternative architectures.

---

# Shared Model Classes

Assume these classes already exist:

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
Report
```

Do not regenerate them unless explicitly requested.

Do not create duplicate versions.

---

# Shared Remote Contract

Assume the project contains:

```java
ClinicRemoteInterface
```

All remote methods use:

```java
throws RemoteException
```

Do not modify method signatures without explicit instruction.

---

# Kai Hin Responsibilities

## Authentication

Generate components related to:

```text
Login
Logout
Credential Validation
Session Creation
Session Termination
```

Primary class:

```text
AuthService
```

---

## Authorization

Generate components related to:

```text
Role Validation
Permission Checking
Access Control
```

Primary class:

```text
PermissionChecker
```

Supported roles:

```text
ADMIN
DOCTOR
RECEPTIONIST
PATIENT
```

---

## Session Management

Generate components related to:

```text
Active Sessions
Session Expiry
Session Validation
```

Primary class:

```text
SessionManager
```

Keep implementations lightweight and suitable for an academic project.

---

## Reporting

Generate components related to:

```text
Monthly Appointment Reports
Doctor Consultation Reports
Patient Visit Summaries
System Statistics
```

Primary class:

```text
ReportService
```

Reports are:

```text
Generated
Returned
Displayed
```

Reports are NOT:

```text
Persisted
Stored
Versioned
```

Do not create:

```text
ReportDAO
ReportRepository
ReportTable
```

unless explicitly instructed.

---

## Security

Generate components related to:

```text
Authentication
Authorization
SSL/TLS Support
Secure Communication Utilities
```

Keep implementations practical and understandable for a university project.

Avoid enterprise-grade complexity unless specifically requested.

---

# Expected Package Structure

Focus only on the packages relevant to Kai Hin's responsibilities:

```text
service/
├── AuthService.java
└── ReportService.java

security/
├── PermissionChecker.java
├── SessionManager.java
└── SSLConfig.java
```

Other packages may exist in the final project but are outside the scope of this repository.

---

# Coding Preferences

Prefer:

```text
Simple Java
Readable code
Explicit logic
Clear class responsibilities
```

Avoid:

```text
Overengineering
Unnecessary abstractions
Complex design patterns
Framework dependencies
```

If multiple solutions are possible:

1. Choose the solution most compatible with NetBeans.
2. Choose the solution easiest for teammates to understand.
3. Choose the solution easiest to integrate into the final shared project.

---

# Current Development Priority

Priority Order:

```text
1. AuthService
2. PermissionChecker
3. SessionManager
4. ReportService
5. SSL/TLS Utilities
6. RMI Integration
```

When uncertain, prioritize Kai Hin's assigned responsibilities over other project components.
