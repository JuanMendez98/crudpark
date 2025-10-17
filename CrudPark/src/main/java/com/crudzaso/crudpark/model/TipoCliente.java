package com.crudzaso.crudpark.model;

/**
 * Enum que representa el tipo de cliente en el parqueadero
 */
public enum TipoCliente {
    MENSUALIDAD("Mensualidad"),
    INVITADO("Invitado");

    private final String descripcion;

    TipoCliente(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
