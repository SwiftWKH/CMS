package brightcare.client.admin.view;

import brightcare.model.Report;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class ReportDisplayHelper {
    private ReportDisplayHelper() {
    }

    static String formatReport(Report report) {
        if (report == null) {
            return "No report was returned.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Report Type: ").append(report.getReportType()).append(System.lineSeparator());
        builder.append("Generated At: ").append(report.getGeneratedAt()).append(System.lineSeparator());
        builder.append("File Path: ").append(report.getFilePath()).append(System.lineSeparator());

        String content = readReportContent(report.getFilePath());
        if (content.length() > 0) {
            builder.append(System.lineSeparator());
            builder.append(content);
        }
        return builder.toString();
    }

    private static String readReportContent(String filePath) {
        if (filePath == null || filePath.trim().length() == 0) {
            return "";
        }

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return "Report file was not found.";
            }
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "Report file could not be read: " + ex.getMessage();
        }
    }
}
