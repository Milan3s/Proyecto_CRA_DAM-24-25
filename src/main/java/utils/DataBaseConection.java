package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConection {

    // Datos de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/cra_dispositivos";
    private static final String USER = "root"; // Usuario de la base de datos
    private static final String PASSWORD = ""; // Contraseña de la base de datos
    private static Connection connection = null;
    
    // Método para obtener la conexión
    public static Connection getConnection() {
        try {
            // Establecer la conexión
            if (connection == null) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error: No se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
        return connection;
    }

    // Método para cerrar la conexión
    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error: No se pudo cerrar la conexión.");
            e.printStackTrace();
        }
    }
}
