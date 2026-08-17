package brightcare.client.gateway;

import brightcare.model.UserAccount;
import brightcare.remote.ClinicRemoteInterface;
import brightcare.util.BrightCareLogger;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiAuthenticationGateway implements AuthenticationGateway {
    private static final Logger LOGGER = BrightCareLogger.getLogger(RmiAuthenticationGateway.class);

    private final ClinicRemoteInterface remote;

    public RmiAuthenticationGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public UserAccount login(String username, String password) {
        LOGGER.info("Calling remote login for username=" + safeUsername(username) + ".");
        try {
            UserAccount account = remote.login(username, password);
            LOGGER.info("Remote login returned " + (account == null ? "null" : "userId="
                    + account.getUserId() + ", role=" + account.getRole()
                    + ", roleId=" + account.getRoleId()) + ".");
            return account;
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Remote login failed for username=" + safeUsername(username) + ".", ex);
            return null;
        }
    }

    public boolean logout(int userId) {
        try {
            return remote.logout(userId);
        } catch (RemoteException ex) {
            LOGGER.log(Level.WARNING, "Remote logout failed for userId=" + userId + ".", ex);
            return false;
        }
    }

    private String safeUsername(String username) {
        return username == null ? "<null>" : username.trim();
    }
}
