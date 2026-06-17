# Contract Table Reference

`Contract Table.xlsx` is the shared team contract for ownership, data fields, and remote method signatures.

When there is a conflict between older planning notes and `Contract Table.xlsx`, follow `Contract Table.xlsx` and update the docs/code to match.

## Sheets

```text
Responsibility  Team ownership and technical lead scope
Data Type       Shared entity fields, Java types, SQL types, keys, notes
Method          ClinicRemoteInterface method signatures and return types
```

## Kai Hin Scope From Contract

Module:

```text
Admin & Security
```

Owned features:

```text
Authentication
Authorization
Reporting
```

Owned methods:

```text
login(String username, String password) -> UserAccount
logout(int userId) -> boolean
checkPermission(int userId, String requiredRole) -> boolean
generateMonthlyAppointmentReport(int month, int year) -> Report
generateDoctorConsultationReport(int doctorId, int month, int year) -> Report
generatePatientVisitSummary(int patientId) -> Report
viewSystemStatistics() -> String
```

Technical lead scope:

```text
SSL/TLS
RBAC
Authentication flow
Secure communication
```

## Shared Method Rule

All methods in `ClinicRemoteInterface` must include:

```java
throws RemoteException
```

Do not change method names, parameters, or return types without team agreement.

## Client UI Structure Rule

The contract table controls data and method usage. Client UI folders follow the project architecture:

```text
client/<module>/
    <Module>Client.java
    controller/<Module>Controller.java
    view/<Module>Frame.java
    view/<Module>Frame.form
```

JFrame classes should keep their NetBeans `.form` pair and delegate actions to controllers. Controllers should call the RMI contract rather than bypassing the server layer.

Shared screens such as login, navigation, access denied, and session expired belong under `client/common`.

Client gateway classes belong under `client/gateway` and are the boundary between controllers and `ClinicRemoteInterface`.

## Shared Data Rule

Do not add, remove, or rename shared model fields unless the team updates `Contract Table.xlsx`.

The shared entities are:

```text
UserAccount
Patient
Doctor
Appointment
ConsultationNote
Report
```

## Report Contract

The spreadsheet defines `Report` as metadata:

```java
int reportId;
String reportType;
int generatedBy;
LocalDateTime generatedAt;
String filePath;
```

Reports are generated on demand and are not database CRUD objects.
