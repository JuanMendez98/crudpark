package com.crudzaso.crudpark.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para manejo de fechas y timestamps
 */
public class DateUtils {

    private static final DateTimeFormatter FORMATTER_FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter FORMATTER_FECHA_CORTA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATTER_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final DateTimeFormatter FORMATTER_TICKET =
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    public static String formatearFechaHora(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_FECHA_HORA);
    }

    public static String formatearFechaCorta(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_FECHA_CORTA);
    }

    public static String formatearHora(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_HORA);
    }

    public static String formatearParaTicket(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_TICKET);
    }

    public static Timestamp ahora() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static String formatearTiempoEstadia(int minutos) {
        if (minutos < 60) {
            return minutos + " minutos";
        }

        int horas = minutos / 60;
        int minutosRestantes = minutos % 60;

        if (minutosRestantes == 0) {
            return horas + (horas == 1 ? " hora" : " horas");
        }

        return horas + (horas == 1 ? " hora" : " horas") + " y " +
                minutosRestantes + " minutos";
    }
}
