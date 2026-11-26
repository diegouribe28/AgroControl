
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.RegistroSanitario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

public class RegistroSanitarioDAO {

    // REGISTRAR EVENTO SANITARIO

    public boolean registrar(RegistroSanitario registro)
            throws SQLException {

        String sql = "INSERT INTO registro_sanitario "
                + "(id_animal, id_usuario, fecha, tipo, descripcion) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, registro.getIdAnimal());
            ps.setInt(2, registro.getIdUsuario());
            ps.setDate(3, Date.valueOf(registro.getFecha()));
            ps.setString(4, registro.getTipo());
            ps.setString(5, registro.getDescripcion());

            return ps.executeUpdate() > 0;
        }
    }

    // LISTAR TODOS LOS REGISTROS SANITARIOS

    public List<RegistroSanitario> listar()
            throws SQLException {

        List<RegistroSanitario> registros =
                new ArrayList<>();

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, tipo, descripcion "
                + "FROM registro_sanitario "
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

    // CONSULTAR HISTORIAL SANITARIO POR ANIMAL

    public List<RegistroSanitario> listarPorAnimal(
            int idAnimal) throws SQLException {

        List<RegistroSanitario> registros =
                new ArrayList<>();

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, tipo, descripcion "
                + "FROM registro_sanitario "
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

    // BUSCAR REGISTRO SANITARIO POR ID

    public RegistroSanitario buscarPorId(
            int idRegistro) throws SQLException {

        String sql = "SELECT id_registro, id_animal, "
                + "id_usuario, fecha, tipo, descripcion "
                + "FROM registro_sanitario "
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

    // ACTUALIZAR REGISTRO SANITARIO

    public boolean actualizar(RegistroSanitario registro)
            throws SQLException {

        String sql = "UPDATE registro_sanitario SET "
                + "fecha = ?, "
                + "tipo = ?, "
                + "descripcion = ? "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(registro.getFecha()));
            ps.setString(2, registro.getTipo());
            ps.setString(3, registro.getDescripcion());
            ps.setInt(4, registro.getIdRegistro());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR REGISTRO SANITARIO

    public boolean eliminar(int idRegistro)
            throws SQLException {

        String sql = "DELETE FROM registro_sanitario "
                + "WHERE id_registro = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRegistro);

            return ps.executeUpdate() > 0;
        }
    }

    // CONTAR REGISTROS SANITARIOS DE UN ANIMAL

    public int contarRegistrosPorAnimal(int idAnimal)
            throws SQLException {

        String sql = "SELECT COUNT(*) AS total "
                + "FROM registro_sanitario "
                + "WHERE id_animal = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAnimal);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("total");
                }
            }
        }

        return 0;
    }

    // CONVERTIR UNA FILA EN UN OBJETO JAVA

    private RegistroSanitario convertirRegistro(
            ResultSet rs) throws SQLException {

        return new RegistroSanitario(

                rs.getInt("id_registro"),
                rs.getInt("id_animal"),
                rs.getInt("id_usuario"),
                rs.getDate("fecha").toLocalDate(),
                rs.getString("tipo"),
                rs.getString("descripcion")
        );
    }
}
