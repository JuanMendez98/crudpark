package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.model.Tarifa;
import com.crudzaso.crudpark.model.TipoVehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para gestionar operaciones de la tabla tarifas
 */
public class TarifaDAO {

    public Tarifa findActivaByTipoVehiculo(TipoVehiculo tipoVehiculo) {
        String sql = "SELECT id, nombre, tipo_vehiculo, valor_hora, tiempo_gracia, activo " +
                "FROM tarifas " +
                "WHERE tipo_vehiculo = ? AND activo = true";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoVehiculo.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTarifa(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar tarifa activa: " + e.getMessage());
        }

        return null;
    }

    public Tarifa findById(int id) {
        String sql = "SELECT id, nombre, tipo_vehiculo, valor_hora, tiempo_gracia, activo " +
                "FROM tarifas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTarifa(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar tarifa por ID: " + e.getMessage());
        }

        return null;
    }

    private Tarifa mapResultSetToTarifa(ResultSet rs) throws SQLException {
        Tarifa tarifa = new Tarifa();
        tarifa.setId(rs.getInt("id"));
        tarifa.setNombre(rs.getString("nombre"));
        tarifa.setTipoVehiculo(TipoVehiculo.valueOf(rs.getString("tipo_vehiculo")));
        tarifa.setValorHora(rs.getBigDecimal("valor_hora"));
        tarifa.setTiempoGracia(rs.getInt("tiempo_gracia"));
        tarifa.setActivo(rs.getBoolean("activo"));
        return tarifa;
    }
}
