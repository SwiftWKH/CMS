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
CONTRACT.md         Extracted rules from Contract Table.xlsx
ADMIN_MODULE.md     Admin module responsibilities, dashboard, and UI rules
AGENTS.md           Working rules for Codex and Kai Hin's implementation scope
ARCHITECTURE.md     Full system architecture, package structure, and method contract
WORKFLOW.md         Team ownership, Git process, and integration checklist
PROJECT_SUMMARY.md  Compact development baseline
```

`Contract Table.xlsx` is the shared team contract for data fields and method signatures. If the contract conflicts with older notes, follow the spreadsheet and update the docs/code to match.

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
        gateway/
        common/
            controller/
            view/
        patient/
            controller/
            view/
        receptionist/
            controller/
            view/
        doctor/
            controller/
            view/
        admin/
            controller/
            view/
```

Client modules use a NetBeans/Swing structure:

```text
*Client.java       Small launcher class
controller/*.java Controller shell for UI-to-RMI coordination
view/*Frame.java  JFrame logic shell for NetBeans Swing screens
view/*Frame.form  NetBeans GUI Builder metadata paired with the JFrame
```

NetBeans JFrame screens must be kept as paired `.java` and `.form` files with the same base name. UI classes must call RMI methods through controllers and must not contain business logic.

Shared, non-role-specific screens live in `client/common`, such as login, main menu/navigation, access denied, and session expired screens.

Client controllers must not directly construct server-side services. They should call `ClinicRemoteInterface` or a small gateway interface that can later be backed by RMI.

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

## Local Testing With Mock Data

Keep production/export code and local-only test data separate:

```text
src/        Production source code. This is the real deliverable.
test/       Optional test harness code. NetBeans treats this as test source.
local/      Ignored local scratch files and fake datasets.
mock-data/  Ignored local mock API/database payloads.
sandbox/    Ignored experiments.
```

Rules:

1. Do not put fake database records directly in `src/`.
2. Do not make production classes depend on local mock classes.
3. Use constructor parameters or simple interfaces when a service needs data access.
4. Keep disposable mock payloads in `local/`, `mock-data/`, or `sandbox/`.
5. Export from Git-tracked files or the NetBeans build output, not from local mock folders.

The ignored local folders allow Kai's module to be tested with fake data without those files becoming part of the teammate handoff.

## Export Handoff

Exports should be implementation-only puzzle pieces, not full workspace zips.

The default Kai handoff includes only admin/security/auth/report implementation, required NetBeans `.java/.form` pairs, controllers/gateways, minimal shared skeletons needed to compile, and minimal NetBeans/Git recognition files.

Do not include docs, diagrams, spreadsheets, tests, mock/local folders, build output, or unrelated teammate modules unless they are explicitly integrated.

Zip names use bracketed component tags:

```text
CMS-[Admin].zip
CMS-[Admin][Receptionist].zip
CMS-[Admin][Receptionist][Doctor][Patient].zip
```

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
