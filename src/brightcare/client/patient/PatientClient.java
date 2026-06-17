package brightcare.client.patient;

import brightcare.client.patient.view.PatientFrame;
import javax.swing.SwingUtilities;

public class PatientClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PatientFrame().setVisible(true);
            }
        });
    }
}
