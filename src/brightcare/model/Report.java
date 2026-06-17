package brightcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private int reportId;
    private String reportType;
    private int generatedBy;
    private LocalDateTime generatedAt;
    private String filePath;

    public Report() {
    }

    public Report(int reportId, String reportType, int generatedBy, LocalDateTime generatedAt, String filePath) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
        this.filePath = filePath;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(int generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
