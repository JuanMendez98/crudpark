package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.TipoCliente;
import com.crudzaso.crudpark.model.TipoVehiculo;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar operaciones de la tabla tickets
 * ACTUALIZADO para coincidir con el schema real de la BD
 */
public class TicketDAO {

    public Integer insert(Ticket ticket) {
        String sql = "INSERT INTO tickets (folio, placa, tipo_cliente, tipo_vehiculo, fecha_ingreso, operador_ingreso_id, qr_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getFolio());
            stmt.setString(2, ticket.getPlaca());
            stmt.setString(3, ticket.getTipoCliente().name());
            stmt.setString(4, ticket.getTipoVehiculo() != null ? ticket.getTipoVehiculo().name() : "CARRO");
            stmt.setTimestamp(5, ticket.getFechaIngreso());
            stmt.setInt(6, ticket.getOperadorId());
            stmt.setString(7, ticket.getQrCode());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar ticket: " + e.getMessage());
        }

        return null;
    }

    public Ticket findTicketAbiertoByPlaca(String placa) {
        String sql = "SELECT id, folio, placa, tipo_cliente, tipo_vehiculo, fecha_ingreso, fecha_salida, " +
                "operador_ingreso_id, operador_salida_id, qr_code " +
                "FROM tickets " +
                "WHERE placa = ? AND fecha_salida IS NULL";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar ticket abierto: " + e.getMessage());
        }

        return null;
    }

    public boolean updateSalida(int ticketId, Timestamp fechaSalida, int tiempoEstadia, 
                                int operadorSalidaId, BigDecimal montoCobrado) {
        String sql = "UPDATE tickets SET fecha_salida = ?, operador_salida_id = ?, estado = 'CERRADO', monto_cobrado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, fechaSalida);
            stmt.setInt(2, operadorSalidaId);
            stmt.setBigDecimal(3, montoCobrado != null ? montoCobrado : BigDecimal.ZERO);
            stmt.setInt(4, ticketId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar salida de ticket: " + e.getMessage());
            return false;
        }
    }

    public Ticket findById(int id) {
        String sql = "SELECT id, folio, placa, tipo_cliente, tipo_vehiculo, fecha_ingreso, fecha_salida, " +
                "operador_ingreso_id, operador_salida_id, qr_code " +
                "FROM tickets WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar ticket por ID: " + e.getMessage());
        }

        return null;
    }

    public List<Ticket> findTicketsAbiertos() {
        String sql = "SELECT id, folio, placa, tipo_cliente, tipo_vehiculo, fecha_ingreso, fecha_salida, " +
                "operador_ingreso_id, operador_salida_id, qr_code " +
                "FROM tickets " +
                "WHERE fecha_salida IS NULL " +
                "ORDER BY fecha_ingreso DESC";

        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar tickets abiertos: " + e.getMessage());
        }

        return tickets;
    }

    public int getNextFolioNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(folio FROM 5) AS INTEGER)), 0) + 1 AS next_number " +
                     "FROM tickets WHERE folio LIKE 'TKT-%'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("next_number");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente número de folio: " + e.getMessage());
        }

        return 1;
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setFolio(rs.getString("folio"));
        ticket.setPlaca(rs.getString("placa"));
        ticket.setTipoCliente(TipoCliente.valueOf(rs.getString("tipo_cliente")));

        String tipoVehiculoStr = rs.getString("tipo_vehiculo");
        if (tipoVehiculoStr != null) {
            ticket.setTipoVehiculo(TipoVehiculo.valueOf(tipoVehiculoStr));
        }

        ticket.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
        ticket.setFechaSalida(rs.getTimestamp("fecha_salida"));
        ticket.setOperadorId(rs.getInt("operador_ingreso_id"));
        ticket.setQrCode(rs.getString("qr_code"));

        return ticket;
    }
}
