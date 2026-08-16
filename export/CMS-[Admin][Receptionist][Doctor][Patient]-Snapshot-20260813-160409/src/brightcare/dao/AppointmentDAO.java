package brightcare.dao;

import brightcare.model.Appointment;
import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAO {
    private static final Logger LOGGER = BrightCareLogger.getLogger(AppointmentDAO.class);

    private final DerbyConnectionFactory connectionFactory;
    private final HospitalApiClient apiClient;

    public AppointmentDAO() {
        this(new DerbyConnectionFactory(), new HospitalApiClient());
    }

    public AppointmentDAO(DerbyConnectionFactory connectionFactory) {
        this(connectionFactory, new HospitalApiClient());
    }

    public AppointmentDAO(HospitalApiClient apiClient) {
        this(new DerbyConnectionFactory(), apiClient);
    }

    public AppointmentDAO(DerbyConnectionFactory connectionFactory, HospitalApiClient apiClient) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("API client is required.");
        }
        this.connectionFactory = connectionFactory;
        this.apiClient = apiClient;
    }

    public List<Appointment> findAll() {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                return HospitalJsonMapper.appointments(apiClient.get("/appointment"));
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Appointment API list failed; falling back to Derby.", ex);
            }
        }
        return findMany("SELECT appointment_id, patient_id, doctor_id, appointment_date, "
                + "appointment_time, status, reason FROM APPOINTMENT ORDER BY appointment_date, appointment_time");
    }

    public List<Appointment> findByPatientId(int patientId) {
        if (DataSourceConfig.preferHospitalApi()) {
            List<Appointment> result = new ArrayList<Appointment>();
            for (Appointment appointment : findAll()) {
                if (appointment.getPatientId() == patientId) {
                    result.add(appointment);
                }
            }
            return result;
        }
        return findMany("SELECT appointment_id, patient_id, doctor_id, appointment_date, "
                + "appointment_time, status, reason FROM APPOINTMENT WHERE patient_id = ? "
                + "ORDER BY appointment_date, appointment_time", patientId);
    }

    public List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date) {
        if (DataSourceConfig.preferHospitalApi()) {
            List<Appointment> result = new ArrayList<Appointment>();
            for (Appointment appointment : findAll()) {
                boolean doctorMatches = appointment.getDoctorId() == doctorId;
                boolean dateMatches = date == null || date.equals(appointment.getAppointmentDate());
                if (doctorMatches && dateMatches) {
                    result.add(appointment);
                }
            }
            return result;
        }
        if (date == null) {
            return findMany("SELECT appointment_id, patient_id, doctor_id, appointment_date, "
                    + "appointment_time, status, reason FROM APPOINTMENT WHERE doctor_id = ? "
                    + "ORDER BY appointment_date, appointment_time", doctorId);
        }
        return findMany("SELECT appointment_id, patient_id, doctor_id, appointment_date, "
                + "appointment_time, status, reason FROM APPOINTMENT WHERE doctor_id = ? "
                + "AND appointment_date = ? ORDER BY appointment_time", doctorId, date);
    }

    public List<Appointment> findByDate(LocalDate date) {
        if (DataSourceConfig.preferHospitalApi()) {
            List<Appointment> result = new ArrayList<Appointment>();
            for (Appointment appointment : findAll()) {
                if (date == null || date.equals(appointment.getAppointmentDate())) {
                    result.add(appointment);
                }
            }
            return result;
        }
        if (date == null) {
            return findAll();
        }
        return findMany("SELECT appointment_id, patient_id, doctor_id, appointment_date, "
                + "appointment_time, status, reason FROM APPOINTMENT WHERE appointment_date = ? "
                + "ORDER BY appointment_time", date);
    }

    public Appointment save(Appointment appointment) {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                String response = apiClient.post("/appointment", HospitalJsonMapper.appointmentJson(appointment));
                List<Appointment> appointments = HospitalJsonMapper.appointments(response);
                return appointments.isEmpty() ? appointment : appointments.get(0);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Appointment API save failed; falling back to Derby.", ex);
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            int appointmentId = nextAppointmentId(connection);
            statement = connection.prepareStatement("INSERT INTO APPOINTMENT "
                    + "(appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status, reason) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, appointmentId);
            setAppointmentFields(statement, appointment, 2);
            statement.executeUpdate();
            appointment.setAppointmentId(appointmentId);
            return appointment;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Appointment save failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return appointment;
        } finally {
            close(statement);
            close(connection);
        }
    }

    public Appointment update(Appointment appointment) {
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                String response = apiClient.put("/appointment/" + appointment.getAppointmentId(),
                        HospitalJsonMapper.appointmentJson(appointment));
                List<Appointment> appointments = HospitalJsonMapper.appointments(response);
                return appointments.isEmpty() ? appointment : appointments.get(0);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Appointment API update failed; falling back to Derby.", ex);
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("UPDATE APPOINTMENT SET patient_id = ?, doctor_id = ?, "
                    + "appointment_date = ?, appointment_time = ?, status = ?, reason = ? "
                    + "WHERE appointment_id = ?");
            setAppointmentFields(statement, appointment, 1);
            statement.setInt(7, appointment.getAppointmentId());
            statement.executeUpdate();
            return appointment;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Appointment update failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return appointment;
        } finally {
            close(statement);
            close(connection);
        }
    }

    public Appointment cancel(int appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                apiClient.delete("/appointment/" + appointmentId);
                if (appointment == null) {
                    appointment = new Appointment();
                    appointment.setAppointmentId(appointmentId);
                }
                appointment.setStatus("CANCELLED");
                return appointment;
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Appointment API cancel failed; falling back to Derby.", ex);
            }
        }
        if (appointment == null) {
            appointment = new Appointment();
            appointment.setAppointmentId(appointmentId);
            appointment.setStatus("CANCELLED");
            return appointment;
        }
        appointment.setStatus("CANCELLED");
        return update(appointment);
    }

    public Appointment findById(int appointmentId) {
        if (DataSourceConfig.preferHospitalApi()) {
            for (Appointment appointment : findAll()) {
                if (appointment.getAppointmentId() == appointmentId) {
                    return appointment;
                }
            }
            return null;
        }
        List<Appointment> appointments = findMany("SELECT appointment_id, patient_id, doctor_id, appointment_date, "
                + "appointment_time, status, reason FROM APPOINTMENT WHERE appointment_id = ?", appointmentId);
        return appointments.isEmpty() ? null : appointments.get(0);
    }

    private List<Appointment> findMany(String sql, Object... parameters) {
        List<Appointment> appointments = new ArrayList<Appointment>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement(sql);
            bindParameters(statement, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(mapAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Appointment query failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return appointments;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            int index = i + 1;
            if (parameter instanceof Integer) {
                statement.setInt(index, ((Integer) parameter).intValue());
            } else if (parameter instanceof LocalDate) {
                statement.setDate(index, Date.valueOf((LocalDate) parameter));
            } else {
                statement.setString(index, String.valueOf(parameter));
            }
        }
    }

    private void setAppointmentFields(PreparedStatement statement, Appointment appointment, int startIndex)
            throws SQLException {
        statement.setInt(startIndex, appointment.getPatientId());
        statement.setInt(startIndex + 1, appointment.getDoctorId());
        statement.setDate(startIndex + 2, Date.valueOf(appointment.getAppointmentDate()));
        statement.setTime(startIndex + 3, Time.valueOf(appointment.getAppointmentTime()));
        statement.setString(startIndex + 4, appointment.getStatus());
        statement.setString(startIndex + 5, appointment.getReason());
    }

    private Appointment mapAppointment(ResultSet resultSet) throws SQLException {
        Date date = resultSet.getDate("appointment_date");
        Time time = resultSet.getTime("appointment_time");
        return new Appointment(
                resultSet.getInt("appointment_id"),
                resultSet.getInt("patient_id"),
                resultSet.getInt("doctor_id"),
                date == null ? null : date.toLocalDate(),
                time == null ? null : time.toLocalTime(),
                resultSet.getString("status"),
                resultSet.getString("reason")
        );
    }

    private int nextAppointmentId(Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT COALESCE(MAX(appointment_id), 0) + 1 FROM APPOINTMENT");
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
            LOGGER.log(Level.WARNING, "Unable to close Derby appointment resource.", ex);
        }
    }
}
