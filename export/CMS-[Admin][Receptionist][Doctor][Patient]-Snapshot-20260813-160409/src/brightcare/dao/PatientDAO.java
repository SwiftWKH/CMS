package brightcare.dao;

import brightcare.model.Patient;
import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientDAO {
    private static final Logger LOGGER = BrightCareLogger.getLogger(PatientDAO.class);

    private final DerbyConnectionFactory connectionFactory;
    private final HospitalApiClient apiClient;

    public PatientDAO() {
        this(new DerbyConnectionFactory(), new HospitalApiClient());
    }

    public PatientDAO(DerbyConnectionFactory connectionFactory) {
        this(connectionFactory, new HospitalApiClient());
    }

    public PatientDAO(HospitalApiClient apiClient) {
        this(new DerbyConnectionFactory(), apiClient);
    }

    public PatientDAO(DerbyConnectionFactory connectionFactory, HospitalApiClient apiClient) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("API client is required.");
        }
        this.connectionFactory = connectionFactory;
        this.apiClient = apiClient;
    }

    public List<Patient> findAll() {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                return HospitalJsonMapper.patients(apiClient.get("/patient"));
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Patient API list failed; falling back to Derby.", ex);
            }
        }

        List<Patient> patients = new ArrayList<Patient>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT patient_id, user_id, first_name, last_name, "
                    + "ic_passport_no, contact_number, medical_record_id FROM PATIENT ORDER BY patient_id");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                patients.add(mapPatient(resultSet));
            }
            return patients;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Patient list failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return patients;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public Patient findById(int patientId) {
        if (DataSourceConfig.preferHospitalApi()) {
            for (Patient patient : findAll()) {
                if (patient.getPatientId() == patientId) {
                    return patient;
                }
            }
            return null;
        }
        return findOne("SELECT patient_id, user_id, first_name, last_name, ic_passport_no, "
                + "contact_number, medical_record_id FROM PATIENT WHERE patient_id = ?", patientId);
    }

    public Patient findByUserId(int userId) {
        if (DataSourceConfig.preferHospitalApi()) {
            for (Patient patient : findAll()) {
                if (patient.getUserId() == userId || patient.getPatientId() == userId) {
                    return patient;
                }
            }
            return null;
        }
        return findOne("SELECT patient_id, user_id, first_name, last_name, ic_passport_no, "
                + "contact_number, medical_record_id FROM PATIENT WHERE user_id = ?", userId);
    }

    public Patient save(Patient patient) {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                String response = apiClient.post("/patient", HospitalJsonMapper.patientJson(patient));
                List<Patient> patients = HospitalJsonMapper.patients(response);
                return patients.isEmpty() ? patient : patients.get(0);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Patient API save failed; falling back to Derby.", ex);
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            int patientId = nextPatientId(connection);
            statement = connection.prepareStatement("INSERT INTO PATIENT "
                    + "(patient_id, user_id, first_name, last_name, ic_passport_no, contact_number, medical_record_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, patientId);
            setNullableInt(statement, 2, patient.getUserId());
            statement.setString(3, patient.getFirstName());
            statement.setString(4, patient.getLastName());
            statement.setString(5, patient.getIcPassportNo());
            statement.setString(6, patient.getContactNumber());
            statement.setString(7, patient.getMedicalRecordId());
            statement.executeUpdate();
            patient.setPatientId(patientId);
            return patient;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Patient save failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return patient;
        } finally {
            close(statement);
            close(connection);
        }
    }

    public Patient update(Patient patient) {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                String response = apiClient.put("/patient/" + patient.getPatientId(),
                        HospitalJsonMapper.patientJson(patient));
                List<Patient> patients = HospitalJsonMapper.patients(response);
                return patients.isEmpty() ? patient : patients.get(0);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Patient API update failed; falling back to Derby.", ex);
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("UPDATE PATIENT SET user_id = ?, first_name = ?, "
                    + "last_name = ?, ic_passport_no = ?, contact_number = ?, medical_record_id = ? "
                    + "WHERE patient_id = ?");
            setNullableInt(statement, 1, patient.getUserId());
            statement.setString(2, patient.getFirstName());
            statement.setString(3, patient.getLastName());
            statement.setString(4, patient.getIcPassportNo());
            statement.setString(5, patient.getContactNumber());
            statement.setString(6, patient.getMedicalRecordId());
            statement.setInt(7, patient.getPatientId());
            statement.executeUpdate();
            return patient;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Patient update failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return patient;
        } finally {
            close(statement);
            close(connection);
        }
    }

    private Patient findOne(String sql, int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            return resultSet.next() ? mapPatient(resultSet) : null;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Patient lookup failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return null;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private Patient mapPatient(ResultSet resultSet) throws SQLException {
        return new Patient(
                resultSet.getInt("patient_id"),
                resultSet.getInt("user_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("ic_passport_no"),
                resultSet.getString("contact_number"),
                resultSet.getString("medical_record_id")
        );
    }

    private int nextPatientId(Connection connection) throws SQLException {
        return nextId(connection, "SELECT COALESCE(MAX(patient_id), 0) + 1 FROM PATIENT");
    }

    private int nextId(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 1;
        } finally {
            close(resultSet);
            close(statement);
        }
    }

    private void setNullableInt(PreparedStatement statement, int index, int value) throws SQLException {
        if (value <= 0) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Unable to close Derby patient resource.", ex);
        }
    }
}
