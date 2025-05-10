package utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerUtils {

    private static final Logger logger = Logger.getLogger(LoggerUtils.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            // Crear carpeta logs si no existe
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            fileHandler = new FileHandler("logs/app.log", true);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(
                            "[%1$tF %1$tT] [%2$-7s] %3$s%n",
                            new java.util.Date(record.getMillis()),
                            record.getLevel().getName(),
                            record.getMessage()
                    );
                }
            });

            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println("Error al iniciar el logger: " + e.getMessage());
        }
    }

    // === MÉTODOS CON SECCIÓN ===
    public static void logSection(String section) {
        logger.info("=== SECCIÓN: " + section.toUpperCase() + " ===");
    }

    public static void logInfo(String section, String message) {
        logger.info("[" + section.toUpperCase() + "] " + message);
    }

    public static void logWarning(String section, String message) {
        logger.warning("[" + section.toUpperCase() + "] " + message);
    }

    public static void logQuery(String section, String descripcion, String sql) {
        logger.info("[" + section.toUpperCase() + "] CONSULTA → " + descripcion + "\n[" + section.toUpperCase() + "] SQL: " + sql);
    }

    public static void logError(String section, String message, Throwable throwable) {
        logger.log(Level.SEVERE, "[" + section.toUpperCase() + "] ERROR → " + message, throwable);
    }

    // === Compatibilidad con llamadas sin sección ===
    public static void logInfo(String message) {
        logInfo("GENERAL", message);
    }

    public static void logWarning(String message) {
        logWarning("GENERAL", message);
    }

    public static void logQuery(String descripcion, String sql) {
        logQuery("GENERAL", descripcion, sql);
    }

    public static void logError(String message, Throwable throwable) {
        logError("GENERAL", message, throwable);
    }
}
