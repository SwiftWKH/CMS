package brightcare.client.gateway;

import brightcare.model.UserAccount;
import brightcare.util.BrightCareLogger;
import java.util.logging.Logger;

public class UnavailableAuthenticationGateway implements AuthenticationGateway {
    private static final Logger LOGGER = BrightCareLogger.getLogger(UnavailableAuthenticationGateway.class);

    public UserAccount login(String username, String password) {
        LOGGER.warning("Login attempted while authentication gateway is unavailable. "
                + "Start ClinicServer and ensure RMI lookup succeeds.");
        return null;
    }

    public boolean logout(int userId) {
        return false;
    }
}
