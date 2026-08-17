package brightcare.client.gateway;

import brightcare.model.UserAccount;

public interface AuthenticationGateway {
    UserAccount login(String username, String password);

    boolean logout(int userId);
}
