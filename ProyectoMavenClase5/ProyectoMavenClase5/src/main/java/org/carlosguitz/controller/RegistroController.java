package org.carlosguitz.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.carlosguitz.system.Main;

/**
 * FXML Controller class
 *
 * @author alexg
 */
public class RegistroController implements Initializable {

    @FXML
    private Button btnInicioDeSesion;
    @FXML
    private Button btnCancelar;
    
    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void clicInicioSesion(ActionEvent evento) {
        if (evento.getSource() == btnInicioDeSesion) {
            principal.mostrarProductos();

        }
    }

    @FXML
    public void Regresar(ActionEvent evento) {
        if (evento.getSource() == btnCancelar) {
            principal.inicio();

        }
    }

}
