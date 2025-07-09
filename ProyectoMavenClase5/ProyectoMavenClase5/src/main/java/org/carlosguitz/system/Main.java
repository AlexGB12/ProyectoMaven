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
    private Stage escenarioPrincipal; // Declara la instancia de Stage

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenario) throws Exception {
        this.escenarioPrincipal = escenario; // <--- IMPORTANTE: Asigna el escenario aquí
        // Carga la vista inicial a través de tu método 'inicio()' para que el controlador se inicialice correctamente.
        inicio();
        escenarioPrincipal.show(); // Muestra el escenario después de cargar la escena inicial
    }

    public FXMLLoader cambiarEscena(String fxml, double ancho, double alto) {
        FXMLLoader cargadorFXML = null;
        try {
            cargadorFXML = new FXMLLoader(getClass().getResource(URL_VIEW + fxml));
            Parent archivoFXML = cargadorFXML.load();
            Scene escena = new Scene(archivoFXML, ancho, alto);
            escenarioPrincipal.setScene(escena);
            // No necesitas escenarioPrincipal.show() aquí, solo en el start.
        } catch (IOException ex) {
            System.out.println("Error al cambiar: " + ex.getMessage());
            ex.printStackTrace();
        }
        return cargadorFXML;
    }

    public void inicio() {
        // En este caso, ya estamos en el inicio, así que cargamos InicioView.fxml
        // Este método asegura que la referencia 'principal' se pase al controlador
        InicioController ic = cambiarEscena("InicioView.fxml", 400, 500).getController();
        ic.setPrincipal(this);
    }

    // Sugerencia: Cambia el nombre de este método a algo como 'mostrarProductos'
    public void mostrarProductos() { // <-- Renombrado para ser más idiomático
        ProductosController vvc = cambiarEscena("VistaProductos.fxml", 833, 492).getController(); // <--- Asegúrate de usar ".fxml"
        vvc.setPrincipal(this);
    }
}