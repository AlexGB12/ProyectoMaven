package org.carlosguitz.system;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.carlosguitz.controller.*;

public class Main extends Application {

    private static String URL_VIEW = "/view/";
    private Stage escenarioPrincipal; 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenario) throws Exception {
        this.escenarioPrincipal = escenario;
        inicio();
        escenarioPrincipal.show(); 
    }

    public FXMLLoader cambiarEscena(String fxml, double ancho, double alto) {
        FXMLLoader cargadorFXML = null;
        try {
            cargadorFXML = new FXMLLoader(getClass().getResource(URL_VIEW + fxml));
            Parent archivoFXML = cargadorFXML.load();
            Scene escena = new Scene(archivoFXML, ancho, alto);
            escenarioPrincipal.setScene(escena);
 
        } catch (IOException ex) {
            System.out.println("Error al cambiar: " + ex.getMessage());
            ex.printStackTrace();
        }
        return cargadorFXML;
    }

    public void inicio() {

        PrincipalViewController ic = cambiarEscena("PrincipalView.fxml", 800, 600).getController();
        ic.setPrincipal(this);
    }

    public void mostrarProductos() {
        ProductosController vvc = cambiarEscena("VistaProductos.fxml", 1000, 700).getController();
        vvc.setPrincipal(this);
    }

    public void iniciarSesion() {
        InicioController vc = cambiarEscena("InicioView.fxml", 404, 500).getController();
        vc.setPrincipal(this);
    }

    public void Registrarte() {
        RegistroController c = cambiarEscena("RegistroView.fxml", 450, 848).getController();
        c.setPrincipal(this);
    }
}
