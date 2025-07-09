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
 * @author informatica
 */
public class InicioController implements Initializable {

    @FXML
    private Button btnInicioDeSesion;

    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    public void clicInicioSesion(ActionEvent evento) {
        if (evento.getSource() == btnInicioDeSesion) {
            principal.mostrarProductos(); // Use the new method name

        }
    }

}
