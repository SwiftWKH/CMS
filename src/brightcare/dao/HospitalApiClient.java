package brightcare.dao;

import brightcare.security.SSLConfig;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HospitalApiClient {
    public static final String DEFAULT_BASE_URL = "https://192.168.137.1:7230/hospital";

    private final String baseUrl;
    private final boolean trustAllCertificates;

    public HospitalApiClient() {
        this(resolveBaseUrl(), Boolean.parseBoolean(System.getProperty("brightcare.api.trustAll", "false")));
    }

    public HospitalApiClient(String baseUrl, boolean trustAllCertificates) {
        if (baseUrl == null || baseUrl.trim().length() == 0) {
            throw new IllegalArgumentException("API base URL is required.");
        }
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.trustAllCertificates = trustAllCertificates;
    }

    public String get(String path) {
        return request("GET", path, null);
    }

    public String post(String path, String jsonBody) {
        return request("POST", path, jsonBody);
    }

    public String put(String path, String jsonBody) {
        return request("PUT", path, jsonBody);
    }

    public String delete(String path) {
        return request("DELETE", path, null);
    }

    private String request(String method, String path, String jsonBody) {
        HttpURLConnection connection = null;
        try {
            if (trustAllCertificates) {
                SSLConfig.applyTrustAllForDevelopment();
            }

            URL url = new URL(baseUrl + normalizePath(path));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (jsonBody != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                byte[] bytes = jsonBody.getBytes("UTF-8");
                connection.setFixedLengthStreamingMode(bytes.length);
                OutputStream output = connection.getOutputStream();
                try {
                    output.write(bytes);
                } finally {
                    output.close();
                }
            }

            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 200 && responseCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String response = read(stream);
            if (responseCode < 200 || responseCode >= 300) {
                throw new IllegalStateException("API request failed with status " + responseCode + ": " + response);
            }
            return response;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to call hospital API: " + ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String read(InputStream stream) throws java.io.IOException {
        if (stream == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        try {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } finally {
            reader.close();
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private String trimTrailingSlash(String value) {
        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static String resolveBaseUrl() {
        String property = System.getProperty("brightcare.api.baseUrl");
        if (property != null && property.trim().length() > 0) {
            return property;
        }
        String environment = System.getenv("BRIGHTCARE_API_BASE_URL");
        if (environment != null && environment.trim().length() > 0) {
            return environment;
        }
        return DEFAULT_BASE_URL;
    }
}
