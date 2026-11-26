
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.HistorialEtapa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialEtapaDAO {

    // REGISTRAR HISTORIAL DENTRO DE UNA TRANSACCIÓN

    public boolean registrar(
            Connection con,
            HistorialEtapa historial)
            throws SQLException {

        String sql = "INSERT INTO historial_etapa "
                + "(id_animal, id_usuario, etapa_anterior, "
                + "etapa_nueva, observaciones) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(1, historial.getIdAnimal());

            ps.setInt(2, historial.getIdUsuario());

            if (historial.getEtapaAnterior() == null) {

                ps.setNull(3, Types.VARCHAR);

            } else {

                ps.setString(3,
                        historial.getEtapaAnterior());
            }

            ps.setString(4, historial.getEtapaNueva());

            ps.setString(5, historial.getObservaciones());

            return ps.executeUpdate() == 1;
        }
    }

    // LISTAR TODOS LOS HISTORIALES

    public List<HistorialEtapa> listar()
            throws SQLException {

        List<HistorialEtapa> registros =
                new ArrayList<>();

        String sql = "SELECT * FROM historial_etapa "
                + "ORDER BY fecha_cambio DESC, "
                + "id_historial DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                registros.add(convertirRegistro(rs));
            }
        }

        return registros;
    }

    // CONSULTAR HISTORIAL POR ANIMAL

    public List<HistorialEtapa> listarPorAnimal(
            int idAnimal)
            throws SQLException {

        List<HistorialEtapa> registros =
                new ArrayList<>();

        String sql = "SELECT * FROM historial_etapa "
                + "WHERE id_animal = ? "
                + "ORDER BY fecha_cambio DESC, "
                + "id_historial DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, idAnimal);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    registros.add(convertirRegistro(rs));
                }
            }
        }

        return registros;
    }

    // CONSULTAR HISTORIAL POR ETAPA NUEVA

    public List<HistorialEtapa> listarPorEtapa(
            String etapa)
            throws SQLException {

        List<HistorialEtapa> registros =
                new ArrayList<>();

        String sql = "SELECT * FROM historial_etapa "
                + "WHERE etapa_nueva = ? "
                + "ORDER BY fecha_cambio DESC, "
                + "id_historial DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, etapa);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    registros.add(convertirRegistro(rs));
                }
            }
        }

        return registros;
    }

    // CONSULTAR HISTORIAL POR RANGO DE FECHAS

    public List<HistorialEtapa> listarPorFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws SQLException {

        List<HistorialEtapa> registros =
                new ArrayList<>();

        String sql = "SELECT * FROM historial_etapa "
                + "WHERE fecha_cambio >= ? "
                + "AND fecha_cambio < ? "
                + "ORDER BY fecha_cambio DESC, "
                + "id_historial DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(
                    fechaInicio.atStartOfDay()));

            ps.setTimestamp(2, Timestamp.valueOf(
                    fechaFin.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    registros.add(convertirRegistro(rs));
                }
            }
        }

        return registros;
    }

    // BUSCAR HISTORIAL POR ID

    public HistorialEtapa buscarPorId(
            int idHistorial)
            throws SQLException {

        String sql = "SELECT * FROM historial_etapa "
                + "WHERE id_historial = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, idHistorial);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return convertirRegistro(rs);
                }
            }
        }

        return null;
    }

    // CONTAR CAMBIOS REALES DE ETAPA POR ANIMAL

    public int contarCambiosPorAnimal(int idAnimal)
            throws SQLException {

        String sql = "SELECT COUNT(*) AS total "
                + "FROM historial_etapa "
                + "WHERE id_animal = ? "
                + "AND etapa_anterior IS NOT NULL "
                + "AND etapa_anterior <> etapa_nueva";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, idAnimal);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("total");
                }
            }
        }

        return 0;
    }

    // CONVERTIR FILA EN OBJETO JAVA

    private HistorialEtapa convertirRegistro(
            ResultSet rs)
            throws SQLException {

        return new HistorialEtapa(

                rs.getInt("id_historial"),

                rs.getInt("id_animal"),

                rs.getInt("id_usuario"),

                rs.getString("etapa_anterior"),

                rs.getString("etapa_nueva"),

                rs.getTimestamp("fecha_cambio")
                        .toLocalDateTime(),

                rs.getString("observaciones")
        );
    }
}
