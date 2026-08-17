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
        String host = System.getProperty("brightcare.rmi.host", "localhost");
        return lookupRemote(host);
    }

    public static ClinicRemoteInterface lookupRemote(String host) {
        try {
            String resolvedHost = host == null || host.trim().length() == 0 ? "localhost" : host.trim();
            int port = Integer.parseInt(System.getProperty("brightcare.rmi.port",
                    String.valueOf(ClinicServer.DEFAULT_RMI_PORT)));
            System.setProperty("brightcare.rmi.host", resolvedHost);
            LOGGER.info("Looking up RMI service. host=" + resolvedHost + ", port=" + port
                    + ", service=" + ClinicServer.SERVICE_NAME
                    + ", ssl=" + SSLConfig.isRmiSslEnabled()
                    + ", relaxedHostCheck=" + SSLConfig.isRmiRelaxedHostCheckEnabled() + ".");
            Registry registry = SSLConfig.isRmiSslEnabled()
                    ? LocateRegistry.getRegistry(resolvedHost, port, SSLConfig.rmiClientSocketFactoryIfEnabled())
                    : LocateRegistry.getRegistry(resolvedHost, port);
            ClinicRemoteInterface remote = (ClinicRemoteInterface) registry.lookup(ClinicServer.SERVICE_NAME);
            LOGGER.info("RMI service lookup succeeded.");
            return remote;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "RMI service lookup failed. Login will use unavailable gateway.", ex);
            return null;
        }
    }
}
