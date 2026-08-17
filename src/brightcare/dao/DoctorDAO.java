package brightcare.dao;

import brightcare.model.Doctor;
import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorDAO {
    private static final Logger LOGGER = BrightCareLogger.getLogger(DoctorDAO.class);

    private final DerbyConnectionFactory connectionFactory;
    private final HospitalApiClient apiClient;

    public DoctorDAO() {
        this(new DerbyConnectionFactory(), new HospitalApiClient());
    }

    public DoctorDAO(DerbyConnectionFactory connectionFactory) {
        this(connectionFactory, new HospitalApiClient());
    }

    public DoctorDAO(HospitalApiClient apiClient) {
        this(new DerbyConnectionFactory(), apiClient);
    }

    public DoctorDAO(DerbyConnectionFactory connectionFactory, HospitalApiClient apiClient) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("API client is required.");
        }
        this.connectionFactory = connectionFactory;
        this.apiClient = apiClient;
    }

    public List<Doctor> findAll() {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                return HospitalJsonMapper.doctors(apiClient.get("/doctor"));
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Doctor API list failed; falling back to Derby.", ex);
            }
        }

        List<Doctor> doctors = new ArrayList<Doctor>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT doctor_id, user_id, name, specialization, "
                    + "contact_number FROM DOCTOR ORDER BY doctor_id");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet));
            }
            return doctors;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Doctor list failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return doctors;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public Doctor findById(int doctorId) {
        if (DataSourceConfig.preferHospitalApi()) {
            for (Doctor doctor : findAll()) {
                if (doctor.getDoctorId() == doctorId) {
                    return doctor;
                }
            }
            return null;
        }
        return findOne("SELECT doctor_id, user_id, name, specialization, contact_number "
                + "FROM DOCTOR WHERE doctor_id = ?", doctorId);
    }

    public Doctor findByUserId(int userId) {
        if (DataSourceConfig.preferHospitalApi()) {
            for (Doctor doctor : findAll()) {
                if (doctor.getUserId() == userId || doctor.getDoctorId() == userId) {
                    return doctor;
                }
            }
            return null;
        }
        return findOne("SELECT doctor_id, user_id, name, specialization, contact_number "
                + "FROM DOCTOR WHERE user_id = ?", userId);
    }

    public Doctor saveWithAccount(Doctor doctor, String username, String passwordHash, String status) {
        if (!DataSourceConfig.preferHospitalApi()) {
            return null;
        }

        try {
            LOGGER.info("Creating doctor with linked user account through hospital API. username=" + username + ".");
            String response = apiClient.post("/doctor",
                    HospitalJsonMapper.doctorAccountJson(doctor, username, passwordHash, status));
            List<Doctor> doctors = HospitalJsonMapper.doctors(response);
            if (doctors.isEmpty()) {
                LOGGER.info("Doctor account API create returned no parseable doctor; returning submitted doctor.");
                return doctor;
            }
            return doctors.get(0);
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Doctor account API create failed. username=" + username + ".", ex);
            return null;
        }
    }

    private Doctor findOne(String sql, int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            return resultSet.next() ? mapDoctor(resultSet) : null;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Doctor lookup failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return null;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private Doctor mapDoctor(ResultSet resultSet) throws SQLException {
        return new Doctor(
                resultSet.getInt("doctor_id"),
                resultSet.getInt("user_id"),
                resultSet.getString("name"),
                resultSet.getString("specialization"),
                resultSet.getString("contact_number")
        );
    }

    private void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Unable to close Derby doctor resource.", ex);
        }
    }
}
