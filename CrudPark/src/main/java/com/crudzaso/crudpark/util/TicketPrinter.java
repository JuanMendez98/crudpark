package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.model.Operador;
import com.crudzaso.crudpark.model.Ticket;

import java.awt.image.BufferedImage;

/**
 * Clase para formatear e imprimir tickets
 */
public class TicketPrinter {
    private final AppConfig config;
    private final QRGenerator qrGenerator;

    public TicketPrinter() {
        this.config = AppConfig.getInstance();
        this.qrGenerator = new QRGenerator();
    }

    public String formatearTicket(Ticket ticket, Operador operador, BufferedImage qrImage) {
        StringBuilder sb = new StringBuilder();
        int ancho = config.getTicketAncho();

        sb.append(centrar("=", ancho)).append("\n");
        sb.append(centrar(config.getTicketEmpresa(), ancho)).append("\n");
        sb.append(centrar("=", ancho)).append("\n");
        sb.append(String.format("Folio: %s%n", ticket.getFolio() != null ? ticket.getFolio() : "N/A"));
        sb.append(String.format("Ticket #: %06d%n", ticket.getId()));
        sb.append(String.format("Placa: %s%n", ticket.getPlaca()));
        sb.append(String.format("Tipo: %s%n", ticket.getTipoCliente().getDescripcion()));
        sb.append(String.format("Ingreso: %s%n", DateUtils.formatearParaTicket(ticket.getFechaIngreso())));
        sb.append(String.format("Operador: %s%n", operador.getNombre()));
        sb.append(centrar("-", ancho)).append("\n");

        if (qrImage != null) {
            sb.append("QR: TICKET:").append(ticket.getId())
                    .append("|PLATE:").append(ticket.getPlaca())
                    .append("|DATE:").append(ticket.getFechaIngreso().getTime())
                    .append("\n");
        }

        sb.append(centrar("-", ancho)).append("\n");
        sb.append(centrar(config.getTicketMensajeDespedida(), ancho)).append("\n");
        sb.append(centrar("=", ancho)).append("\n");

        return sb.toString();
    }

    public void imprimirTicket(Ticket ticket, Operador operador) {
        long timestamp = ticket.getFechaIngreso().getTime();
        BufferedImage qrImage = qrGenerator.generarQR(
                ticket.getId(),
                ticket.getPlaca(),
                timestamp
        );

        String ticketFormateado = formatearTicket(ticket, operador, qrImage);

        System.out.println("\n" + ticketFormateado);

        System.out.println("[SIMULACIÓN] Enviando a impresora: " +
                config.getImpresoraNombre());
    }

    private String centrar(String texto, int ancho) {
        if (texto.length() == 1) {
            return texto.repeat(ancho);
        }

        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }

        int espaciosIzq = (ancho - texto.length()) / 2;
        int espaciosDer = ancho - texto.length() - espaciosIzq;

        return " ".repeat(espaciosIzq) + texto + " ".repeat(espaciosDer);
    }

    public String generarTicketParaVista(Ticket ticket, Operador operador) {
        return formatearTicket(ticket, operador, null);
    }
}
