package com.crudzaso.crudpark.model;

import java.math.BigDecimal;

/**
 * Clase modelo que representa una tarifa de parqueadero
 */
public class Tarifa {
    private Integer id;
    private String nombre;
    private TipoVehiculo tipoVehiculo;
    private BigDecimal valorHora;
    private Integer tiempoGracia;
    private Boolean activo;

    public Tarifa() {
    }

    public Tarifa(Integer id, String nombre, TipoVehiculo tipoVehiculo, BigDecimal valorHora,
                  Integer tiempoGracia, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.tipoVehiculo = tipoVehiculo;
        this.valorHora = valorHora;
        this.tiempoGracia = tiempoGracia;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public BigDecimal getValorHora() {
        return valorHora;
    }

    public void setValorHora(BigDecimal valorHora) {
        this.valorHora = valorHora;
    }

    public Integer getTiempoGracia() {
        return tiempoGracia;
    }

    public void setTiempoGracia(Integer tiempoGracia) {
        this.tiempoGracia = tiempoGracia;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Tarifa{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipoVehiculo=" + tipoVehiculo +
                ", valorHora=" + valorHora +
                ", tiempoGracia=" + tiempoGracia +
                ", activo=" + activo +
                '}';
    }
}
