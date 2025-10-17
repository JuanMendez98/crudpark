package com.crudzaso.crudpark.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Clase modelo que representa un pago realizado
 */
public class Pago {
    private Integer id;
    private Integer ticketId;
    private BigDecimal monto;
    private String metodoPago;
    private Timestamp fechaPago;
    private Integer operadorId;

    public Pago() {
    }

    public Pago(Integer id, Integer ticketId, BigDecimal monto, String metodoPago,
                Timestamp fechaPago, Integer operadorId) {
        this.id = id;
        this.ticketId = ticketId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.operadorId = operadorId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Timestamp getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Timestamp fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Integer getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Integer operadorId) {
        this.operadorId = operadorId;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", fechaPago=" + fechaPago +
                ", operadorId=" + operadorId +
                '}';
    }
}
