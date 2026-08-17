package brightcare.client.admin;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.admin.view.AdminFrame;
import brightcare.client.common.controller.NavigationController;
import brightcare.client.gateway.RmiAdminGateway;
import brightcare.client.gateway.RmiAuthenticationGateway;
import brightcare.client.gateway.RmiDoctorGateway;
import brightcare.client.gateway.RmiGatewayFactory;
import brightcare.client.gateway.RmiPatientGateway;
import brightcare.client.gateway.RmiReceptionistGateway;
import brightcare.client.gateway.UnavailableAdminGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UnavailableDoctorGateway;
import brightcare.client.gateway.UnavailablePatientGateway;
import brightcare.client.gateway.UnavailableReceptionistGateway;
import brightcare.remote.ClinicRemoteInterface;
import javax.swing.SwingUtilities;

public class AdminClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClinicRemoteInterface remote = RmiGatewayFactory.lookupDefaultRemote();
                AdminController controller;
                if (remote == null) {
                    NavigationController navigation = new NavigationController(
                            new UnavailableAuthenticationGateway(),
                            new UnavailableAdminGateway(),
                            new UnavailablePatientGateway(),
                            new UnavailableDoctorGateway(),
                            new UnavailableReceptionistGateway()
                    );
                    controller = new AdminController(navigation);
                } else {
                    RmiAuthenticationGateway authenticationGateway = new RmiAuthenticationGateway(remote);
                    RmiAdminGateway adminGateway = new RmiAdminGateway(remote);
                    NavigationController navigation = new NavigationController(
                            authenticationGateway,
                            adminGateway,
                            new RmiPatientGateway(remote),
                            new RmiDoctorGateway(remote),
                            new RmiReceptionistGateway(remote)
                    );
                    controller = new AdminController(navigation, adminGateway, authenticationGateway, 0);
                }
                new AdminFrame(controller).setVisible(true);
            }
        });
    }
}
