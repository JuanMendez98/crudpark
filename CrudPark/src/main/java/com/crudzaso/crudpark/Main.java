package com.crudzaso.crudpark;

import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.ui.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Clase principal de la aplicación CrudPark
 * Sistema de gestión de parqueadero
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    CrudPark - Sistema de Parqueadero");
        System.out.println("===========================================");
        System.out.println();

        inicializarConfiguracion();

        if (verificarConexionBD()) {
            iniciarInterfazGrafica();
        } else {
            System.err.println("No se pudo establecer conexión con la base de datos.");
            System.err.println("Verifique la configuración en application.properties");
            System.exit(1);
        }
    }

    private static void inicializarConfiguracion() {
        System.out.println("Inicializando configuración...");
        AppConfig config = AppConfig.getInstance();

        System.out.println("Base de datos: " + config.getDbUrl());
        System.out.println("Usuario: " + config.getDbUser());
        System.out.println("Tiempo de gracia: " + config.getTiempoGracia() + " minutos");
        System.out.println();
    }

    private static boolean verificarConexionBD() {
        System.out.println("Verificando conexión a base de datos...");

        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            boolean isConnected = dbConnection.testConnection();

            if (isConnected) {
                System.out.println("Conexión a base de datos exitosa");
                System.out.println();
                return true;
            } else {
                System.err.println("Error: No se pudo conectar a la base de datos");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void iniciarInterfazGrafica() {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
                System.out.println("Look and Feel: FlatLaf Light aplicado");

            } catch (Exception e) {
                System.err.println("No se pudo aplicar FlatLaf, usando Look and Feel del sistema");
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("Iniciando interfaz gráfica...");
            System.out.println();

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
