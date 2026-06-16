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
[ ] Method signatures match the contract
[ ] Architecture flow is preserved
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
