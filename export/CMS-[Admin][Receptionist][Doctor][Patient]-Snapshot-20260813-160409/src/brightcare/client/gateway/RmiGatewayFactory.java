package brightcare.client.gateway;

import brightcare.remote.ClinicRemoteInterface;
import brightcare.security.SSLConfig;
import brightcare.server.ClinicServer;
import brightcare.util.BrightCareLogger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiGatewayFactory {
    private static final Logger LOGGER = BrightCareLogger.getLogger(RmiGatewayFactory.class);

    private RmiGatewayFactory() {
    }

    public static ClinicRemoteInterface lookupDefaultRemote() {
        try {
            String host = System.getProperty("brightcare.rmi.host", "localhost");
            int port = Integer.parseInt(System.getProperty("brightcare.rmi.port",
                    String.valueOf(ClinicServer.DEFAULT_RMI_PORT)));
            LOGGER.info("Looking up RMI service. host=" + host + ", port=" + port
                    + ", service=" + ClinicServer.SERVICE_NAME
                    + ", ssl=" + SSLConfig.isRmiSslEnabled() + ".");
            Registry registry = SSLConfig.isRmiSslEnabled()
                    ? LocateRegistry.getRegistry(host, port, SSLConfig.rmiClientSocketFactoryIfEnabled())
                    : LocateRegistry.getRegistry(host, port);
            ClinicRemoteInterface remote = (ClinicRemoteInterface) registry.lookup(ClinicServer.SERVICE_NAME);
            LOGGER.info("RMI service lookup succeeded.");
            return remote;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "RMI service lookup failed. Login will use unavailable gateway.", ex);
            return null;
        }
    }
}
