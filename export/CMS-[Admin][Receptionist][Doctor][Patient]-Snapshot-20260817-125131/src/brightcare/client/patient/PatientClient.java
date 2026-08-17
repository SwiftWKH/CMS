package brightcare.client.patient;

import brightcare.client.gateway.RmiGatewayFactory;
import brightcare.client.gateway.RmiPatientGateway;
import brightcare.client.gateway.UnavailablePatientGateway;
import brightcare.client.patient.controller.PatientController;
import brightcare.client.patient.view.PatientFrame;
import brightcare.remote.ClinicRemoteInterface;
import javax.swing.SwingUtilities;

public class PatientClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClinicRemoteInterface remote = RmiGatewayFactory.lookupDefaultRemote();
                PatientController controller = remote == null
                        ? new PatientController(new UnavailablePatientGateway(), 0)
                        : new PatientController(new RmiPatientGateway(remote), 0);
                new PatientFrame(controller).setVisible(true);
            }
        });
    }
}
