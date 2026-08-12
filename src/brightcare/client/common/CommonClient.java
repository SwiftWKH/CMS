package brightcare.client.common;

import brightcare.client.common.controller.LoginController;
import brightcare.client.common.controller.NavigationController;
import brightcare.client.common.view.LoginFrame;
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

public class CommonClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClinicRemoteInterface remote = RmiGatewayFactory.lookupDefaultRemote();
                NavigationController navigationController;
                LoginController loginController;

                if (remote == null) {
                    navigationController = new NavigationController(
                            new UnavailableAuthenticationGateway(),
                            new UnavailableAdminGateway(),
                            new UnavailablePatientGateway(),
                            new UnavailableDoctorGateway(),
                            new UnavailableReceptionistGateway()
                    );
                    loginController = new LoginController(navigationController);
                } else {
                    RmiAuthenticationGateway authenticationGateway = new RmiAuthenticationGateway(remote);
                    navigationController = new NavigationController(
                            authenticationGateway,
                            new RmiAdminGateway(remote),
                            new RmiPatientGateway(remote),
                            new RmiDoctorGateway(remote),
                            new RmiReceptionistGateway(remote)
                    );
                    loginController = new LoginController(authenticationGateway, navigationController);
                }

                new LoginFrame(loginController).setVisible(true);
            }
        });
    }
}
