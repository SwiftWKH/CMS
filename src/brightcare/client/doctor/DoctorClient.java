package brightcare.client.doctor;

import brightcare.client.doctor.controller.DoctorController;
import brightcare.client.doctor.view.DoctorFrame;
import brightcare.client.gateway.RmiDoctorGateway;
import brightcare.client.gateway.RmiGatewayFactory;
import brightcare.client.gateway.UnavailableDoctorGateway;
import brightcare.remote.ClinicRemoteInterface;
import javax.swing.SwingUtilities;

public class DoctorClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClinicRemoteInterface remote = RmiGatewayFactory.lookupDefaultRemote();
                DoctorController controller = remote == null
                        ? new DoctorController(new UnavailableDoctorGateway(), 0)
                        : new DoctorController(new RmiDoctorGateway(remote), 0);
                new DoctorFrame(controller).setVisible(true);
            }
        });
    }
}
