package brightcare.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class BrightCareLogger {
    private static final String LOG_FILE = "logs/brightcare.log";
    private static boolean configured;

    private BrightCareLogger() {
    }

    public static Logger getLogger(Class<?> owner) {
        configure();
        return Logger.getLogger(owner.getName());
    }

    private static synchronized void configure() {
        if (configured) {
            return;
        }

        Logger root = Logger.getLogger("");
        root.setLevel(Level.INFO);

        for (java.util.logging.Handler handler : root.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.INFO);
            }
        }

        try {
            File directory = new File("logs");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            root.addHandler(fileHandler);
        } catch (IOException ex) {
            root.log(Level.WARNING, "Unable to create BrightCare log file: " + LOG_FILE, ex);
        }

        configured = true;
    }
}
