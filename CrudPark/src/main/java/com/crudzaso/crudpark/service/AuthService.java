package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.dao.OperadorDAO;
import com.crudzaso.crudpark.model.Operador;

/**
 * Servicio para gestionar la autenticación de operadores
 */
public class AuthService {
    private final OperadorDAO operadorDAO;
    private Operador operadorActual;

    public AuthService() {
        this.operadorDAO = new OperadorDAO();
    }

    public Operador login(String usuario, String password) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        Operador operador = operadorDAO.findByUsuarioAndPassword(usuario, password);

        if (operador != null && operador.getActivo()) {
            operadorActual = operador;
            System.out.println("Login exitoso: " + operador.getNombre());
            return operador;
        }

        return null;
    }

    public void logout() {
        if (operadorActual != null) {
            System.out.println("Logout: " + operadorActual.getNombre());
            operadorActual = null;
        }
    }

    public Operador getOperadorActual() {
        return operadorActual;
    }

    public boolean isAuthenticated() {
        return operadorActual != null;
    }
}
