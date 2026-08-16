package brightcare.dao;

import brightcare.service.ReportService;
import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DerbyReportDataProvider implements ReportService.ReportDataProvider {
    private static final Logger LOGGER = BrightCareLogger.getLogger(DerbyReportDataProvider.class);

    private final DerbyConnectionFactory connectionFactory;

    public DerbyReportDataProvider() {
        this(new DerbyConnectionFactory());
    }

    public DerbyReportDataProvider(DerbyConnectionFactory connectionFactory) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        this.connectionFactory = connectionFactory;
    }

    public List<ReportService.AppointmentSummary> findAppointmentsByMonth(int month, int year) {
        List<ReportService.AppointmentSummary> appointments =
                new ArrayList<ReportService.AppointmentSummary>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT appointment_id, patient_id, doctor_id, "
                    + "appointment_date, appointment_time, status FROM APPOINTMENT "
                    + "WHERE MONTH(appointment_date) = ? AND YEAR(appointment_date) = ? "
                    + "ORDER BY appointment_date, appointment_time");
            statement.setInt(1, month);
            statement.setInt(2, year);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(new ReportService.AppointmentSummary(
                        resultSet.getInt("appointment_id"),
                        resultSet.getInt("patient_id"),
                        resultSet.getInt("doctor_id"),
                        toLocalDate(resultSet.getDate("appointment_date")),
                        toTimeText(resultSet.getTime("appointment_time")),
                        resultSet.getString("status")
                ));
            }
            return appointments;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Monthly appointment report query failed. sqlState="
                    + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return appointments;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<ReportService.ConsultationSummary> findConsultationsByDoctorAndMonth(
            int doctorId, int month, int year) {
        List<ReportService.ConsultationSummary> consultations =
                new ArrayList<ReportService.ConsultationSummary>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT cn.appointment_id, a.patient_id, cn.diagnosis "
                    + "FROM CONSULTATION_NOTE cn "
                    + "JOIN APPOINTMENT a ON cn.appointment_id = a.appointment_id "
                    + "WHERE cn.doctor_id = ? AND MONTH(cn.created_at) = ? AND YEAR(cn.created_at) = ? "
                    + "ORDER BY cn.created_at");
            statement.setInt(1, doctorId);
            statement.setInt(2, month);
            statement.setInt(3, year);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consultations.add(new ReportService.ConsultationSummary(
                        resultSet.getInt("appointment_id"),
                        resultSet.getInt("patient_id"),
                        resultSet.getString("diagnosis")
                ));
            }
            return consultations;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Doctor consultation report query failed. sqlState="
                    + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return consultations;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<ReportService.VisitSummary> findVisitsByPatient(int patientId) {
        List<ReportService.VisitSummary> visits = new ArrayList<ReportService.VisitSummary>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT a.appointment_date, a.doctor_id, "
                    + "a.status, cn.diagnosis, cn.notes "
                    + "FROM APPOINTMENT a "
                    + "LEFT JOIN CONSULTATION_NOTE cn ON a.appointment_id = cn.appointment_id "
                    + "WHERE a.patient_id = ? "
                    + "ORDER BY a.appointment_date, a.appointment_time");
            statement.setInt(1, patientId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                visits.add(new ReportService.VisitSummary(
                        toLocalDate(resultSet.getDate("appointment_date")),
                        resultSet.getInt("doctor_id"),
                        buildVisitSummary(resultSet)
                ));
            }
            return visits;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Patient visit summary query failed. sqlState="
                    + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return visits;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public ReportService.SystemStatistics getSystemStatistics() {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            return new ReportService.SystemStatistics(
                    count(connection, "SELECT COUNT(*) FROM PATIENT"),
                    count(connection, "SELECT COUNT(*) FROM DOCTOR"),
                    count(connection, "SELECT COUNT(*) FROM APPOINTMENT"),
                    count(connection, "SELECT COUNT(*) FROM APPOINTMENT WHERE status = 'COMPLETED'"),
                    count(connection, "SELECT COUNT(*) FROM APPOINTMENT WHERE status = 'CANCELLED'")
            );
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "System statistics query failed. sqlState="
                    + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return new ReportService.SystemStatistics(0, 0, 0, 0, 0);
        } finally {
            close(connection);
        }
    }

    private int count(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } finally {
            close(resultSet);
            close(statement);
        }
    }

    private String buildVisitSummary(ResultSet resultSet) throws SQLException {
        String diagnosis = resultSet.getString("diagnosis");
        String notes = resultSet.getString("notes");
        if (diagnosis != null && diagnosis.trim().length() > 0) {
            return "Diagnosis: " + diagnosis;
        }
        if (notes != null && notes.trim().length() > 0) {
            return "Notes: " + notes;
        }
        return "Appointment status: " + resultSet.getString("status");
    }

    private java.time.LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private String toTimeText(Time time) {
        return time == null ? "" : time.toLocalTime().toString();
    }

    private void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Unable to close Derby report resource.", ex);
        }
    }
}
