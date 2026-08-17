package brightcare.security;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.rmi.server.RMIClientSocketFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class SSLConfig {

    // RMI SSL SETTINGS
    // Controls whether RMI uses SSL/TLS.

    // JVM property used to enable/disable SSL for RMI.
    // Example: -Dbrightcare.rmi.ssl=true
    public static final String RMI_SSL_PROPERTY = "brightcare.rmi.ssl";

    // JVM property controlling whether certificate hostname/IP checking
    // is relaxed. Intended for development/testing.
    public static final String RMI_RELAXED_HOST_CHECK_PROPERTY =
            "brightcare.rmi.relaxedHostCheck";


    // BRIGHTCARE SSL FILE LOCATION
    // Controls where BrightCare looks for its SSL files.

    // Optional JVM property for overriding the default SSL directory.
    // Example: -Dbrightcare.ssl.dir=C:\BrightCare\ssl
    public static final String SSL_DIRECTORY_PROPERTY =
            "brightcare.ssl.dir";


    // SERVER KEYSTORE SETTINGS
    // The keystore contains the SERVER'S certificate/private key.
    // Used by the server to prove its identity to clients.

    // Java property containing the path to the server keystore.
    public static final String KEY_STORE_PROPERTY =
            "javax.net.ssl.keyStore";

    // Java property containing the server keystore password.
    public static final String KEY_STORE_PASSWORD_PROPERTY =
            "javax.net.ssl.keyStorePassword";

    // Java property specifying the keystore format, e.g. PKCS12.
    public static final String KEY_STORE_TYPE_PROPERTY =
            "javax.net.ssl.keyStoreType";


    // CLIENT TRUSTSTORE SETTINGS
    // The truststore contains certificates that the CLIENT trusts.
    // Used by clients to verify the RMI server.

    // Java property containing the path to the client truststore.
    public static final String TRUST_STORE_PROPERTY =
            "javax.net.ssl.trustStore";

    // Java property containing the client truststore password.
    public static final String TRUST_STORE_PASSWORD_PROPERTY =
            "javax.net.ssl.trustStorePassword";

    // Java property specifying the truststore format, e.g. PKCS12.
    public static final String TRUST_STORE_TYPE_PROPERTY =
            "javax.net.ssl.trustStoreType";


    // DEFAULT BRIGHTCARE SSL VALUES
    // Used automatically when no custom JVM properties are supplied.

    // Default folder containing the SSL certificate stores.
    public static final String DEFAULT_SSL_DIRECTORY =
            "config/ssl";

    // Default server keystore filename.
    // Contains the server's certificate/private key.
    public static final String DEFAULT_KEY_STORE =
            "brightcare-rmi-keystore.p12";

    // Default client truststore filename.
    // Contains the certificate(s) trusted by the client.
    public static final String DEFAULT_TRUST_STORE =
            "brightcare-rmi-truststore.p12";

    // Default password used to open the development keystore/truststore.
    public static final String DEFAULT_STORE_PASSWORD =
            "brightcare";

    // File format used by both the keystore and truststore.
    public static final String DEFAULT_STORE_TYPE =
            "PKCS12";

    public void configureServer(String keyStorePath, String keyStorePassword) {
        requireText(keyStorePath, "Key store path");
        requireText(keyStorePassword, "Key store password");

        System.setProperty(KEY_STORE_PROPERTY, keyStorePath);
        System.setProperty(KEY_STORE_PASSWORD_PROPERTY, keyStorePassword);
        System.setProperty(KEY_STORE_TYPE_PROPERTY, DEFAULT_STORE_TYPE);
    }

    public void configureClient(String trustStorePath, String trustStorePassword) {
        requireText(trustStorePath, "Trust store path");
        requireText(trustStorePassword, "Trust store password");

        System.setProperty(TRUST_STORE_PROPERTY, trustStorePath);
        System.setProperty(TRUST_STORE_PASSWORD_PROPERTY, trustStorePassword);
        System.setProperty(TRUST_STORE_TYPE_PROPERTY, DEFAULT_STORE_TYPE);
    }

    public void configureMutualTls(String keyStorePath, String keyStorePassword,
            String trustStorePath, String trustStorePassword) {
        configureServer(keyStorePath, keyStorePassword);
        configureClient(trustStorePath, trustStorePassword);
    }

    public boolean isServerConfigured() {
        return hasText(System.getProperty(KEY_STORE_PROPERTY))
                && hasText(System.getProperty(KEY_STORE_PASSWORD_PROPERTY));
    }

    public boolean isClientConfigured() {
        return hasText(System.getProperty(TRUST_STORE_PROPERTY))
                && hasText(System.getProperty(TRUST_STORE_PASSWORD_PROPERTY));
    }

    public void clear() {
        System.clearProperty(RMI_SSL_PROPERTY);
        System.clearProperty(KEY_STORE_PROPERTY);
        System.clearProperty(KEY_STORE_PASSWORD_PROPERTY);
        System.clearProperty(KEY_STORE_TYPE_PROPERTY);
        System.clearProperty(TRUST_STORE_PROPERTY);
        System.clearProperty(TRUST_STORE_PASSWORD_PROPERTY);
        System.clearProperty(TRUST_STORE_TYPE_PROPERTY);
    }

    public static boolean isRmiSslEnabled() {
        return Boolean.parseBoolean(System.getProperty(RMI_SSL_PROPERTY, "true"));
    }

    public static RMIClientSocketFactory rmiClientSocketFactoryIfEnabled() {
        if (!isRmiSslEnabled()) {
            return null;
        }
        configureDefaultStoresIfNeeded();
        if (isRmiRelaxedHostCheckEnabled()) {
            return new RelaxedHostnameSslRMIClientSocketFactory();
        }
        return new SslRMIClientSocketFactory();
    }

    public static SslRMIServerSocketFactory rmiServerSocketFactoryIfEnabled() {
        if (!isRmiSslEnabled()) {
            return null;
        }
        configureDefaultStoresIfNeeded();
        return new SslRMIServerSocketFactory();
    }

    public static void configureDefaultStoresIfNeeded() {
        if (!isRmiSslEnabled()) {
            return;
        }
        if (!hasText(System.getProperty(KEY_STORE_PROPERTY))) {
            System.setProperty(KEY_STORE_PROPERTY, defaultSslPath(DEFAULT_KEY_STORE));
        }
        if (!hasText(System.getProperty(KEY_STORE_PASSWORD_PROPERTY))) {
            System.setProperty(KEY_STORE_PASSWORD_PROPERTY, DEFAULT_STORE_PASSWORD);
        }
        if (!hasText(System.getProperty(KEY_STORE_TYPE_PROPERTY))) {
            System.setProperty(KEY_STORE_TYPE_PROPERTY, DEFAULT_STORE_TYPE);
        }
        if (!hasText(System.getProperty(TRUST_STORE_PROPERTY))) {
            System.setProperty(TRUST_STORE_PROPERTY, defaultSslPath(DEFAULT_TRUST_STORE));
        }
        if (!hasText(System.getProperty(TRUST_STORE_PASSWORD_PROPERTY))) {
            System.setProperty(TRUST_STORE_PASSWORD_PROPERTY, DEFAULT_STORE_PASSWORD);
        }
        if (!hasText(System.getProperty(TRUST_STORE_TYPE_PROPERTY))) {
            System.setProperty(TRUST_STORE_TYPE_PROPERTY, DEFAULT_STORE_TYPE);
        }
    }

    public static boolean isRmiRelaxedHostCheckEnabled() {
        return Boolean.parseBoolean(System.getProperty(RMI_RELAXED_HOST_CHECK_PROPERTY, "true"));
    }

    public static String defaultSslPath(String fileName) {
        String configuredDirectory = System.getProperty(SSL_DIRECTORY_PROPERTY);
        File directory = hasText(configuredDirectory)
                ? new File(configuredDirectory)
                : new File(DEFAULT_SSL_DIRECTORY);
        if (!directory.isAbsolute()) {
            directory = new File(System.getProperty("user.dir"), directory.getPath());
        }
        return new File(directory, fileName).getAbsolutePath();
    }

    public static void applyTrustAllForDevelopment() {
        try {
            TrustManager[] trustAllManagers = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certificates, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certificates, String authType) {
                    }
                }
            };

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAllManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HostnameVerifier verifier = new HostnameVerifier() {
                public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(verifier);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to apply development SSL trust configuration.", ex);
        }
    }

    private void requireText(String value, String label) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    public static class RelaxedHostnameSslRMIClientSocketFactory
            implements RMIClientSocketFactory, Serializable {
        private static final long serialVersionUID = 1L;

        public Socket createSocket(String host, int port) throws IOException {
            configureDefaultStoresIfNeeded();
            SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
            SSLParameters parameters = socket.getSSLParameters();
            parameters.setEndpointIdentificationAlgorithm(null);
            socket.setSSLParameters(parameters);
            return socket;
        }

        public boolean equals(Object other) {
            return other instanceof RelaxedHostnameSslRMIClientSocketFactory;
        }

        public int hashCode() {
            return RelaxedHostnameSslRMIClientSocketFactory.class.hashCode();
        }
    }
}
