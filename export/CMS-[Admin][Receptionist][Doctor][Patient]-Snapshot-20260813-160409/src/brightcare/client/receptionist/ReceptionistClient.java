package brightcare.client.receptionist;

import brightcare.client.gateway.RmiGatewayFactory;
import brightcare.client.gateway.RmiReceptionistGateway;
import brightcare.client.gateway.UnavailableReceptionistGateway;
import brightcare.client.receptionist.controller.ReceptionistController;
import brightcare.client.receptionist.view.ReceptionistFrame;
import brightcare.remote.ClinicRemoteInterface;
import javax.swing.SwingUtilities;

public class ReceptionistClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClinicRemoteInterface remote = RmiGatewayFactory.lookupDefaultRemote();
                ReceptionistController controller = remote == null
                        ? new ReceptionistController(new UnavailableReceptionistGateway())
                        : new ReceptionistController(new RmiReceptionistGateway(remote));
                new ReceptionistFrame(controller).setVisible(true);
            }
        });
    }
}
