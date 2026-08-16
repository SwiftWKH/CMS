package brightcare.concurrency;

import brightcare.model.Appointment;
import brightcare.util.BrightCareLogger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class AppointmentLockManager {
    private static final Logger LOGGER = BrightCareLogger.getLogger(AppointmentLockManager.class);

    private final Set<String> lockedSlots = new HashSet<String>();

    public synchronized boolean acquireSlot(Appointment appointment) {
        String key = keyFor(appointment);
        boolean acquired = lockedSlots.add(key);
        LOGGER.info("Appointment slot lock " + (acquired ? "acquired" : "blocked") + ". slot=" + key + ".");
        return acquired;
    }

    public synchronized void releaseSlot(Appointment appointment) {
        String key = keyFor(appointment);
        lockedSlots.remove(key);
        LOGGER.info("Appointment slot lock released. slot=" + key + ".");
    }

    private String keyFor(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment is required.");
        }
        int doctorId = appointment.getDoctorId();
        LocalDate date = appointment.getAppointmentDate();
        LocalTime time = appointment.getAppointmentTime();
        if (doctorId <= 0 || date == null || time == null) {
            throw new IllegalArgumentException("Doctor, date, and time are required for appointment locking.");
        }
        return doctorId + "|" + date + "|" + time;
    }
}
