package brightcare.client.admin;

import brightcare.client.admin.view.AdminFrame;
import javax.swing.SwingUtilities;

public class AdminClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminFrame().setVisible(true);
            }
        });
    }
}
