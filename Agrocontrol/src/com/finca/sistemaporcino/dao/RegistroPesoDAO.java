
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.RegistroPeso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

public class RegistroPesoDAO {

    // REGISTRAR UN NUEVO PESAJE

    public boolean registrar(RegistroPeso registro)
            throws SQLException {

        String sql = "INSERT INTO registro_peso "
                + "(id_animal, id_usuario, fecha, peso, observaciones) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, registro.getIdAnimal());
            ps.setInt(2, registro.getIdUsuario());
            ps.setDate(3, Date.valueOf(registro.getFecha()));
            ps.setBigDecimal(4, registro.getPeso());
            ps.setString(5, registro.getObservaciones());

            return ps.executeUpdate() > 0;
        }
    }

    // LISTAR TODOS LOS REGISTROS DE PESO

    public List<RegistroPeso> listar()
            throws SQLException {

        List<RegistroPeso> registros = new ArrayList<>();

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, peso, observaciones "
                + "FROM registro_peso "
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

    // CONSULTAR EL HISTORIAL DE PESO DE UN ANIMAL

    public List<RegistroPeso> listarPorAnimal(int idAnimal)
            throws SQLException {

        List<RegistroPeso> registros = new ArrayList<>();

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, peso, observaciones "
                + "FROM registro_peso "
                + "WHERE id_animal = ? "
                + "ORDER BY fecha ASC, id_registro ASC";

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

    // BUSCAR UN REGISTRO POR SU ID

    public RegistroPeso buscarPorId(int idRegistro)
            throws SQLException {

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, peso, observaciones "
                + "FROM registro_peso "
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

    // ACTUALIZAR UN REGISTRO DE PESO

    public boolean actualizar(RegistroPeso registro)
            throws SQLException {

        String sql = "UPDATE registro_peso SET "
                + "fecha = ?, "
                + "peso = ?, "
                + "observaciones = ? "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(registro.getFecha()));
            ps.setBigDecimal(2, registro.getPeso());
            ps.setString(3, registro.getObservaciones());
            ps.setInt(4, registro.getIdRegistro());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR UN REGISTRO DE PESO

    public boolean eliminar(int idRegistro)
            throws SQLException {

        String sql = "DELETE FROM registro_peso "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRegistro);

            return ps.executeUpdate() > 0;
        }
    }

    // CONVERTIR UNA FILA DE POSTGRESQL EN UN OBJETO JAVA

    private RegistroPeso convertirRegistro(ResultSet rs)
            throws SQLException {

        return new RegistroPeso(

                rs.getInt("id_registro"),
                rs.getInt("id_animal"),
                rs.getInt("id_usuario"),
                rs.getDate("fecha").toLocalDate(),
                rs.getBigDecimal("peso"),
                rs.getString("observaciones")
        );
    }
}
