package brightcare.client.common;

import brightcare.client.common.view.LoginFrame;
import javax.swing.SwingUtilities;

public class CommonClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
