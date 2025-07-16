package org.carlosguitz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static Conexion instancia;
    private Connection conexion;


    private static final String URL = "jdbc:mysql://127.0.0.1:3306/ProyectoNovaWearDB?useSSL=false"; 
    private static final String USER = "quintom"; // <-- Cambia "root" si tu usuario es diferente
    private static final String PASSWORD = "admin"; // <-- Cambia "admin" si tu contraseña es diferente

    private static final String DRIVER = "com.mysql.jdbc.Driver"; // Usar el driver moderno de MySQL

    private Conexion() {
        conectar();
    }

    private void conectar() {
        try {
            // Carga el driver JDBC (asegúrate de tener el conector MySQL en tu pom.xml)
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a ProyectoNovaWearDB");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC no encontrado. Asegúrate de tener 'mysql-connector-java' en tus dependencias.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            // Imprime un mensaje más específico si el error es por credenciales incorrectas o DB no existente
            if (e.getSQLState().startsWith("28") || e.getSQLState().startsWith("08")) { // SQLState para auth o conexión
                System.err.println("Verifica el nombre de la base de datos, el usuario y la contraseña.");
            }
            e.printStackTrace();
        } catch (Exception e) { // Captura otras posibles excepciones generales
            System.err.println("Error inesperado al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Conexion getInstance() { // Renombrado a 'getInstance' para consistencia
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            // Reconecta si la conexión es nula o está cerrada
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar el estado de la conexión: " + ex.getMessage());
            ex.printStackTrace();
        }
        return conexion;
    }

    // Opcional: un método para cerrar explícitamente la conexión
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión a la base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}