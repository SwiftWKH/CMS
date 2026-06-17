# Admin Module Responsibilities

Owner: Kai Hin

The Admin module supports clinic oversight and system management. It is not responsible for daily operational tasks such as patient registration, appointment booking, or consultation recording.

## Primary Responsibilities

```text
Authentication
Authorization (RBAC)
Session Management
Report Generation
System Monitoring
SSL/TLS Security
```

## Dashboard Structure

```text
Admin Dashboard
    User Management
        View Users
        Create User
        Disable User
    Reports
        Monthly Appointment Report
        Doctor Consultation Report
        Patient Visit Summary
    System Statistics
    Active Sessions
    Logout
```

## Remote Methods

```java
login(String username, String password)
logout(int userId)
checkPermission(int userId, String requiredRole)
generateMonthlyAppointmentReport(int month, int year)
generateDoctorConsultationReport(int doctorId, int month, int year)
generatePatientVisitSummary(int patientId)
viewSystemStatistics()
```

All remote calls must go through `ClinicRemoteInterface` once RMI integration is available.

## UI Rules

The Admin UI should prioritize functionality over visual complexity.

Recommended Swing components:

```text
JFrame
JPanel
JTable
JButton
JLabel
JTextField
JPasswordField
JComboBox
JTextArea
JTabbedPane
```

UI classes only collect input, call controllers, and display results. Validation, authorization, reporting, session management, and security logic belong in the application layer.

## Contract Notes

`Contract Table.xlsx` remains the source of truth for shared data and method signatures.

User-management and active-session UI screens may exist before remote methods are approved, but their gateway methods must be marked as placeholders until the team updates the contract.
