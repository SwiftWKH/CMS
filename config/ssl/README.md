# BrightCare RMI SSL Development Stores

These files are shared development SSL materials for the university project.

They let every teammate run Java RMI over SSL/TLS without generating their own keystore first.

Files:

```text
brightcare-rmi-keystore.p12    Server private key and certificate
brightcare-rmi-truststore.p12  Client trusted certificate
brightcare-rmi.cer             Exported public certificate
```

Default password:

```text
brightcare
```

`SSLConfig` enables RMI SSL by default and automatically uses these files from `config/ssl`.

To disable RMI SSL for troubleshooting:

```text
-Dbrightcare.rmi.ssl=false
```

These files are for local/team testing only. Do not treat them as production secrets.
