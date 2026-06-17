package brightcare.client.doctor;

import brightcare.client.doctor.view.DoctorFrame;
import javax.swing.SwingUtilities;

public class DoctorClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DoctorFrame().setVisible(true);
            }
        });
    }
}
