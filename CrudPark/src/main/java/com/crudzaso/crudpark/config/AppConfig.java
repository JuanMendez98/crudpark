package com.crudzaso.crudpark.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para cargar y gestionar la configuración de la aplicación
 * desde el archivo application.properties
 */
public class AppConfig {
    private static AppConfig instance;
    private Properties properties;

    private AppConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                System.err.println("No se pudo encontrar application.properties");
                return;
            }

            properties.load(input);
            System.out.println("Configuración cargada exitosamente");

        } catch (IOException e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public int getTiempoGracia() {
        return Integer.parseInt(properties.getProperty("app.tiempo.gracia", "30"));
    }

    public String getImpresoraNombre() {
        return properties.getProperty("app.impresora.nombre", "Thermal Printer");
    }

    public int getTicketAncho() {
        return Integer.parseInt(properties.getProperty("app.ticket.ancho", "42"));
    }

    public String getTicketEmpresa() {
        return properties.getProperty("app.ticket.empresa", "CrudPark - Crudzaso");
    }

    public String getTicketMensajeDespedida() {
        return properties.getProperty("app.ticket.mensaje.despedida", "Gracias por su visita.");
    }

    public int getQrWidth() {
        return Integer.parseInt(properties.getProperty("qr.width", "200"));
    }

    public int getQrHeight() {
        return Integer.parseInt(properties.getProperty("qr.height", "200"));
    }

    public String getQrFormat() {
        return properties.getProperty("qr.format", "TICKET:%d|PLATE:%s|DATE:%d");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
