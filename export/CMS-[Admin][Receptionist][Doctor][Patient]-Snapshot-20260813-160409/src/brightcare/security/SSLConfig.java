package brightcare.security;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class SSLConfig {
    public static final String RMI_SSL_PROPERTY = "brightcare.rmi.ssl";
    public static final String KEY_STORE_PROPERTY = "javax.net.ssl.keyStore";
    public static final String KEY_STORE_PASSWORD_PROPERTY = "javax.net.ssl.keyStorePassword";
    public static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";
    public static final String TRUST_STORE_PASSWORD_PROPERTY = "javax.net.ssl.trustStorePassword";

    public void configureServer(String keyStorePath, String keyStorePassword) {
        requireText(keyStorePath, "Key store path");
        requireText(keyStorePassword, "Key store password");

        System.setProperty(KEY_STORE_PROPERTY, keyStorePath);
        System.setProperty(KEY_STORE_PASSWORD_PROPERTY, keyStorePassword);
    }

    public void configureClient(String trustStorePath, String trustStorePassword) {
        requireText(trustStorePath, "Trust store path");
        requireText(trustStorePassword, "Trust store password");

        System.setProperty(TRUST_STORE_PROPERTY, trustStorePath);
        System.setProperty(TRUST_STORE_PASSWORD_PROPERTY, trustStorePassword);
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
        System.clearProperty(TRUST_STORE_PROPERTY);
        System.clearProperty(TRUST_STORE_PASSWORD_PROPERTY);
    }

    public static boolean isRmiSslEnabled() {
        return Boolean.parseBoolean(System.getProperty(RMI_SSL_PROPERTY, "false"));
    }

    public static SslRMIClientSocketFactory rmiClientSocketFactoryIfEnabled() {
        return isRmiSslEnabled() ? new SslRMIClientSocketFactory() : null;
    }

    public static SslRMIServerSocketFactory rmiServerSocketFactoryIfEnabled() {
        return isRmiSslEnabled() ? new SslRMIServerSocketFactory() : null;
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

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
