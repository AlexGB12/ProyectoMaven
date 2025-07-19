package org.carlosguitz.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * @author alexg
 */
public class RegistroController implements Initializable {

    // --- ELEMENTOS FXML DE LA VISTA (COINCIDEN CON TU REGISTROVIEW.FXML) ---
    @FXML
    private TextField nombreTextField; // fx:id="nombreTextField"
    @FXML
    private TextField correoTextField; // fx:id="correoTextField"
    @FXML
    private TextField usuarioTextField; // fx:id="usuarioTextField"
    @FXML
    private PasswordField contrasenaPasswordField; // fx:id="contrasenaPasswordField"
    @FXML
    private PasswordField confirmarContrasenaPasswordField; // fx:id="confirmarContrasenaPasswordField"

    @FXML
    private Button btnRegistrar; // CAMBIO: Usaremos este para la acción de registro
    @FXML
    private Button btnCancelar;

    // --- INSTANCIA DE LA CLASE PRINCIPAL ---
    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Puedes añadir inicializaciones aquí si es necesario
    }

    // --- MÉTODOS DE ACCIÓN ---

    // CAMBIO DE NOMBRE: Este método se llamará cuando se presione el botón "Registrarse"
    @FXML
    public void handleRegistrar(ActionEvent evento) { // Cambié el nombre del método para mayor claridad
        if (evento.getSource() == btnRegistrar) { // Aseguramos que el botón sea btnRegistrar

            String nombreCompleto = nombreTextField.getText();
            String correoElectronico = correoTextField.getText();
            String nombreUsuario = usuarioTextField.getText();
            String contrasena = contrasenaPasswordField.getText();
            String confirmarContrasena = confirmarContrasenaPasswordField.getText();

            // 1. Validaciones de campos
            if (nombreCompleto.isEmpty() || correoElectronico.isEmpty() || nombreUsuario.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                mostrarAlerta("Campos Vacíos", "Por favor, completa todos los campos para el registro.", Alert.AlertType.WARNING);
                return;
            }
            if (!contrasena.equals(confirmarContrasena)) {
                mostrarAlerta("Contraseñas no Coinciden", "Las contraseñas ingresadas no coinciden.", Alert.AlertType.ERROR);
                return;
            }

            // 2. Intentar registrar el nuevo usuario en la base de datos
            try (Connection c = Conexion.getInstance().getConexion();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO Usuarios (nombre_completo, correo_electronico, nombre_usuario, contrasena) VALUES (?, ?, ?, ?)")) {

                // *** ¡ATENCIÓN SEGURIDAD! ***
                // En un proyecto real, NUNCA guardes contraseñas en texto plano.
                // Usarías un algoritmo de hashing (ej: BCrypt) para guardar el hash.
                // Ejemplo (requiere librería jBCrypt): String hashedContrasena = BCrypt.hashpw(contrasena, BCrypt.gensalt());
                // ps.setString(4, hashedContrasena);
                // Por ahora, para que sea funcional de inmediato:
                ps.setString(1, nombreCompleto);
                ps.setString(2, correoElectronico);
                ps.setString(3, nombreUsuario);
                ps.setString(4, contrasena); // Contraseña sin hash. ¡Cambia esto!

                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta("Registro Exitoso", "¡Cuenta creada con éxito!", Alert.AlertType.INFORMATION);
                    principal.iniciarSesion(); // Redirige al usuario a la pantalla de login
                } else {
                    mostrarAlerta("Error de Registro", "No se pudo crear la cuenta. Inténtalo de nuevo.", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                // Código de error SQLState para violación de restricción UNIQUE (nombre de usuario o correo duplicado)
                if (e.getSQLState().equals("23000")) {
                    // Puedes refinar este mensaje para indicar si es el usuario o el correo
                    mostrarAlerta("Datos Duplicados", "El nombre de usuario o el correo electrónico ya están registrados.", Alert.AlertType.ERROR);
                } else {
                    mostrarAlerta("Error de Base de Datos", "Ocurrió un error al registrar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
                e.printStackTrace();
            }
        }
    }

    // Este método ya estaba, es para el botón "Cancelar"
    @FXML
    public void Regresar(ActionEvent evento) {
        if (evento.getSource() == btnCancelar) {
            principal.inicio(); // Regresa a la vista inicial (PrincipalView.fxml, según tu Main)
        }
    }

    // --- MÉTODO DE UTILIDAD PARA MOSTRAR ALERTAS ---
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}