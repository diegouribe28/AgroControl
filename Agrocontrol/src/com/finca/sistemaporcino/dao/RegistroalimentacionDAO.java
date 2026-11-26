
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.RegistroAlimentacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RegistroAlimentacionDAO {

    // REGISTRAR ALIMENTACIÓN

    public boolean registrar(RegistroAlimentacion registro)
            throws SQLException {

        String sql = "INSERT INTO registro_alimentacion "
                + "(id_animal, id_usuario, fecha, tipo_alimento, "
                + "cantidad_kg, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, registro.getIdAnimal());
            ps.setInt(2, registro.getIdUsuario());

            ps.setTimestamp(3,
                    Timestamp.valueOf(registro.getFecha()));

            ps.setString(4, registro.getTipoAlimento());
            ps.setBigDecimal(5, registro.getCantidadKg());
            ps.setString(6, registro.getObservaciones());

            return ps.executeUpdate() > 0;
        }
    }

    // LISTAR TODOS LOS REGISTROS

    public List<RegistroAlimentacion> listar()
            throws SQLException {

        List<RegistroAlimentacion> registros =
                new ArrayList<>();

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, tipo_alimento, "
                + "cantidad_kg, observaciones "
                + "FROM registro_alimentacion "
                + "ORDER BY fecha DESC, id_registro DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                registros.add(convertirRegistro(rs));
            }
        }

        return registros;
    }

    // CONSULTAR HISTORIAL DE UN ANIMAL

    public List<RegistroAlimentacion> listarPorAnimal(
            int idAnimal) throws SQLException {

        List<RegistroAlimentacion> registros =
                new ArrayList<>();

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, tipo_alimento, "
                + "cantidad_kg, observaciones "
                + "FROM registro_alimentacion "
                + "WHERE id_animal = ? "
                + "ORDER BY fecha DESC, id_registro DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAnimal);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    registros.add(convertirRegistro(rs));
                }
            }
        }

        return registros;
    }

    // BUSCAR REGISTRO POR ID

    public RegistroAlimentacion buscarPorId(
            int idRegistro) throws SQLException {

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, tipo_alimento, "
                + "cantidad_kg, observaciones "
                + "FROM registro_alimentacion "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRegistro);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return convertirRegistro(rs);
                }
            }
        }

        return null;
    }

    // ACTUALIZAR REGISTRO DE ALIMENTACIÓN

    public boolean actualizar(RegistroAlimentacion registro)
            throws SQLException {

        String sql = "UPDATE registro_alimentacion SET "
                + "fecha = ?, "
                + "tipo_alimento = ?, "
                + "cantidad_kg = ?, "
                + "observaciones = ? "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1,
                    Timestamp.valueOf(registro.getFecha()));

            ps.setString(2, registro.getTipoAlimento());
            ps.setBigDecimal(3, registro.getCantidadKg());
            ps.setString(4, registro.getObservaciones());
            ps.setInt(5, registro.getIdRegistro());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR REGISTRO DE ALIMENTACIÓN

    public boolean eliminar(int idRegistro)
            throws SQLException {

        String sql = "DELETE FROM registro_alimentacion "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRegistro);

            return ps.executeUpdate() > 0;
        }
    }

    // CALCULAR CANTIDAD TOTAL DE ALIMENTO

    public BigDecimal calcularConsumoTotal(int idAnimal)
            throws SQLException {

        String sql = "SELECT COALESCE(SUM(cantidad_kg), 0) "
                + "AS consumo_total "
                + "FROM registro_alimentacion "
                + "WHERE id_animal = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAnimal);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getBigDecimal("consumo_total");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    // CONVERTIR UNA FILA EN UN OBJETO JAVA

    private RegistroAlimentacion convertirRegistro(
            ResultSet rs) throws SQLException {

        return new RegistroAlimentacion(

                rs.getInt("id_registro"),
                rs.getInt("id_animal"),
                rs.getInt("id_usuario"),
                rs.getTimestamp("fecha").toLocalDateTime(),
                rs.getString("tipo_alimento"),
                rs.getBigDecimal("cantidad_kg"),
                rs.getString("observaciones")
        );
    }
}
