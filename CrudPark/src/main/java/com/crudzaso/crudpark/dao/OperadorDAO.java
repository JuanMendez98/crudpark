package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.model.Operador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para gestionar operaciones de la tabla operadores
 */
public class OperadorDAO {

    public Operador findByUsuarioAndPassword(String usuario, String password) {
        String sql = "SELECT id, nombre, usuario, password, email, activo " +
                "FROM operadores " +
                "WHERE usuario = ? AND password = ? AND activo = true";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOperador(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar operador: " + e.getMessage());
        }

        return null;
    }

    public Operador findById(int id) {
        String sql = "SELECT id, nombre, usuario, password, email, activo " +
                "FROM operadores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOperador(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar operador por ID: " + e.getMessage());
        }

        return null;
    }

    private Operador mapResultSetToOperador(ResultSet rs) throws SQLException {
        Operador operador = new Operador();
        operador.setId(rs.getInt("id"));
        operador.setNombre(rs.getString("nombre"));
        operador.setUsuario(rs.getString("usuario"));
        operador.setPassword(rs.getString("password"));
        operador.setEmail(rs.getString("email"));
        operador.setActivo(rs.getBoolean("activo"));
        return operador;
    }
}
