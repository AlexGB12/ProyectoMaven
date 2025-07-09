package org.carlosguitz.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modelo de datos para un Producto.
 * Utiliza propiedades de JavaFX para permitir la observación en TableView.
 */
public class Producto {
    private final IntegerProperty idProducto;
    private final StringProperty nombreProducto;
    private final StringProperty descripcionProducto;
    private final IntegerProperty stock;
    private final DoubleProperty precioProducto;

    // Constructor completo
    public Producto(int idProducto, String nombreProducto, String descripcionProducto, int stock, double precioProducto) {
        this.idProducto = new SimpleIntegerProperty(idProducto);
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.descripcionProducto = new SimpleStringProperty(descripcionProducto);
        this.stock = new SimpleIntegerProperty(stock);
        this.precioProducto = new SimpleDoubleProperty(precioProducto);
    }

    // Constructor para agregar (sin ID, ya que es auto_increment en la DB)
    public Producto(String nombreProducto, String descripcionProducto, int stock, double precioProducto) {
        this(0, nombreProducto, descripcionProducto, stock, precioProducto); // ID 0 o algún valor por defecto inicial
    }

    // --- Getters para las propiedades de JavaFX (necesarios para TableView) ---
    public IntegerProperty idProductoProperty() {
        return idProducto;
    }

    public StringProperty nombreProductoProperty() {
        return nombreProducto;
    }

    public StringProperty descripcionProductoProperty() {
        return descripcionProducto;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public DoubleProperty precioProductoProperty() {
        return precioProducto;
    }

    // --- Getters para obtener los valores directos ---
    public int getIdProducto() {
        return idProducto.get();
    }

    public String getNombreProducto() {
        return nombreProducto.get();
    }

    public String getDescripcionProducto() {
        return descripcionProducto.get();
    }

    public int getStock() {
        return stock.get();
    }

    public double getPrecioProducto() {
        return precioProducto.get();
    }

    // --- Setters para modificar los valores de las propiedades ---
    public void setIdProducto(int idProducto) {
        this.idProducto.set(idProducto);
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto.set(nombreProducto);
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto.set(descripcionProducto);
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto.set(precioProducto);
    }

    @Override
    public String toString() {
        return idProducto.get() + " / " + nombreProducto.get() + " / " + descripcionProducto.get();
    }
}