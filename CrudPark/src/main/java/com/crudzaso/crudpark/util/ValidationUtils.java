package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.model.TipoVehiculo;

import java.util.regex.Pattern;

/**
 * Utilidades para validación de datos
 * Actualizado para soportar placas de carros y motos
 */
public class ValidationUtils {

    // Patrón para placas de CARRO: 3 letras + 3 números (ABC123)
    private static final Pattern PLACA_CARRO_PATTERN = Pattern.compile("^[A-Z]{3}\\d{3}$");

    // Patrón para placas de MOTO: 3 letras + 2 números + 1 letra (ABC12A)
    private static final Pattern PLACA_MOTO_PATTERN = Pattern.compile("^[A-Z]{3}\\d{2}[A-Z]$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Valida si la placa es válida (carro o moto)
     */
    public static boolean isPlacaValida(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }
        String placaUpper = placa.trim().toUpperCase();
        return PLACA_CARRO_PATTERN.matcher(placaUpper).matches()
            || PLACA_MOTO_PATTERN.matcher(placaUpper).matches();
    }

    /**
     * Valida si la placa es de CARRO (ABC123)
     */
    public static boolean isPlacaCarro(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }
        return PLACA_CARRO_PATTERN.matcher(placa.trim().toUpperCase()).matches();
    }

    /**
     * Valida si la placa es de MOTO (ABC12A)
     */
    public static boolean isPlacaMoto(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }
        return PLACA_MOTO_PATTERN.matcher(placa.trim().toUpperCase()).matches();
    }

    /**
     * Detecta automáticamente el tipo de vehículo por el formato de placa
     */
    public static TipoVehiculo detectarTipoVehiculo(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return TipoVehiculo.CARRO; // Por defecto
        }

        String placaUpper = placa.trim().toUpperCase();

        if (PLACA_MOTO_PATTERN.matcher(placaUpper).matches()) {
            return TipoVehiculo.MOTO;
        } else if (PLACA_CARRO_PATTERN.matcher(placaUpper).matches()) {
            return TipoVehiculo.CARRO;
        }

        return TipoVehiculo.CARRO; // Por defecto si no coincide
    }

    /**
     * Devuelve el formato esperado según el tipo de vehículo
     */
    public static String getFormatoPlaca(TipoVehiculo tipo) {
        return tipo == TipoVehiculo.MOTO ? "ABC12A" : "ABC123";
    }

    public static boolean isEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static String formatearPlaca(String placa) {
        if (placa == null) {
            return null;
        }
        return placa.trim().toUpperCase();
    }

    public static boolean isStringVacia(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNumeroPositivo(Number numero) {
        return numero != null && numero.doubleValue() > 0;
    }
}
