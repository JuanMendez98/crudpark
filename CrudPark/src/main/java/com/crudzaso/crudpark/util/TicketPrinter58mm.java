package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.model.Operador;
import com.crudzaso.crudpark.model.Ticket;

import java.awt.image.BufferedImage;

/**
 * Impresora de tickets optimizada para impresoras térmicas de 58mm
 * Especialmente diseñada para Xprinter XP-58IIT
 */
public class TicketPrinter58mm {
    private final AppConfig config;
    private final QRGenerator qrGenerator;

    public TicketPrinter58mm() {
        this.config = AppConfig.getInstance();
        this.qrGenerator = new QRGenerator();
    }

    /**
     * Formatea un ticket para impresoras de 58mm de ancho
     * Ancho máximo: 32 caracteres (para fuente monoespaciada)
     */
    public String formatearTicket58mm(Ticket ticket, Operador operador, BufferedImage qrImage) {
        StringBuilder sb = new StringBuilder();
        int ancho = 32; // Ancho para impresoras de 58mm

        // Encabezado
        sb.append(centrar("================================", ancho)).append("\n");
        sb.append(centrar("CrudPark - Crudzaso", ancho)).append("\n");
        sb.append(centrar("================================", ancho)).append("\n");
        sb.append("\n");

        // Información del ticket
        sb.append("Folio: ").append(ticket.getFolio()).append("\n");
        sb.append("Ticket: #").append(String.format("%06d", ticket.getId())).append("\n");
        sb.append("Placa: ").append(ticket.getPlaca()).append("\n");
        sb.append("Tipo: ").append(ticket.getTipoCliente().getDescripcion()).append("\n");
        sb.append("\n");

        // Fecha y hora
        sb.append("Ingreso:\n");
        sb.append(DateUtils.formatearParaTicket(ticket.getFechaIngreso())).append("\n");
        sb.append("\n");

        // Operador
        sb.append("Operador: ").append(operador.getNombre()).append("\n");

        sb.append(centrar("--------------------------------", ancho)).append("\n");

        // Información del QR (solo texto, la imagen se imprime aparte)
        if (qrImage != null) {
            sb.append(centrar("ESCANEA EL CODIGO QR", ancho)).append("\n");
            sb.append(centrar("PARA SALIDA RAPIDA", ancho)).append("\n");
            sb.append("\n");

            // Contenido del QR en texto pequeño
            String qrContent = String.format("TKT:%d|%s", ticket.getId(), ticket.getPlaca());
            sb.append(centrar(qrContent, ancho)).append("\n");
        }

        sb.append(centrar("--------------------------------", ancho)).append("\n");
        sb.append("\n");

        // Mensaje de despedida
        sb.append(centrar("Gracias por su visita", ancho)).append("\n");
        sb.append(centrar("www.crudpark.com", ancho)).append("\n");
        sb.append("\n");

        sb.append(centrar("================================", ancho)).append("\n");

        // Espacio adicional para corte
        sb.append("\n\n\n");

        return sb.toString();
    }

    /**
     * Genera un QR optimizado para impresión en 58mm
     * Tamaño reducido para caber en papel de 58mm
     */
    public BufferedImage generarQRParaImpresion58mm(Ticket ticket) {
        long timestamp = ticket.getFechaIngreso().getTime();

        // Crear QR con tamaño optimizado para 58mm (100x100px)
        QRGenerator qrGen = new QRGenerator();
        return qrGen.generarQR(ticket.getId(), ticket.getPlaca(), timestamp);
    }

    private String centrar(String texto, int ancho) {
        if (texto.length() == 1) {
            return texto.repeat(Math.min(ancho, texto.length() * ancho));
        }

        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }

        int espaciosIzq = (ancho - texto.length()) / 2;
        int espaciosDer = ancho - texto.length() - espaciosIzq;

        return " ".repeat(Math.max(0, espaciosIzq)) + texto + " ".repeat(Math.max(0, espaciosDer));
    }

    /**
     * Vista previa del ticket (sin QR visual, solo texto)
     */
    public String generarVistaPrevia(Ticket ticket, Operador operador) {
        return formatearTicket58mm(ticket, operador, null);
    }

    /**
     * Imprime en consola (para debug)
     */
    public void imprimirEnConsola(Ticket ticket, Operador operador) {
        BufferedImage qr = generarQRParaImpresion58mm(ticket);
        String ticketFormateado = formatearTicket58mm(ticket, operador, qr);

        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║  SIMULACIÓN XPRINTER XP-58IIT  ║");
        System.out.println("╚════════════════════════════════╝\n");
        System.out.println(ticketFormateado);
    }
}
