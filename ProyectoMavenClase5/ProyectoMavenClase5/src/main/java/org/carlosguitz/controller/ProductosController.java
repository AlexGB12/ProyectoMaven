package org.carlosguitz.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.carlosguitz.system.Main; 

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;


import org.carlosguitz.model.Producto; 
import org.carlosguitz.db.Conexion; 

public class ProductosController implements Initializable {

    private Main principal; 
    private ObservableList<Producto> listaProductos; 

    // Enumeración para controlar el estado de las operaciones CRUD
    private enum Operacion {
        NINGUNO, AGREGAR, ACTUALIZAR
    }
    private Operacion tipoDeOperacion = Operacion.NINGUNO;


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


    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Vista de Productos cargada y controlada.");
        configurarColumnas();
        cargarTablaProductos();
        desactivarControles(); 
        
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && tipoDeOperacion == Operacion.NINGUNO) {
                cargarProductoEnTextField();
            }
        });
    }


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
            tablaProductos.getSelectionModel().selectFirst(); 
            cargarProductoEnTextField(); 
        } else {
            limpiarControles(); 
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
            limpiarControles(); 
        }
    }


    public ArrayList<Producto> listarProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstance().getConexion(); 
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

        }
        
        try {
            precio = Double.parseDouble(precioProductoTextField.getText());
        } catch (NumberFormatException e) {
            System.err.println("Precio inválido: " + precioProductoTextField.getText());

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

            } catch (SQLException e) {
                System.out.println("Error al eliminar producto: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Debe seleccionar un producto para eliminar.");
        }
    }


    public void limpiarControles() {
        idProductoTextField.clear();
        nombreProductoTextField.clear();
        descripcionProductoTextField.clear();
        stockTextField.clear();
        precioProductoTextField.clear();
        txtBuscar.clear();
    }

    public void activarControles() {

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
        idProductoTextField.setDisable(true); 
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


    @FXML
    private void btnNuevoAction(ActionEvent event) {
        limpiarControles();
        activarControles();
        nombreProductoTextField.requestFocus(); 
        tipoDeOperacion = Operacion.AGREGAR;
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {
        if (tablaProductos.getSelectionModel().getSelectedItem() != null) {
            activarControles();
            tipoDeOperacion = Operacion.ACTUALIZAR;
        } else {
 
            System.out.println("Debe seleccionar un producto para modificar.");
        }
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        if (tablaProductos.getSelectionModel().getSelectedItem() != null) {

                eliminarProducto();
                cargarTablaProductos(); 
                limpiarControles();

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
        cargarTablaProductos(); 
        limpiarControles();
        desactivarControles();
        tipoDeOperacion = Operacion.NINGUNO; 
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        limpiarControles();
        desactivarControles();
        tipoDeOperacion = Operacion.NINGUNO; 
        cargarProductoEnTextField(); 
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

            cargarTablaProductos();
        }
    }


    @FXML
    public void regresarMenuPrincipal(ActionEvent event) {
        if (principal != null) {
            principal.inicio(); 
        }
    }
}