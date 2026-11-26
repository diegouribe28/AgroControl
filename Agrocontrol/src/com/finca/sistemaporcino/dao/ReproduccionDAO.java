
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.Reproduccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReproduccionDAO {

    // REGISTRAR REPRODUCCIÓN

    public boolean registrar(Reproduccion reproduccion)
            throws SQLException {

        String sql = "INSERT INTO reproduccion "
                + "(id_cerda, id_usuario, fecha_servicio, "
                + "fecha_probable_parto, fecha_parto_real, "
                + "resultado, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reproduccion.getIdCerda());

            ps.setInt(2, reproduccion.getIdUsuario());

            ps.setDate(3, Date.valueOf(
                    reproduccion.getFechaServicio()));

            asignarFecha(ps, 4,
                    reproduccion.getFechaProbableParto());

            asignarFecha(ps, 5,
                    reproduccion.getFechaPartoReal());

            ps.setString(6, reproduccion.getResultado());

            ps.setString(7, reproduccion.getObservaciones());

            return ps.executeUpdate() > 0;
        }
    }

    // LISTAR TODOS LOS REGISTROS

    public List<Reproduccion> listar()
            throws SQLException {

        List<Reproduccion> registros =
                new ArrayList<>();

        String sql = "SELECT * FROM reproduccion "
                + "ORDER BY fecha_servicio DESC, "
                + "id_reproduccion DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                registros.add(convertirRegistro(rs));
            }
        }

        return registros;
    }

    // CONSULTAR HISTORIAL REPRODUCTIVO POR CERDA

    public List<Reproduccion> listarPorCerda(int idCerda)
            throws SQLException {

        List<Reproduccion> registros =
                new ArrayList<>();

        String sql = "SELECT * FROM reproduccion "
                + "WHERE id_cerda = ? "
                + "ORDER BY fecha_servicio DESC, "
                + "id_reproduccion DESC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCerda);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    registros.add(convertirRegistro(rs));
                }
            }
        }

        return registros;
    }

    // BUSCAR REGISTRO POR ID

    public Reproduccion buscarPorId(int idReproduccion)
            throws SQLException {

        String sql = "SELECT * FROM reproduccion "
                + "WHERE id_reproduccion = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReproduccion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return convertirRegistro(rs);
                }
            }
        }

        return null;
    }

    // ACTUALIZAR REGISTRO REPRODUCTIVO

    public boolean actualizar(Reproduccion reproduccion)
            throws SQLException {

        String sql = "UPDATE reproduccion SET "
                + "fecha_servicio = ?, "
                + "fecha_probable_parto = ?, "
                + "fecha_parto_real = ?, "
                + "resultado = ?, "
                + "observaciones = ? "
                + "WHERE id_reproduccion = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(
                    reproduccion.getFechaServicio()));

            asignarFecha(ps, 2,
                    reproduccion.getFechaProbableParto());

            asignarFecha(ps, 3,
                    reproduccion.getFechaPartoReal());

            ps.setString(4, reproduccion.getResultado());

            ps.setString(5, reproduccion.getObservaciones());

            ps.setInt(6,
                    reproduccion.getIdReproduccion());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR REGISTRO REPRODUCTIVO

    public boolean eliminar(int idReproduccion)
            throws SQLException {

        String sql = "DELETE FROM reproduccion "
                + "WHERE id_reproduccion = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReproduccion);

            return ps.executeUpdate() > 0;
        }
    }

    // CONSULTAR PRÓXIMOS PARTOS ESTIMADOS

    public List<Reproduccion> listarProximosPartos()
            throws SQLException {

        List<Reproduccion> registros =
                new ArrayList<>();

        String sql = "SELECT r.* FROM reproduccion r "
                + "INNER JOIN animal a "
                + "ON r.id_cerda = a.id_animal "
                + "WHERE r.resultado = 'PENDIENTE' "
                + "AND r.fecha_probable_parto >= CURRENT_DATE "
                + "AND a.estado = 'ACTIVO' "
                + "AND a.sexo = 'HEMBRA' "
                + "ORDER BY r.fecha_probable_parto ASC, "
                + "r.id_reproduccion ASC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                registros.add(convertirRegistro(rs));
            }
        }

        return registros;
    }

    // CONSULTAR PARTOS PENDIENTES CON FECHA VENCIDA

    public List<Reproduccion> listarPartosVencidos()
            throws SQLException {

        List<Reproduccion> registros =
                new ArrayList<>();

        String sql = "SELECT r.* FROM reproduccion r "
                + "INNER JOIN animal a "
                + "ON r.id_cerda = a.id_animal "
                + "WHERE r.resultado = 'PENDIENTE' "
                + "AND r.fecha_probable_parto < CURRENT_DATE "
                + "AND a.estado = 'ACTIVO' "
                + "AND a.sexo = 'HEMBRA' "
                + "ORDER BY r.fecha_probable_parto ASC, "
                + "r.id_reproduccion ASC";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                registros.add(convertirRegistro(rs));
            }
        }

        return registros;
    }

    // CONTAR PARTOS REGISTRADOS POR CERDA

    public int contarPartosPorCerda(int idCerda)
            throws SQLException {

        String sql = "SELECT COUNT(*) AS total "
                + "FROM reproduccion "
                + "WHERE id_cerda = ? "
                + "AND resultado = 'PARTO'";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCerda);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("total");
                }
            }
        }

        return 0;
    }

    // VERIFICAR SI EXISTE UN SERVICIO PENDIENTE

    public boolean existePendiente(
            int idCerda, int idExcluir)
            throws SQLException {

        String sql = "SELECT 1 FROM reproduccion "
                + "WHERE id_cerda = ? "
                + "AND resultado = 'PENDIENTE' "
                + "AND id_reproduccion <> ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCerda);
            ps.setInt(2, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    // ASIGNAR FECHAS OPCIONALES

    private void asignarFecha(
            PreparedStatement ps,
            int posicion,
            LocalDate fecha)
            throws SQLException {

        if (fecha == null) {

            ps.setNull(posicion, Types.DATE);

        } else {

            ps.setDate(posicion, Date.valueOf(fecha));
        }
    }

    // LEER FECHAS QUE PUEDEN SER NULAS

    private LocalDate obtenerFecha(
            ResultSet rs, String columna)
            throws SQLException {

        Date fecha = rs.getDate(columna);

        if (fecha == null) {

            return null;
        }

        return fecha.toLocalDate();
    }

    // CONVERTIR FILA EN OBJETO JAVA

    private Reproduccion convertirRegistro(ResultSet rs)
            throws SQLException {

        return new Reproduccion(

                rs.getInt("id_reproduccion"),

                rs.getInt("id_cerda"),

                rs.getInt("id_usuario"),

                obtenerFecha(rs, "fecha_servicio"),

                obtenerFecha(rs, "fecha_probable_parto"),

                obtenerFecha(rs, "fecha_parto_real"),

                rs.getString("resultado"),

                rs.getString("observaciones")
        );
    }
}
