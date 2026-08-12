package brightcare.client.gateway;

import brightcare.model.UserAccount;
import brightcare.remote.ClinicRemoteInterface;
import java.rmi.RemoteException;

public class RmiAuthenticationGateway implements AuthenticationGateway {
    private final ClinicRemoteInterface remote;

    public RmiAuthenticationGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public UserAccount login(String username, String password) {
        try {
            return remote.login(username, password);
        } catch (RemoteException ex) {
            return null;
        }
    }

    public boolean logout(int userId) {
        try {
            return remote.logout(userId);
        } catch (RemoteException ex) {
            return false;
        }
    }
}
