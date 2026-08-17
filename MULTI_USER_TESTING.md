# BrightCare Multi-User Testing Notes

## Purpose

This package is for multi-machine Java RMI testing with the current hospital API and Derby fallback.

The current role-module persistence adapter is the hospital API by default, with Derby fallback available.

Hospital API:

```text
https://192.168.137.1:7230/hospital
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

The `/user` endpoint is used for login and Admin user management first. Derby `USER_ACCOUNT` remains the fallback when the API is unavailable or a local-only account is needed.

Derby fallback:

```text
jdbc:derby://localhost:1527/BRIGHTCARE_DB
user: app
password: app
```

Run the scripts in order if the database is not already prepared:

```text
database/brightcare_schema.sql
database/brightcare_seed.sql
```

## Test Credentials

```text
admin1 / admin123
doc01  / doctor123
rec01  / receptionist123
pat01  / patient123
doc02  / doctor123
pat02  / patient123
```

## Server Machine

Start the hospital API and keep Derby available as fallback, then run:

```text
brightcare.server.ClinicServer
```

Optional properties:

```text
-Dbrightcare.data.source=api
-Dbrightcare.data.source=derby
-Dbrightcare.api.baseUrl=https://192.168.137.1:7230/hospital
-Dbrightcare.api.trustAll=true
```

Default RMI settings:

```text
service: BrightCareClinicService
port: 1099
transport: Java RMI over TCP
```

Allow TCP port `1099` through the server firewall.

## Client Machines

Run `brightcare.client.common.CommonClient`.

In the Login screen, set:

```text
Server: <server-ip>
```

Example:

```text
Server: 192.168.137.1
```

The Server field is remembered locally with Java Preferences after use.

Optional NetBeans/JVM defaults:

```text
-Dbrightcare.rmi.host=<server-ip>
-Dbrightcare.rmi.port=1099
```

If testing on the same machine, use `localhost`.

## Logs

Check the server log first when remote testing fails:

```text
logs/brightcare.log
```

Useful log evidence:

```text
BrightCare RMI server started
RMI service lookup succeeded
RMI call received. method=...
clientHost=...
Appointment slot lock acquired
Appointment slot lock released
```

## Concurrent Appointment Test

Use two clients at the same time.

Try booking the same slot:

```text
doctor: 1
date: 2026-08-14
time: 09:00
```

Expected result:

```text
Only one booking for a doctor/date/time slot should succeed.
Duplicate or simultaneous booking attempts should be rejected.
```

The seeded `doctor 1 / 2026-08-14 / 09:00` slot is already booked, so it should reject immediately.

## SSL-RMI

SSL-RMI is enabled by default using the shared development stores:

```text
config/ssl/brightcare-rmi-keystore.p12
config/ssl/brightcare-rmi-truststore.p12
```

Default store password: `brightcare`.

To disable SSL-RMI temporarily, run both server and clients with `-Dbrightcare.rmi.ssl=false`.

For demo compatibility, SSL-RMI relaxed host checking is enabled by default:

```text
-Dbrightcare.rmi.relaxedHostCheck=true
```

This avoids regenerating keys when the server laptop receives a different LAN/hotspot IP. The connection still uses SSL-RMI and the shared truststore. For stricter certificate hostname/IP validation, set `-Dbrightcare.rmi.relaxedHostCheck=false`.

All server and client processes must use the same SSL mode. Mixing SSL and non-SSL RMI will fail lookup.

## UDP

UDP is not included in this test package. TCP is covered through Java RMI.
