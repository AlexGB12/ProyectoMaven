package org.carlosguitz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static Conexion instancia;
    private Connection conexion;

    private static final String URL = "jdbc:mysql://localhost:3306/ProyectoNovaWearDB2?useSSL=false"; 
    private static final String USER = "root"; // <-- Cambia "root" si tu usuario es diferente
    private static final String PASSWORD = "admin"; // <-- Cambia "admin" si tu contraseña es diferente

    private static final String DRIVER = "com.mysql.jdbc.Driver"; // Usar el driver moderno de MySQL

    private Conexion() {
        conectar();
    }

    private void conectar() {
        try {

            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a ProyectoNovaWearDB");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC no encontrado. Asegúrate de tener 'mysql-connector-java' en tus dependencias.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());

            if (e.getSQLState().startsWith("28") || e.getSQLState().startsWith("08")) { 
                System.err.println("Verifica el nombre de la base de datos, el usuario y la contraseña.");
            }
            e.printStackTrace();
        } catch (Exception e) { 
            System.err.println("Error inesperado al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Conexion getInstance() { 
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {

            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar el estado de la conexión: " + ex.getMessage());
            ex.printStackTrace();
        }
        return conexion;
    }


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