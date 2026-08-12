package brightcare.model;

import java.io.Serializable;

public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;

    private int doctorId;
    private int userId;
    private String name;
    private String specialization;
    private String contactNumber;

    public Doctor() {
    }

    public Doctor(int doctorId, int userId, String name, String specialization, String contactNumber) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
