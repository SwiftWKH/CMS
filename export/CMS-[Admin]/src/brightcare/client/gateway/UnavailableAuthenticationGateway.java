package brightcare.client.gateway;

import brightcare.model.UserAccount;

public class UnavailableAuthenticationGateway implements AuthenticationGateway {
    public UserAccount login(String username, String password) {
        return null;
    }

    public boolean logout(int userId) {
        return false;
    }
}
