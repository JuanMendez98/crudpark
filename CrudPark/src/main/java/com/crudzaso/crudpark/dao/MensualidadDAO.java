package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.model.Mensualidad;

import java.sql.*;

/**
 * DAO para gestionar operaciones de la tabla mensualidades
 */
public class MensualidadDAO {

    public Mensualidad findVigenteByPlaca(String placa) {
        String sql = "SELECT id, nombre, email, placa, fecha_inicio, fecha_fin " +
                "FROM mensualidades " +
                "WHERE placa = ? AND CURRENT_DATE BETWEEN fecha_inicio AND fecha_fin";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMensualidad(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar mensualidad vigente: " + e.getMessage());
        }

        return null;
    }

    public Mensualidad findById(int id) {
        String sql = "SELECT id, nombre, email, placa, fecha_inicio, fecha_fin " +
                "FROM mensualidades WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMensualidad(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar mensualidad por ID: " + e.getMessage());
        }

        return null;
    }

    private Mensualidad mapResultSetToMensualidad(ResultSet rs) throws SQLException {
        Mensualidad mensualidad = new Mensualidad();
        mensualidad.setId(rs.getInt("id"));
        mensualidad.setNombre(rs.getString("nombre"));
        mensualidad.setEmail(rs.getString("email"));
        mensualidad.setPlaca(rs.getString("placa"));
        mensualidad.setFechaInicio(rs.getDate("fecha_inicio"));
        mensualidad.setFechaFin(rs.getDate("fecha_fin"));
        return mensualidad;
    }
}
