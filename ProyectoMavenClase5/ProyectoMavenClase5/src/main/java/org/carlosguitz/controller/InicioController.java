package org.carlosguitz.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert; 
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField; 
import javafx.scene.control.TextField; 
import org.carlosguitz.db.Conexion; 
import org.carlosguitz.system.Main;

/**
 * FXML Controller class
 *
 * @author informatica
 */
public class InicioController implements Initializable {

    @FXML
    private TextField txtUsuarioLogin; 
    @FXML
    private PasswordField pfContrasenaLogin; 

    @FXML
    private Button btnInicioDeSesion;
    @FXML
    private Button btnIrARegistro;

    // --- INSTANCIA DE LA CLASE PRINCIPAL ---
    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    // --- MÉTODOS DE ACCIÓN ---
    @FXML
    public void clicInicioSesion(ActionEvent evento) {
        if (evento.getSource() == btnInicioDeSesion) {
            String usuario = txtUsuarioLogin.getText();
            String contrasena = pfContrasenaLogin.getText();
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                mostrarAlerta("Campos Vacíos", "Por favor, ingresa tu usuario y contraseña.", Alert.AlertType.WARNING);
                return;
            }
            try (Connection c = Conexion.getInstance().getConexion();
                 PreparedStatement ps = c.prepareStatement("SELECT contrasena FROM Usuarios WHERE nombre_usuario = ?")) {

                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String contrasenaAlmacenada = rs.getString("contrasena");
                        if (contrasena.equals(contrasenaAlmacenada)) {
                            mostrarAlerta("Inicio de Sesión Exitoso", "¡Bienvenido, " + usuario + "!", Alert.AlertType.INFORMATION);
                            principal.mostrarProductos(); 
                        } else {
                            mostrarAlerta("Error de Credenciales", "Contraseña incorrecta.", Alert.AlertType.ERROR);
                        }
                    } else {
                        mostrarAlerta("Error de Credenciales", "Usuario no encontrado.", Alert.AlertType.ERROR);
                    }
                }

            } catch (SQLException e) {
                mostrarAlerta("Error de Base de Datos", "Ocurrió un error al intentar iniciar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void IrARegistro(ActionEvent evento) {
        if (evento.getSource() == btnIrARegistro) {
            principal.Registrarte(); 
        }
    }

   
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}