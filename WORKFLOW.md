# WORKFLOW.md

## Development Philosophy

Build independently. Integrate later.

All development must follow the architecture and method contract in `ARCHITECTURE.md`.

## Shared Artifacts

These files are shared by the whole team:

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
Report
ClinicRemoteInterface
```

Changes to these artifacts must be communicated to the team before integration.

## Contract Table

`Contract Table.xlsx` is the team source of truth for:

```text
Ownership
Shared data fields
Method signatures
Return types
Remote interface expectations
```

Before merge:

```text
[ ] Shared model fields match Contract Table.xlsx
[ ] Method names, parameters, and return types match Contract Table.xlsx
[ ] Any contract changes were communicated to the team
```

## Ownership

| Member | Ownership |
| --- | --- |
| Tiong | DAO layer, database integration, external API integration |
| Leon | Patient module, patient UI, RMI contract maintenance |
| Amir | Doctor module, concurrency |
| Kai | Authentication, authorization, reporting, security, session management, SSL/TLS |
| Chen | Receptionist module, testing, integration validation |

## Git Rules

Before pushing:

1. Pull the latest changes.
2. Verify the project compiles.
3. Do not modify another member's module unless agreed.
4. Do not modify shared models without team notification.
5. Do not modify `ClinicRemoteInterface` without team notification.

## Branch Strategy

Recommended branch layout:

```text
main
    kai
    tiong
    leon
    amir
    chen
```

Each member develops on their own branch. Merge into `main` only after testing.

## Integration Order

1. Shared models
2. `ClinicRemoteInterface`
3. DAO layer
4. Services
5. Client modules
6. Security
7. Concurrency
8. Final integration

## Integration Checklist

Before merge:

```text
[ ] Compiles successfully
[ ] No duplicate model classes
[ ] No duplicate remote interfaces
[ ] No direct database access from clients
[ ] No business logic in GUI classes
[ ] JFrame screens delegate to controller classes
[ ] Client controllers do not directly construct server-side service classes
[ ] Client gateway classes are the only client-side RMI adapters
[ ] Method signatures match the contract
[ ] Architecture flow is preserved
[ ] Multi-machine RMI host/port settings are documented for testers
[ ] logs/brightcare.log is checked when remote lookup or server calls fail
[ ] Appointment booking conflict behavior is tested with at least two clients
```

Required architecture flow:

```text
Client
-> RMI
-> Service
-> DAO
-> External API
-> Database
```

## NetBeans UI Structure

Each client module should follow the same Swing package shape:

```text
client/<module>/
    <Module>Client.java
    controller/
        <Module>Controller.java
    view/
        <Module>Frame.java
        <Module>Frame.form
```

JFrame classes are for UI only. NetBeans GUI Builder screens must keep the `.java` and `.form` pair with the same base name. Controllers coordinate UI events and RMI calls. Business rules stay on the server side.

Client gateway classes live in `client/gateway`. They adapt controllers to `ClinicRemoteInterface` and keep RMI details out of Swing frames.

Shared screens use:

```text
client/common/
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
```

## Local Mock Data Workflow

Kai may use fake out-of-scope data while developing authentication, authorization, reporting, session management, and security features.

Use this rule:

```text
Tracked and exported: src/, docs, NetBeans project files
Optional tracked tests: test/
Ignored local data: local/, mock-data/, sandbox/
```

Before sharing or merging:

```text
[ ] No fake records are hardcoded into production services
[ ] No production class imports mock-only classes
[ ] No local mock payloads are included in the handoff
[ ] Services can later swap mock collaborators for real DAO/API collaborators
```

## Local Derby Integration Workflow

Derby is allowed as a temporary integration bypass while the final external database API is unavailable.

Use:

```text
database/brightcare_schema.sql
database/brightcare_seed.sql
jdbc:derby://localhost:1527/BRIGHTCARE_DB
app / app
```

Rules:

```text
[ ] Derby access remains inside DAO/provider classes only
[ ] Clients never access Derby directly
[ ] Services keep calling DAOs, not JDBC
[ ] Reports are generated as runtime files, not persisted
[ ] generated-reports/, logs/, build/, and dist/ stay out of exports
[ ] The final API swap should replace DAO/provider internals, not UI/controller/RMI flow
```

Seed login credentials:

```text
admin1 / admin123
doc01  / doctor123
rec01  / receptionist123
pat01  / patient123
doc02  / doctor123
pat02  / patient123
```

## Hospital API Workflow

The current API base URL is:

```text
https://192.168.137.1:7230/hospital
```

Use API mode:

```text
-Dbrightcare.data.source=api
-Dbrightcare.api.baseUrl=https://192.168.137.1:7230/hospital
-Dbrightcare.api.trustAll=true
```

Use Derby-only fallback:

```text
-Dbrightcare.data.source=derby
```

Verified API endpoints:

```text
/doctor
/user
/patient
/appointment
/consultation
/appwcon
```

Authentication and Admin user management now use `/user` first. Keep Derby available as a local fallback for unavailable API runs or unmatched local admin accounts. No separate `/auth` or `/login` endpoint is currently used.

## Multi-Machine Test Workflow

When teammates are ready to test on separate machines:

```text
1. Start Derby/final database access on the server machine.
2. Run brightcare.server.ClinicServer on the server machine.
3. Confirm firewall allows TCP port 1099.
4. On each client machine, run CommonClient with:
   -Dbrightcare.rmi.host=<server-ip>
   -Dbrightcare.rmi.port=1099
5. Watch logs/brightcare.log on the server.
```

Expected log evidence:

```text
RMI server started
RMI service lookup succeeded
RMI call received. method=...
clientHost=...
Appointment slot lock acquired/released
```

For same-slot concurrency testing, have two clients attempt to book the same doctor/date/time. One should succeed and the other should fail with the slot already booked or currently being booked.

## SSL/TLS Workflow

SSL-RMI is optional and off by default.

Only enable after plain RMI works:

```text
-Dbrightcare.rmi.ssl=true
-Djavax.net.ssl.keyStore=<server-keystore>
-Djavax.net.ssl.keyStorePassword=<password>
-Djavax.net.ssl.trustStore=<client-truststore>
-Djavax.net.ssl.trustStorePassword=<password>
```

All server and client processes must agree on SSL mode. Mixing SSL and non-SSL RMI will fail lookup.

## TCP/UDP Note

TCP is provided through Java RMI. UDP remains backlog and is not needed unless the assignment rubric explicitly asks for a UDP feature or demonstration.

## Export Workflow

Exports are puzzle-piece handoffs, not full workspace snapshots.

Default export contents:

```text
Implementation files for the included component tags
NetBeans .java/.form pairs for included UI screens
Required controllers and RMI gateway adapters
Minimal shared model/remote/server skeletons needed to compile
build.xml
manifest.mf
nbproject/ without nbproject/private/
.gitignore and .gitattributes
```

Always exclude:

```text
Documentation files
Diagrams and images
Spreadsheets
test/
local/
mock-data/
sandbox/
build/
dist/
.class files
.git/
nbproject/private/
Unincluded teammate modules
```

Use bracketed component tags in the zip name:

```text
CMS-[Admin].zip
CMS-[Admin][Receptionist].zip
CMS-[Admin][Receptionist][Doctor][Patient].zip
```

For Kai's current standalone handoff, use:

```text
CMS-[Admin].zip
```

When teammate modules are integrated later, add their tags only after their source files are intentionally included in the export.
