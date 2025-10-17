package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.model.Pago;

import java.sql.*;

/**
 * DAO para gestionar operaciones de la tabla pagos
 */
public class PagoDAO {

    public Integer insert(Pago pago) {
        String sql = "INSERT INTO pagos (ticket_id, monto, metodo_pago, fecha_pago, operador_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pago.getTicketId());
            stmt.setBigDecimal(2, pago.getMonto());
            stmt.setString(3, pago.getMetodoPago());
            stmt.setTimestamp(4, pago.getFechaPago());
            stmt.setInt(5, pago.getOperadorId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar pago: " + e.getMessage());
        }

        return null;
    }

    public Pago findByTicketId(int ticketId) {
        String sql = "SELECT id, ticket_id, monto, metodo_pago, fecha_pago, operador_id " +
                "FROM pagos WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPago(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar pago por ticket: " + e.getMessage());
        }

        return null;
    }

    public Pago findById(int id) {
        String sql = "SELECT id, ticket_id, monto, metodo_pago, fecha_pago, operador_id " +
                "FROM pagos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPago(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar pago por ID: " + e.getMessage());
        }

        return null;
    }

    private Pago mapResultSetToPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setId(rs.getInt("id"));
        pago.setTicketId(rs.getInt("ticket_id"));
        pago.setMonto(rs.getBigDecimal("monto"));
        pago.setMetodoPago(rs.getString("metodo_pago"));
        pago.setFechaPago(rs.getTimestamp("fecha_pago"));
        pago.setOperadorId(rs.getInt("operador_id"));
        return pago;
    }
}
