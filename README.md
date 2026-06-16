# BrightCare Clinic Management System

Distributed Computing group assignment workspace.

This repository is currently used for Kai Hin's assigned part of the system:

```text
Authentication
Authorization
Session management
Reporting
SSL/TLS support
Admin module logic
```

The final implementation is expected to be integrated into a shared NetBeans Java project.

## Document Map

```text
README.md           Project entry point and repository guide
AGENTS.md           Working rules for Codex and Kai Hin's implementation scope
ARCHITECTURE.md     Full system architecture, package structure, and method contract
WORKFLOW.md         Team ownership, Git process, and integration checklist
PROJECT_SUMMARY.md  Compact development baseline
```

## Architecture

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

Clients must never access the database or external storage API directly.

## Current Package Skeleton

```text
src/brightcare/
    model/
    remote/
    server/
    service/
    dao/
    security/
    concurrency/
    client/
        patient/
        receptionist/
        doctor/
        admin/
```

## IDE Setup

Open this repository root directly in both tools:

```text
NetBeans: Open Project -> this CMS folder
VS Code:  Open Folder -> this CMS folder
```

The NetBeans Ant project files live at the repository root:

```text
build.xml
manifest.mf
nbproject/
```

Do not create or open a second nested NetBeans project inside this folder.

## Kai Hin Development Priority

1. `AuthService`
2. `PermissionChecker`
3. `SessionManager`
4. `ReportService`
5. `SSLConfig`
6. RMI integration points

## Compatibility Rules

Use plain Java suitable for NetBeans.

Do not use:

```text
Spring Boot
Hibernate
Lombok
Gradle-specific features
Maven-specific plugins
Dependency injection frameworks
Java modules
Reflection-heavy designs
```

Prefer explicit constructors, getters, setters, standard collections, and simple package structures.
