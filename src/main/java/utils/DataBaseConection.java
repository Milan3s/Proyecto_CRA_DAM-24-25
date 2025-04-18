package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConection {

    private static final String URL = "jdbc:mysql://localhost:3306/cra_dispositivos";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LoggerUtils.logInfo("Conexión establecida correctamente con la base de datos.");
            return connection;
        } catch (SQLException e) {
            LoggerUtils.logError("Error al conectar con la base de datos: " + e.getMessage(), e);
            throw e; // Lo relanzamos para que quien lo llame sepa manejarlo
        }
    }
}
