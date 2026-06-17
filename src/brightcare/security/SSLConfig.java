package brightcare.security;

public class SSLConfig {
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
        System.clearProperty(KEY_STORE_PROPERTY);
        System.clearProperty(KEY_STORE_PASSWORD_PROPERTY);
        System.clearProperty(TRUST_STORE_PROPERTY);
        System.clearProperty(TRUST_STORE_PASSWORD_PROPERTY);
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
