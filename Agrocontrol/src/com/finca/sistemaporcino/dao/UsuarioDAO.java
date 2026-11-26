
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // REGISTRAR USUARIO
    public boolean registrar(Usuario usuario) throws SQLException {

        String sql = "INSERT INTO usuario "
                + "(nombre, nombre_usuario, clave_hash, rol, activo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, usuario.getNombre());
            consulta.setString(2, usuario.getNombreUsuario());
            consulta.setString(3, usuario.getClaveHash());
            consulta.setString(4, usuario.getRol());
            consulta.setBoolean(5, usuario.isActivo());

            return consulta.executeUpdate() > 0;
        }
    }

    // LISTAR USUARIOS
    public List<Usuario> listar() throws SQLException {

        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT id_usuario, nombre, nombre_usuario, "
                + "rol, activo FROM usuario ORDER BY id_usuario";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                Usuario usuario = new Usuario();

                usuario.setIdUsuario(
                        resultado.getInt("id_usuario"));

                usuario.setNombre(
                        resultado.getString("nombre"));

                usuario.setNombreUsuario(
                        resultado.getString("nombre_usuario"));

                usuario.setRol(
                        resultado.getString("rol"));

                usuario.setActivo(
                        resultado.getBoolean("activo"));

                lista.add(usuario);
            }
        }

        return lista;
    }

    // ACTUALIZAR USUARIO
    public boolean actualizar(Usuario usuario) throws SQLException {

        String sql = "UPDATE usuario SET nombre = ?, "
                + "nombre_usuario = ?, rol = ? "
                + "WHERE id_usuario = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, usuario.getNombre());
            consulta.setString(2, usuario.getNombreUsuario());
            consulta.setString(3, usuario.getRol());
            consulta.setInt(4, usuario.getIdUsuario());

            return consulta.executeUpdate() > 0;
        }
    }

    // ACTIVAR O DESACTIVAR USUARIO
    public boolean cambiarEstado(int idUsuario, boolean activo)
            throws SQLException {

        String sql = "UPDATE usuario SET activo = ? "
                + "WHERE id_usuario = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setBoolean(1, activo);
            consulta.setInt(2, idUsuario);

            return consulta.executeUpdate() > 0;
        }
    }

    // CAMBIAR CONTRASEÑA
    public boolean cambiarClave(int idUsuario, String claveHash)
            throws SQLException {

        String sql = "UPDATE usuario SET clave_hash = ? "
                + "WHERE id_usuario = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, claveHash);
            consulta.setInt(2, idUsuario);

            return consulta.executeUpdate() > 0;
        }
    }

    // VERIFICAR SI EXISTE EL NOMBRE DE USUARIO
    public boolean existeUsuario(String nombreUsuario)
            throws SQLException {

        String sql = "SELECT 1 FROM usuario "
                + "WHERE nombre_usuario = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario);

            try (ResultSet resultado = consulta.executeQuery()) {

                return resultado.next();
            }
        }
    }
}
