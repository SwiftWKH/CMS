package brightcare.server;

import brightcare.remote.ClinicRemoteInterface;
import brightcare.util.BrightCareLogger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClinicServer {
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final String SERVICE_NAME = "BrightCareClinicService";
    private static final Logger LOGGER = BrightCareLogger.getLogger(ClinicServer.class);

    public static void main(String[] args) {
        try {
            int port = resolvePort(args);
            Registry registry = getOrCreateRegistry(port);
            ClinicRemoteInterface server = new ClinicServerImplementation();
            registry.rebind(SERVICE_NAME, server);

            System.out.println("BrightCare RMI server started.");
            System.out.println("Service: " + SERVICE_NAME);
            System.out.println("Port: " + port);
            LOGGER.info("BrightCare RMI server started. service=" + SERVICE_NAME + ", port=" + port + ".");
        } catch (Exception ex) {
            System.err.println("Unable to start BrightCare RMI server: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, "Unable to start BrightCare RMI server.", ex);
            ex.printStackTrace();
        }
    }

    private static Registry getOrCreateRegistry(int port) throws java.rmi.RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list();
            return registry;
        } catch (java.rmi.RemoteException ex) {
            return LocateRegistry.createRegistry(port);
        }
    }

    private static int resolvePort(String[] args) {
        if (args != null && args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                return DEFAULT_RMI_PORT;
            }
        }
        String property = System.getProperty("brightcare.rmi.port");
        if (property != null && property.trim().length() > 0) {
            try {
                return Integer.parseInt(property);
            } catch (NumberFormatException ex) {
                return DEFAULT_RMI_PORT;
            }
        }
        return DEFAULT_RMI_PORT;
    }
}
