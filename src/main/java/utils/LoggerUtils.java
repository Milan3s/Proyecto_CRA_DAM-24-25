package utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * Clase utilitaria para la gestión centralizada de logs en la aplicación.
 * Registra mensajes en archivo usando java.util.logging con formato
 * personalizado.
 */
public class LoggerUtils {

    // Instancia principal del logger
    private static final Logger logger = Logger.getLogger(LoggerUtils.class.getName());

    // Manejador de archivo para guardar los logs
    private static FileHandler fileHandler;

    // Bloque estático que se ejecuta al cargar la clase
    static {
        try {
            // Crear la carpeta "logs" si no existe
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs(); // Crea la carpeta de logs
            }

            // Crear o reutilizar el archivo de log (modo append = true)
            fileHandler = new FileHandler("logs/app.log", true);

            // Formatear cada línea del log con fecha, nivel y mensaje
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(
                            "[%1$tF %1$tT] [%2$-7s] %3$s%n", // Ej: [2025-05-10 16:40:12] [INFO   ] mensaje
                            new java.util.Date(record.getMillis()),
                            record.getLevel().getName(),
                            record.getMessage()
                    );
                }
            });

            // Asociar el manejador de archivo al logger
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Registrar todos los niveles (INFO, SEVERE, etc.)
            logger.setUseParentHandlers(false); // Evita duplicación en consola

        } catch (IOException e) {
            // Fallback por consola si el archivo de log falla
            System.err.println("Error al iniciar el logger: " + e.getMessage());
        }
    }

    // ========================
    // MÉTODOS CON SECCIÓN PERSONALIZADA
    // ========================
    /**
     * Marca visualmente el inicio de una sección en el log.
     *
     * @param section Nombre de la sección (mayúsculas por convención)
     */
    public static void logSection(String section) {
        logger.info("=== SECCIÓN: " + section.toUpperCase() + " ===");
    }

    /**
     * Registra un mensaje informativo con nombre de sección.
     *
     * @param section Módulo o funcionalidad
     * @param message Texto del log
     */
    public static void logInfo(String section, String message) {
        logger.info("[" + section.toUpperCase() + "] " + message);
    }

    /**
     * Registra una advertencia con sección personalizada.
     *
     * @param section Contexto
     * @param message Descripción del warning
     */
    public static void logWarning(String section, String message) {
        logger.warning("[" + section.toUpperCase() + "] " + message);
    }

    /**
     * Log específico para consultas SQL, incluye descripción y sentencia.
     *
     * @param section Contexto
     * @param descripcion Explicación de lo que se hace
     * @param sql Consulta SQL
     */
    public static void logQuery(String section, String descripcion, String sql) {
        logger.info("[" + section.toUpperCase() + "] CONSULTA → " + descripcion
                + "\n[" + section.toUpperCase() + "] SQL: " + sql);
    }

    /**
     * Registra un error grave con detalle de excepción.
     *
     * @param section Módulo o sección
     * @param message Descripción del error
     * @param throwable Excepción lanzada
     */
    public static void logError(String section, String message, Throwable throwable) {
        logger.log(Level.SEVERE, "[" + section.toUpperCase() + "] ERROR → " + message, throwable);
    }

    // ========================
    // MÉTODOS GENERALES (sin sección)
    // ========================
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
