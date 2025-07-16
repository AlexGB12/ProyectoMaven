package org.carlosguitz.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.carlosguitz.system.Main; // Asegúrate de que esta ruta sea correcta

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

// Importa tu modelo de Producto y la clase de conexión
import org.carlosguitz.model.Producto; // La clase Producto que definimos arriba
import org.carlosguitz.db.Conexion; // Tu clase de conexión Singleton

public class ProductosController implements Initializable {

    private Main principal; // Referencia a la clase Main
    private ObservableList<Producto> listaProductos; // Lista observable para la TableView

    // Enumeración para controlar el estado de las operaciones CRUD
    private enum Operacion {
        NINGUNO, AGREGAR, ACTUALIZAR
    }
    private Operacion tipoDeOperacion = Operacion.NINGUNO;

    // --- Elementos FXML (IDs deben coincidir con ProductosView.fxml) ---
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> idProductoColumn;
    @FXML private TableColumn<Producto, String> nombreProductoColumn;
    @FXML private TableColumn<Producto, String> descripcionProductoColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TableColumn<Producto, Double> precioProductoColumn;

    @FXML private TextField idProductoTextField;
    @FXML private TextField nombreProductoTextField;
    @FXML private TextField descripcionProductoTextField;
    @FXML private TextField stockTextField;
    @FXML private TextField precioProductoTextField;
    @FXML private TextField txtBuscar;

    @FXML private Button btnAñadir;
    @FXML private Button btnEliminar;
    @FXML private Button btnModificar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnBuscar;
    @FXML private Button btnAtras;
    @FXML private Button btnSiguiente;

    // --- Setter para la referencia a Main ---
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Vista de Productos cargada y controlada.");
        configurarColumnas();
        cargarTablaProductos();
        desactivarControles(); // Inicia con los TextField deshabilitados
        // Añade un listener para cuando se selecciona un elemento en la tabla
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && tipoDeOperacion == Operacion.NINGUNO) {
                cargarProductoEnTextField();
            }
        });
    }

    // --- Métodos de configuración de la tabla y carga de datos ---
    public void configurarColumnas() {
        idProductoColumn.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        nombreProductoColumn.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        descripcionProductoColumn.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        precioProductoColumn.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
    }

    public void cargarTablaProductos() {
        listaProductos = FXCollections.observableArrayList(listarProductos());
        tablaProductos.setItems(listaProductos);
        if (!listaProductos.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst(); // Selecciona la primera fila por defecto
            cargarProductoEnTextField(); // Carga el primer producto en los TextField
        } else {
            limpiarControles(); // Limpia los campos si no hay productos
        }
    }

    public void cargarProductoEnTextField() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            idProductoTextField.setText(String.valueOf(productoSeleccionado.getIdProducto()));
            nombreProductoTextField.setText(productoSeleccionado.getNombreProducto());
            descripcionProductoTextField.setText(productoSeleccionado.getDescripcionProducto());
            stockTextField.setText(String.valueOf(productoSeleccionado.getStock()));
            precioProductoTextField.setText(String.valueOf(productoSeleccionado.getPrecioProducto()));
        } else {
            limpiarControles(); // Limpia si no hay selección
        }
    }

    // --- Métodos de interacción con la Base de Datos ---
    public ArrayList<Producto> listarProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstance().getConexion(); // Usa getInstance()
            CallableStatement enunciado = conexion.prepareCall("{call sp_ListarProductos()}");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                productos.add(new Producto(
                    resultado.getInt("ID"),
                    resultado.getString("NOMBRE"),
                    resultado.getString("DESCRIPCION"),
                    resultado.getInt("STOCK"),
                    resultado.getDouble("PRECIO")
                ));
            }
        } catch (SQLException ex) {
            System.out.println("Error al cargar los productos desde MySQL: " + ex.getMessage());
            ex.printStackTrace();
        }
        return productos;
    }

    public Producto obtenerModeloProductoDeUI() {
        // ID no se toma del TextField si es para agregar (auto_increment)
        // Se usa para actualizar o eliminar
        int id = 0;
        if (!idProductoTextField.getText().isEmpty()) {
            id = Integer.parseInt(idProductoTextField.getText());
        }
        
        String nombre = nombreProductoTextField.getText();
        String descripcion = descripcionProductoTextField.getText();
        int stock = 0;
        double precio = 0.0;

        try {
            stock = Integer.parseInt(stockTextField.getText());
        } catch (NumberFormatException e) {
            System.err.println("Stock inválido: " + stockTextField.getText());
            // Considera mostrar una alerta al usuario
        }
        
        try {
            precio = Double.parseDouble(precioProductoTextField.getText());
        } catch (NumberFormatException e) {
            System.err.println("Precio inválido: " + precioProductoTextField.getText());
            // Considera mostrar una alerta al usuario
        }

        return new Producto(id, nombre, descripcion, stock, precio);
    }

    public void agregarProducto() {
        Producto nuevoProducto = obtenerModeloProductoDeUI();
        try {
            CallableStatement enunciado = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarProducto(?,?,?,?)}");
            enunciado.setString(1, nuevoProducto.getNombreProducto());
            enunciado.setString(2, nuevoProducto.getDescripcionProducto());
            enunciado.setInt(3, nuevoProducto.getStock());
            enunciado.setDouble(4, nuevoProducto.getPrecioProducto());
            enunciado.execute();
            // No necesitas agregar a listaProductos aquí, cargarTablaProductos lo hará
        } catch (SQLException ex) {
            System.out.println("Error al agregar producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void actualizarProducto() {
        Producto productoAActualizar = obtenerModeloProductoDeUI();
        try {
            CallableStatement enunciado = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarProducto(?,?,?,?,?)}");
            enunciado.setInt(1, productoAActualizar.getIdProducto());
            enunciado.setString(2, productoAActualizar.getNombreProducto());
            enunciado.setString(3, productoAActualizar.getDescripcionProducto());
            enunciado.setInt(4, productoAActualizar.getStock());
            enunciado.setDouble(5, productoAActualizar.getPrecioProducto());
            enunciado.execute();
            // No necesitas actualizar en listaProductos aquí, cargarTablaProductos lo hará
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarProducto() {
        Producto productoAEliminar = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoAEliminar != null) {
            try {
                CallableStatement enunciado = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarProducto(?)}");
                enunciado.setInt(1, productoAEliminar.getIdProducto());
                enunciado.execute();
                // No necesitas remover de listaProductos aquí, cargarTablaProductos lo hará
            } catch (SQLException e) {
                System.out.println("Error al eliminar producto: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Debe seleccionar un producto para eliminar.");
        }
    }

    // --- Métodos de UI para estado de controles y limpieza ---
    public void limpiarControles() {
        idProductoTextField.clear();
        nombreProductoTextField.clear();
        descripcionProductoTextField.clear();
        stockTextField.clear();
        precioProductoTextField.clear();
        txtBuscar.clear();
    }

    public void activarControles() {
        // idProductoTextField.setDisable(false); // Generalmente, el ID es autoincremental y no se edita manualmente
        nombreProductoTextField.setDisable(false);
        descripcionProductoTextField.setDisable(false);
        stockTextField.setDisable(false);
        precioProductoTextField.setDisable(false);

        btnAñadir.setDisable(true);
        btnEliminar.setDisable(true);
        btnModificar.setDisable(true);
        btnBuscar.setDisable(true);
        txtBuscar.setDisable(true);
        btnAtras.setDisable(true);
        btnSiguiente.setDisable(true);
        tablaProductos.setDisable(true);

        btnGuardar.setDisable(false);
        btnCancelar.setDisable(false);
    }

    public void desactivarControles() {
        idProductoTextField.setDisable(true); // El ID siempre deshabilitado para edición
        nombreProductoTextField.setDisable(true);
        descripcionProductoTextField.setDisable(true);
        stockTextField.setDisable(true);
        precioProductoTextField.setDisable(true);

        btnAñadir.setDisable(false);
        btnEliminar.setDisable(false);
        btnModificar.setDisable(false);
        btnBuscar.setDisable(false);
        txtBuscar.setDisable(false);
        btnAtras.setDisable(false);
        btnSiguiente.setDisable(false);
        tablaProductos.setDisable(false);

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
    }

    // --- Manejo de Acciones de Botones ---
    @FXML
    private void btnNuevoAction(ActionEvent event) {
        limpiarControles();
        activarControles();
        nombreProductoTextField.requestFocus(); // Pone el foco en el primer campo de entrada
        tipoDeOperacion = Operacion.AGREGAR;
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {
        if (tablaProductos.getSelectionModel().getSelectedItem() != null) {
            activarControles();
            tipoDeOperacion = Operacion.ACTUALIZAR;
        } else {
            // Puedes mostrar una alerta o mensaje al usuario si no hay selección
            System.out.println("Debe seleccionar un producto para modificar.");
        }
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        if (tablaProductos.getSelectionModel().getSelectedItem() != null) {
            // Confirmación antes de eliminar (opcional pero recomendado)
            // Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar este producto?");
            // Optional<ButtonType> result = alert.showAndWait();
            // if (result.isPresent() && result.get() == ButtonType.OK) {
                eliminarProducto();
                cargarTablaProductos(); // Recargar datos para reflejar la eliminación
                limpiarControles();
            // }
        } else {
            System.out.println("Debe seleccionar un producto para eliminar.");
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        if (tipoDeOperacion == Operacion.AGREGAR) {
            agregarProducto();
        } else if (tipoDeOperacion == Operacion.ACTUALIZAR) {
            actualizarProducto();
        }
        cargarTablaProductos(); // Recargar datos para ver los cambios aplicados
        limpiarControles();
        desactivarControles();
        tipoDeOperacion = Operacion.NINGUNO; // Resetear el estado
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        limpiarControles();
        desactivarControles();
        tipoDeOperacion = Operacion.NINGUNO; // Resetear el estado
        cargarProductoEnTextField(); // Vuelve a cargar el producto seleccionado si lo había
    }

    @FXML
    private void btnAnteriorAction(ActionEvent event) {
        int currentIndex = tablaProductos.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            tablaProductos.getSelectionModel().select(currentIndex - 1);
            cargarProductoEnTextField();
        }
    }

    @FXML
    private void btnSiguienteAction(ActionEvent event) {
        int currentIndex = tablaProductos.getSelectionModel().getSelectedIndex();
        if (currentIndex < listaProductos.size() - 1) {
            tablaProductos.getSelectionModel().select(currentIndex + 1);
            cargarProductoEnTextField();
        }
    }

    @FXML
    private void buscarProducto(ActionEvent event) {
        String textoBusqueda = txtBuscar.getText().trim();
        if (!textoBusqueda.isEmpty()) {
            ObservableList<Producto> productosFiltrados = FXCollections.observableArrayList();
            for (Producto p : listaProductos) {
                // Puedes buscar por nombre o descripción, o ambos
                if (p.getNombreProducto().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                    p.getDescripcionProducto().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                    productosFiltrados.add(p);
                }
            }
            tablaProductos.setItems(productosFiltrados);
            if (!productosFiltrados.isEmpty()) {
                tablaProductos.getSelectionModel().selectFirst();
                cargarProductoEnTextField();
            } else {
                limpiarControles();
            }
        } else {
            // Si el campo de búsqueda está vacío, recarga todos los productos
            cargarTablaProductos();
        }
    }

    // --- Métodos de Navegación ---
    @FXML
    public void regresarMenuPrincipal(ActionEvent event) {
        if (principal != null) {
            principal.inicio(); // Asumiendo que 'inicio()' es el método para volver al menú principal en tu clase Main
        }
    }
}