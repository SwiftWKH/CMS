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

## Local Derby Integration Bypass

Until the final external database API is available, the project may run against a local NetBeans/Derby database:

```text
jdbc:derby://localhost:1527/BRIGHTCARE_DB
user: app
password: app
```

Setup scripts:

```text
database/brightcare_schema.sql
database/brightcare_seed.sql
```

This Derby path is a temporary integration adapter in the DAO layer. It does not change the target architecture. UI, controllers, RMI gateways, server implementation, and services should remain unchanged when the final API arrives; only DAO/provider implementations should be swapped.

Seed credentials:

```text
admin1 / admin123
doc01  / doctor123
rec01  / receptionist123
pat01  / patient123
doc02  / doctor123
pat02  / patient123
```

Generated report text files are local runtime output and belong under:

```text
generated-reports/
```

Do not export Derby runtime files, generated reports, local logs, or build output as final teammate deliverables.

## Hospital API Data Source

The final hospital API endpoint currently tested is:

```text
https://192.168.137.1:7230/hospital
```

Verified read endpoints:

```text
/doctor
/user
/patient
/appointment
/consultation
/appwcon
```

Runtime data source selection:

```text
Default: API first, Derby fallback
-Dbrightcare.data.source=api
-Dbrightcare.data.source=derby
-Dbrightcare.api.baseUrl=https://192.168.137.1:7230/hospital
-Dbrightcare.api.trustAll=true
```

The current API exposes `/user` for user-account data. Authentication and Admin user listing/creation/disable use `/user` first, with local Derby `USER_ACCOUNT` as fallback when the API is unavailable or does not return a matching account. No separate `/auth` or `/login` endpoint is currently used.

User identity and role-record identity must be treated as separate values:

```text
user_id      Login/account identity from /user
role_id      Role-record identity supplied by /user for the logged-in role
patientID    Patient role-record identity from /patient
doctorID     Doctor role-record identity from /doctor
```

Current final mapping: `/user.role_id` is used to open patient and doctor modules with the correct role-record ID. `user_id` remains the authentication/session/logout ID. Username inference such as `P2 -> patientID 2` and `D3 -> doctorID 3` remains as a legacy compatibility fallback only when `role_id` is missing.

The live API uses these field names:

```text
doctorID, doctorName, special, contextNumber
user_id, username, password_hash, role, role_id, status
patientID, patientName, patientContactNumber
appointmentID, appointmentDate, appointmentTime, stage
consultationID, createAT
```

## Network, Logging, And Security Runtime

The active distributed transport is Java RMI over TCP.

Server:

```text
Run brightcare.server.ClinicServer
Default RMI port: 1099
Default service: BrightCareClinicService
```

Client on another machine:

```text
Run brightcare.client.common.CommonClient
Enter the server machine IP address in the Login screen Server field
Example: 192.168.137.1
```

Optional NetBeans/JVM defaults:

```text
-Dbrightcare.rmi.host=<server-ip>
-Dbrightcare.rmi.port=1099
```

The Login screen Server field is the preferred teammate workflow. It is remembered on each user's machine with Java Preferences, so teammates do not need to keep editing NetBeans Run properties after the first successful setup.

Runtime logs are written to:

```text
logs/brightcare.log
```

The logs include RMI lookup attempts, server startup details, login results, remote method calls, client host when available, and appointment slot lock activity. This is the first place to check during multi-laptop testing.

SSL-RMI is enabled by default for the shared NetBeans project.

The development keystore/truststore are included under:

```text
config/ssl/
```

Default store password:

```text
brightcare
```

`SSLConfig` automatically loads these stores. To temporarily disable SSL-RMI for troubleshooting, run both server and clients with:

```text
-Dbrightcare.rmi.ssl=false
```

For multi-laptop demos, SSL-RMI uses relaxed host checking by default:

```text
-Dbrightcare.rmi.relaxedHostCheck=true
```

This keeps the SSL-RMI transport encrypted and trusted by the shared truststore, but it does not reject the connection only because the server laptop's hotspot/LAN IP changed. To return to stricter certificate hostname/IP validation, run both client and server with:

```text
-Dbrightcare.rmi.relaxedHostCheck=false
```

All server and client processes must use the same SSL mode. Mixing SSL and non-SSL RMI will fail lookup.

UDP is not part of the current implementation. Keep UDP as a backlog item unless the assignment rubric explicitly requires it.

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
