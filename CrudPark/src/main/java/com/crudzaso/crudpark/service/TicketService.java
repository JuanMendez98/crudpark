package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.dao.PagoDAO;
import com.crudzaso.crudpark.dao.TicketDAO;
import com.crudzaso.crudpark.model.Pago;
import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.TipoCliente;
import com.crudzaso.crudpark.model.TipoVehiculo;
import com.crudzaso.crudpark.util.ValidationUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Servicio para gestionar lógica de negocio de tickets
 * ACTUALIZADO: Detecta automáticamente tipo de vehículo por placa
 */
public class TicketService {
    private final TicketDAO ticketDAO;
    private final PagoDAO pagoDAO;
    private final MensualidadService mensualidadService;

    public TicketService() {
        this.ticketDAO = new TicketDAO();
        this.pagoDAO = new PagoDAO();
        this.mensualidadService = new MensualidadService();
    }

    /**
     * Registra ingreso detectando automáticamente el tipo de vehículo por la placa
     */
    public Integer registrarIngreso(String placa, int operadorId) {
        if (placa == null || placa.trim().isEmpty()) {
            System.err.println("Placa inválida");
            return null;
        }

        // DETECCIÓN AUTOMÁTICA: por formato de placa
        // ABC123 = CARRO, ABC12A = MOTO
        TipoVehiculo tipoVehiculo = ValidationUtils.detectarTipoVehiculo(placa);

        return registrarIngreso(placa, operadorId, tipoVehiculo);
    }

    public Integer registrarIngreso(String placa, int operadorId, TipoVehiculo tipoVehiculo) {
        if (placa == null || placa.trim().isEmpty()) {
            System.err.println("Placa inválida");
            return null;
        }

        Ticket ticketAbierto = ticketDAO.findTicketAbiertoByPlaca(placa);
        if (ticketAbierto != null) {
            System.err.println("Ya existe un ticket abierto para la placa: " + placa);
            return null;
        }

        TipoCliente tipoCliente = mensualidadService.tieneMensualidadVigente(placa)
                ? TipoCliente.MENSUALIDAD
                : TipoCliente.INVITADO;

        // Generar folio único
        int nextFolioNumber = ticketDAO.getNextFolioNumber();
        String folio = String.format("TKT-%06d", nextFolioNumber);

        Timestamp fechaIngreso = Timestamp.valueOf(LocalDateTime.now());
        long timestamp = fechaIngreso.getTime();

        Ticket ticket = new Ticket();
        ticket.setFolio(folio);
        ticket.setPlaca(placa.toUpperCase());
        ticket.setTipoCliente(tipoCliente);
        ticket.setTipoVehiculo(tipoVehiculo);
        ticket.setFechaIngreso(fechaIngreso);
        ticket.setOperadorId(operadorId);

        // Generar contenido del QR antes de insertar (sin el ID aún)
        String qrCodeTemp = String.format("TICKET:PENDING|PLATE:%s|DATE:%d", placa.toUpperCase(), timestamp);
        ticket.setQrCode(qrCodeTemp);

        Integer ticketId = ticketDAO.insert(ticket);

        if (ticketId != null) {
            // Actualizar el QR con el ID real del ticket
            String qrCodeFinal = String.format("TICKET:%d|PLATE:%s|DATE:%d", ticketId, placa.toUpperCase(), timestamp);

            System.out.println("Ticket registrado exitosamente: #" + ticketId +
                    " - Folio: " + folio +
                    " - Tipo: " + tipoCliente +
                    " - Vehículo: " + tipoVehiculo);
        }

        return ticketId;
    }

    public boolean registrarSalida(int ticketId, BigDecimal montoPago,
                                    String metodoPago, int operadorId) {
        Ticket ticket = ticketDAO.findById(ticketId);

        if (ticket == null) {
            System.err.println("Ticket no encontrado: " + ticketId);
            return false;
        }

        if (ticket.getFechaSalida() != null) {
            System.err.println("El ticket ya tiene registrada una salida");
            return false;
        }

        Timestamp fechaSalida = Timestamp.valueOf(LocalDateTime.now());
        int tiempoEstadia = calcularTiempoEstadia(ticket.getFechaIngreso(), fechaSalida);

        boolean actualizacionExitosa = ticketDAO.updateSalida(ticketId, fechaSalida, tiempoEstadia, operadorId, montoPago);

        if (!actualizacionExitosa) {
            return false;
        }

        if (montoPago != null && montoPago.compareTo(BigDecimal.ZERO) > 0) {
            Pago pago = new Pago();
            pago.setTicketId(ticketId);
            pago.setMonto(montoPago);
            pago.setMetodoPago(metodoPago);
            pago.setFechaPago(fechaSalida);
            pago.setOperadorId(operadorId);

            Integer pagoId = pagoDAO.insert(pago);

            if (pagoId != null) {
                System.out.println("Pago registrado exitosamente: #" + pagoId +
                        " - Monto: $" + montoPago);
            }
        }

        System.out.println("Salida registrada exitosamente - Ticket: #" + ticketId +
                " - Tiempo: " + tiempoEstadia + " minutos");

        return true;
    }

    public int calcularTiempoEstadia(Timestamp fechaIngreso, Timestamp fechaSalida) {
        LocalDateTime ingreso = fechaIngreso.toLocalDateTime();
        LocalDateTime salida = fechaSalida.toLocalDateTime();
        return (int) ChronoUnit.MINUTES.between(ingreso, salida);
    }

    public Ticket buscarTicketAbierto(String placa) {
        return ticketDAO.findTicketAbiertoByPlaca(placa);
    }

    public Ticket buscarTicketPorId(int ticketId) {
        return ticketDAO.findById(ticketId);
    }
}
