package brightcare.client.receptionist;

import brightcare.client.receptionist.view.ReceptionistFrame;
import javax.swing.SwingUtilities;

public class ReceptionistClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ReceptionistFrame().setVisible(true);
            }
        });
    }
}
