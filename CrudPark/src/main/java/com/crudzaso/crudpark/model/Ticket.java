package com.crudzaso.crudpark.model;

import java.sql.Timestamp;

/**
 * Clase modelo que representa un ticket de ingreso/salida
 */
public class Ticket {
    private Integer id;
    private String folio;
    private String placa;
    private TipoCliente tipoCliente;
    private TipoVehiculo tipoVehiculo;
    private Timestamp fechaIngreso;
    private Timestamp fechaSalida;
    private Integer operadorId;
    private Integer tiempoEstadia;
    private String qrCode;

    public Ticket() {
    }

    public Ticket(Integer id, String placa, TipoCliente tipoCliente, TipoVehiculo tipoVehiculo,
                  Timestamp fechaIngreso, Timestamp fechaSalida, Integer operadorId, Integer tiempoEstadia) {
        this.id = id;
        this.placa = placa;
        this.tipoCliente = tipoCliente;
        this.tipoVehiculo = tipoVehiculo;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.operadorId = operadorId;
        this.tiempoEstadia = tiempoEstadia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public Timestamp getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Timestamp fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Timestamp getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Timestamp fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Integer operadorId) {
        this.operadorId = operadorId;
    }

    public Integer getTiempoEstadia() {
        return tiempoEstadia;
    }

    public void setTiempoEstadia(Integer tiempoEstadia) {
        this.tiempoEstadia = tiempoEstadia;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", placa='" + placa + '\'' +
                ", tipoCliente=" + tipoCliente +
                ", fechaIngreso=" + fechaIngreso +
                ", fechaSalida=" + fechaSalida +
                ", operadorId=" + operadorId +
                ", tiempoEstadia=" + tiempoEstadia +
                '}';
    }
}
