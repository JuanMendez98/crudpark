package com.crudzaso.crudpark.model;

/**
 * Enum que representa el tipo de vehículo
 */
public enum TipoVehiculo {
    CARRO("Carro"),
    MOTO("Moto");

    private final String descripcion;

    TipoVehiculo(String descripcion) {
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
