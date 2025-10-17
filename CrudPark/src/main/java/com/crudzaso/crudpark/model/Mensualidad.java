package com.crudzaso.crudpark.model;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Clase modelo que representa una mensualidad
 */
public class Mensualidad {
    private Integer id;
    private String nombre;
    private String email;
    private String placa;
    private Date fechaInicio;
    private Date fechaFin;

    public Mensualidad() {
    }

    public Mensualidad(Integer id, String nombre, String email, String placa, Date fechaInicio, Date fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.placa = placa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isVigente() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = fechaInicio.toLocalDate();
        LocalDate fin = fechaFin.toLocalDate();
        return !hoy.isBefore(inicio) && !hoy.isAfter(fin);
    }

    @Override
    public String toString() {
        return "Mensualidad{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", placa='" + placa + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                '}';
    }
}
