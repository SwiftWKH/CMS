package brightcare.dao;

public final class DataSourceConfig {
    public static final String DATA_SOURCE_PROPERTY = "brightcare.data.source";
    public static final String DATA_SOURCE_ENV = "BRIGHTCARE_DATA_SOURCE";
    public static final String MODE_API = "api";
    public static final String MODE_DERBY = "derby";

    private DataSourceConfig() {
    }

    public static boolean preferHospitalApi() {
        return !MODE_DERBY.equalsIgnoreCase(resolveMode());
    }

    public static String resolveMode() {
        String property = System.getProperty(DATA_SOURCE_PROPERTY);
        if (hasText(property)) {
            return property.trim();
        }
        String environment = System.getenv(DATA_SOURCE_ENV);
        if (hasText(environment)) {
            return environment.trim();
        }
        return MODE_API;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
