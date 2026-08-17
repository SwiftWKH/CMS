package brightcare.dao;

import brightcare.model.ConsultationNote;
import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultationNoteDAO {
    private static final Logger LOGGER = BrightCareLogger.getLogger(ConsultationNoteDAO.class);

    private final DerbyConnectionFactory connectionFactory;
    private final HospitalApiClient apiClient;

    public ConsultationNoteDAO() {
        this(new DerbyConnectionFactory(), new HospitalApiClient());
    }

    public ConsultationNoteDAO(DerbyConnectionFactory connectionFactory) {
        this(connectionFactory, new HospitalApiClient());
    }

    public ConsultationNoteDAO(HospitalApiClient apiClient) {
        this(new DerbyConnectionFactory(), apiClient);
    }

    public ConsultationNoteDAO(DerbyConnectionFactory connectionFactory, HospitalApiClient apiClient) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("API client is required.");
        }
        this.connectionFactory = connectionFactory;
        this.apiClient = apiClient;
    }

    public List<ConsultationNote> findByPatientId(int patientId) {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                return HospitalJsonMapper.consultationsForPatient(apiClient.get("/consultation"), patientId);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Consultation API history failed; falling back to Derby.", ex);
            }
        }

        List<ConsultationNote> notes = new ArrayList<ConsultationNote>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT cn.note_id, cn.appointment_id, a.patient_id, cn.doctor_id, "
                    + "cn.notes, cn.diagnosis, cn.prescription, cn.created_at "
                    + "FROM CONSULTATION_NOTE cn "
                    + "JOIN APPOINTMENT a ON cn.appointment_id = a.appointment_id "
                    + "WHERE a.patient_id = ? ORDER BY cn.created_at");
            statement.setInt(1, patientId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                notes.add(mapNote(resultSet));
            }
            return notes;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Consultation history query failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return notes;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public ConsultationNote save(ConsultationNote note) {
        if (note.getCreatedAt() == null) {
            note.setCreatedAt(LocalDateTime.now());
        }
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                LOGGER.info("Saving consultation through hospital API. appointmentId=" + note.getAppointmentId()
                        + ", patientId=" + note.getPatientId() + ", doctorId=" + note.getDoctorId() + ".");
                String response = apiClient.post("/consultation", HospitalJsonMapper.consultationJson(note));
                List<ConsultationNote> notes = HospitalJsonMapper.consultations(response);
                return notes.isEmpty() ? note : notes.get(0);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Consultation API save failed; falling back to Derby.", ex);
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            int noteId = nextNoteId(connection);
            statement = connection.prepareStatement("INSERT INTO CONSULTATION_NOTE "
                    + "(note_id, appointment_id, doctor_id, notes, diagnosis, prescription, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, noteId);
            statement.setInt(2, note.getAppointmentId());
            statement.setInt(3, note.getDoctorId());
            statement.setString(4, note.getNotes());
            statement.setString(5, note.getDiagnosis());
            statement.setString(6, note.getPrescription());
            statement.setTimestamp(7, Timestamp.valueOf(note.getCreatedAt()));
            statement.executeUpdate();
            note.setNoteId(noteId);
            return note;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Consultation note save failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return note;
        } finally {
            close(statement);
            close(connection);
        }
    }

    private ConsultationNote mapNote(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new ConsultationNote(
                resultSet.getInt("note_id"),
                resultSet.getInt("appointment_id"),
                readOptionalInt(resultSet, "patient_id"),
                resultSet.getInt("doctor_id"),
                resultSet.getString("notes"),
                resultSet.getString("diagnosis"),
                resultSet.getString("prescription"),
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
    }

    private int readOptionalInt(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getInt(columnName);
        } catch (SQLException ex) {
            return 0;
        }
    }

    private int nextNoteId(Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT COALESCE(MAX(note_id), 0) + 1 FROM CONSULTATION_NOTE");
            resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 1;
        } finally {
            close(resultSet);
            close(statement);
        }
    }

    private void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Unable to close Derby consultation resource.", ex);
        }
    }
}
