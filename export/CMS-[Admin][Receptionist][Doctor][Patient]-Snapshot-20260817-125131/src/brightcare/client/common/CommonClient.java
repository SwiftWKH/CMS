package brightcare.client.common;

import brightcare.client.common.controller.LoginController;
import brightcare.client.common.view.LoginFrame;
import javax.swing.SwingUtilities;

public class CommonClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame(new LoginController()).setVisible(true);
            }
        });
    }
}
