package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.model.Operador;
import com.crudzaso.crudpark.model.Ticket;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

/**
 * Helper class para imprimir tickets en impresora física real
 * Usa javax.print (Java Print API estándar)
 */
public class PrinterHelper {
    private final TicketPrinter ticketPrinter;
    private final QRGenerator qrGenerator;

    public PrinterHelper() {
        this.ticketPrinter = new TicketPrinter();
        this.qrGenerator = new QRGenerator();
    }

    /**
     * Imprime el ticket usando el diálogo de impresión del sistema
     */
    public void imprimirConDialogo(Ticket ticket, Operador operador) {
        PrinterJob job = PrinterJob.getPrinterJob();

        // Crear el contenido imprimible
        job.setPrintable(new TicketPrintable(ticket, operador));

        // Mostrar diálogo de impresión
        boolean doPrint = job.printDialog();

        if (doPrint) {
            try {
                job.print();
                System.out.println("Ticket enviado a imprimir: Folio " + ticket.getFolio());
            } catch (PrinterException e) {
                System.err.println("Error al imprimir: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Imprime directamente en la impresora predeterminada sin diálogo
     */
    public void imprimirDirecto(Ticket ticket, Operador operador) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new TicketPrintable(ticket, operador));

        try {
            job.print();
            System.out.println("Ticket impreso: Folio " + ticket.getFolio());
        } catch (PrinterException e) {
            System.err.println("Error al imprimir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Imprime en una impresora específica por nombre
     */
    public void imprimirEnImpresora(Ticket ticket, Operador operador, String nombreImpresora) {
        PrintService printService = encontrarImpresora(nombreImpresora);

        if (printService == null) {
            System.err.println("Impresora no encontrada: " + nombreImpresora);
            System.out.println("Impresoras disponibles:");
            listarImpresoras();
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();

        try {
            job.setPrintService(printService);
            job.setPrintable(new TicketPrintable(ticket, operador));
            job.print();
            System.out.println("Ticket impreso en: " + nombreImpresora);
        } catch (PrinterException e) {
            System.err.println("Error al imprimir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Imprime en Xprinter XP-58IIT de forma automática
     * Busca automáticamente la impresora por su nombre
     */
    public boolean imprimirEnXprinter(Ticket ticket, Operador operador) {
        // Posibles nombres que puede tener la Xprinter en el sistema
        String[] posiblesNombres = {"XP-58", "Xprinter", "XP58", "58IIT", "POS-58"};

        PrintService xprinter = null;

        // Buscar la impresora
        for (String nombre : posiblesNombres) {
            xprinter = encontrarImpresora(nombre);
            if (xprinter != null) {
                System.out.println("✓ Xprinter encontrada: " + xprinter.getName());
                break;
            }
        }

        if (xprinter == null) {
            System.err.println("✗ Xprinter XP-58IIT no encontrada");
            System.out.println("Asegúrate de que:");
            System.out.println("  1. La impresora esté conectada (USB)");
            System.out.println("  2. El driver esté instalado");
            System.out.println("  3. Esté encendida");
            System.out.println("\nImpresoras disponibles:");
            listarImpresoras();
            return false;
        }

        // Imprimir usando un formato optimizado para 58mm
        PrinterJob job = PrinterJob.getPrinterJob();

        try {
            job.setPrintService(xprinter);
            job.setPrintable(new TicketPrintable58mm(ticket, operador));
            job.print();

            System.out.println("✓ Ticket impreso exitosamente en Xprinter XP-58IIT");
            System.out.println("  Folio: " + ticket.getFolio());
            System.out.println("  Placa: " + ticket.getPlaca());

            return true;

        } catch (PrinterException e) {
            System.err.println("✗ Error al imprimir en Xprinter: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lista todas las impresoras disponibles en el sistema
     */
    public void listarImpresoras() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        System.out.println("\n=== Impresoras disponibles ===");
        for (int i = 0; i < printServices.length; i++) {
            System.out.println((i + 1) + ". " + printServices[i].getName());
        }
        System.out.println("==============================\n");
    }

    /**
     * Encuentra una impresora por nombre (búsqueda parcial case-insensitive)
     */
    private PrintService encontrarImpresora(String nombreBuscado) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : printServices) {
            if (service.getName().toLowerCase().contains(nombreBuscado.toLowerCase())) {
                return service;
            }
        }

        return null;
    }

    /**
     * Clase interna que define cómo se imprime el ticket
     */
    private class TicketPrintable implements Printable {
        private final Ticket ticket;
        private final Operador operador;
        private final BufferedImage qrImage;

        public TicketPrintable(Ticket ticket, Operador operador) {
            this.ticket = ticket;
            this.operador = operador;

            // Generar QR para incluir en la impresión
            long timestamp = ticket.getFechaIngreso().getTime();
            this.qrImage = qrGenerator.generarQR(ticket.getId(), ticket.getPlaca(), timestamp);
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            // Solo imprimimos una página
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;

            // Traducir al área imprimible
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Configurar fuente
            Font tituloFont = new Font("Monospaced", Font.BOLD, 12);
            Font normalFont = new Font("Monospaced", Font.PLAIN, 10);

            int y = 20;
            int lineHeight = 15;

            // Título
            g2d.setFont(tituloFont);
            g2d.drawString(centrarTexto("CrudPark - Crudzaso", 40), 10, y);
            y += lineHeight + 5;

            g2d.drawString(centrarTexto("=".repeat(40), 40), 10, y);
            y += lineHeight + 5;

            // Contenido
            g2d.setFont(normalFont);
            g2d.drawString("Folio: " + ticket.getFolio(), 10, y);
            y += lineHeight;

            g2d.drawString("Ticket #: " + String.format("%06d", ticket.getId()), 10, y);
            y += lineHeight;

            g2d.drawString("Placa: " + ticket.getPlaca(), 10, y);
            y += lineHeight;

            g2d.drawString("Tipo: " + ticket.getTipoCliente().getDescripcion(), 10, y);
            y += lineHeight;

            g2d.drawString("Ingreso: " + DateUtils.formatearParaTicket(ticket.getFechaIngreso()), 10, y);
            y += lineHeight;

            g2d.drawString("Operador: " + operador.getNombre(), 10, y);
            y += lineHeight + 5;

            // Separador
            g2d.drawString(centrarTexto("-".repeat(40), 40), 10, y);
            y += lineHeight + 5;

            // QR Code
            if (qrImage != null) {
                // Escalar el QR a un tamaño razonable para la impresión (100x100 px)
                int qrSize = 100;
                int qrX = ((int) pageFormat.getImageableWidth() - qrSize) / 2;
                g2d.drawImage(qrImage, qrX, y, qrSize, qrSize, null);
                y += qrSize + 10;

                // Texto del QR
                String qrText = String.format("TICKET:%d|PLATE:%s", ticket.getId(), ticket.getPlaca());
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));
                g2d.drawString(centrarTexto(qrText, 40), 10, y);
                y += lineHeight;
            }

            y += 5;
            g2d.setFont(normalFont);
            g2d.drawString(centrarTexto("-".repeat(40), 40), 10, y);
            y += lineHeight;

            g2d.drawString(centrarTexto("Gracias por su visita", 40), 10, y);
            y += lineHeight;

            g2d.drawString(centrarTexto("=".repeat(40), 40), 10, y);

            return PAGE_EXISTS;
        }

        private String centrarTexto(String texto, int ancho) {
            if (texto.length() >= ancho) {
                return texto.substring(0, ancho);
            }

            int espaciosIzq = (ancho - texto.length()) / 2;
            return " ".repeat(espaciosIzq) + texto;
        }
    }

    /**
     * Clase interna optimizada para imprimir en impresoras de 58mm (Xprinter XP-58IIT)
     */
    private class TicketPrintable58mm implements Printable {
        private final Ticket ticket;
        private final Operador operador;
        private final BufferedImage qrImage;

        public TicketPrintable58mm(Ticket ticket, Operador operador) {
            this.ticket = ticket;
            this.operador = operador;

            // Generar QR optimizado para 58mm (más pequeño: 80x80px)
            long timestamp = ticket.getFechaIngreso().getTime();
            this.qrImage = qrGenerator.generarQR(ticket.getId(), ticket.getPlaca(), timestamp);
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Fuentes más pequeñas para 58mm
            Font tituloFont = new Font("Monospaced", Font.BOLD, 10);
            Font normalFont = new Font("Monospaced", Font.PLAIN, 8);
            Font smallFont = new Font("Monospaced", Font.PLAIN, 7);

            int y = 15;
            int lineHeight = 12;

            // Título
            g2d.setFont(tituloFont);
            g2d.drawString(centrarTexto58mm("CrudPark - Crudzaso"), 5, y);
            y += lineHeight;

            g2d.drawString(centrarTexto58mm("=".repeat(32)), 5, y);
            y += lineHeight + 3;

            // Contenido
            g2d.setFont(normalFont);
            g2d.drawString("Folio: " + ticket.getFolio(), 5, y);
            y += lineHeight;

            g2d.drawString("Ticket: #" + String.format("%06d", ticket.getId()), 5, y);
            y += lineHeight;

            g2d.drawString("Placa: " + ticket.getPlaca(), 5, y);
            y += lineHeight;

            g2d.drawString("Tipo: " + ticket.getTipoCliente().getDescripcion(), 5, y);
            y += lineHeight + 3;

            // Fecha
            g2d.setFont(smallFont);
            String fecha = DateUtils.formatearParaTicket(ticket.getFechaIngreso());
            g2d.drawString(fecha, 5, y);
            y += lineHeight;

            g2d.setFont(normalFont);
            g2d.drawString("Op: " + operador.getNombre(), 5, y);
            y += lineHeight + 3;

            // Separador
            g2d.drawString(centrarTexto58mm("-".repeat(32)), 5, y);
            y += lineHeight;

            // QR Code - centrado y más pequeño para 58mm
            if (qrImage != null) {
                int qrSize = 80; // Más pequeño para 58mm
                int anchoImpresion = (int) pageFormat.getImageableWidth();
                int qrX = (anchoImpresion - qrSize) / 2;
                g2d.drawImage(qrImage, qrX, y, qrSize, qrSize, null);
                y += qrSize + 5;

                // Texto del QR
                g2d.setFont(smallFont);
                String qrText = "TKT:" + ticket.getId() + "|" + ticket.getPlaca();
                g2d.drawString(centrarTexto58mm(qrText), 5, y);
                y += lineHeight;
            }

            y += 3;
            g2d.setFont(normalFont);
            g2d.drawString(centrarTexto58mm("-".repeat(32)), 5, y);
            y += lineHeight;

            g2d.drawString(centrarTexto58mm("Gracias por su visita"), 5, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            g2d.drawString(centrarTexto58mm("www.crudpark.com"), 5, y);
            y += lineHeight;

            g2d.setFont(normalFont);
            g2d.drawString(centrarTexto58mm("=".repeat(32)), 5, y);

            return PAGE_EXISTS;
        }

        private String centrarTexto58mm(String texto) {
            int ancho = 32; // Ancho para 58mm
            if (texto.length() >= ancho) {
                return texto.substring(0, ancho);
            }

            int espaciosIzq = (ancho - texto.length()) / 2;
            return " ".repeat(espaciosIzq) + texto;
        }
    }
}
