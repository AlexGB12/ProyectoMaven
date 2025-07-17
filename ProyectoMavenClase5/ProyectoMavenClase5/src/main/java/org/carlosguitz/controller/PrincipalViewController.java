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
public class PrincipalViewController implements Initializable {
    
    @FXML
    private Button btnIrALogIn;
    @FXML
    private Button btnIrARegistro;
    
    private Main principal;
            

    public void setPrincipal(Main principal){
        this.principal = principal;
    }
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    public void IrALogIn (ActionEvent evento) {
        if (evento.getSource() == btnIrALogIn) {
            principal.iniciarSesion(); 

        }
    }
    
    @FXML
    public void IrARegistro(ActionEvent evento){
        if(evento.getSource() == btnIrARegistro){
            principal.Registrarte();
        }
    }
    
}
