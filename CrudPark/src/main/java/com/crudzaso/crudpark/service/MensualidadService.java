package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.dao.MensualidadDAO;
import com.crudzaso.crudpark.model.Mensualidad;

/**
 * Servicio para gestionar lógica de negocio de mensualidades
 */
public class MensualidadService {
    private final MensualidadDAO mensualidadDAO;

    public MensualidadService() {
        this.mensualidadDAO = new MensualidadDAO();
    }

    public boolean tieneMensualidadVigente(String placa) {
        Mensualidad mensualidad = mensualidadDAO.findVigenteByPlaca(placa);
        return mensualidad != null && mensualidad.isVigente();
    }

    public Mensualidad obtenerMensualidadVigente(String placa) {
        return mensualidadDAO.findVigenteByPlaca(placa);
    }
}
