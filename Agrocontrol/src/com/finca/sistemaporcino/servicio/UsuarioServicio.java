
package com.finca.sistemaporcino.servicio;

import com.finca.sistemaporcino.dao.UsuarioDAO;
import com.finca.sistemaporcino.modelo.Usuario;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.sql.SQLException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UsuarioServicio {

    private final UsuarioDAO dao = new UsuarioDAO();

    // VERIFICAR PERMISOS
    private void verificarPermisos(int idAdministrador)
            throws SQLException {

        for (Usuario usuario : dao.listar()) {

            if (usuario.getIdUsuario() == idAdministrador
                    && usuario.isActivo()
                    && "ADMINISTRADOR".equals(usuario.getRol())) {

                return;
            }
        }

        throw new SecurityException(
                "No tiene permisos para administrar usuarios.");
    }

    // GENERAR CONTRASEÑA SEGURA
    private String generarHash(char[] clave) throws Exception {

        if (clave == null || clave.length < 8) {

            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres.");
        }

        int iteraciones = 600000;

        byte[] sal = new byte[16];

        SecureRandom random = new SecureRandom();
        random.nextBytes(sal);

        PBEKeySpec especificacion = new PBEKeySpec(
                clave, sal, iteraciones, 256);

        try {

            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256");

            byte[] hash = fabrica.generateSecret(
                    especificacion).getEncoded();

            return "pbkdf2_sha256$"
                    + iteraciones + "$"
                    + Base64.getEncoder().encodeToString(sal)
                    + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {

            especificacion.clearPassword();
        }
    }

    // VALIDAR DATOS
    private void validarDatos(Usuario usuario) {

        if (usuario.getNombre() == null
                || usuario.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ingrese el nombre completo.");
        }

        if (usuario.getNombreUsuario() == null
                || usuario.getNombreUsuario().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ingrese el nombre de usuario.");
        }

        usuario.setNombre(usuario.getNombre().trim());
        usuario.setNombreUsuario(
                usuario.getNombreUsuario().trim());

        if (usuario.getNombre().length() > 100
                || usuario.getNombreUsuario().length() > 50) {

            throw new IllegalArgumentException(
                    "Los datos superan la longitud permitida.");
        }

        if (!"ADMINISTRADOR".equals(usuario.getRol())
                && !"EMPLEADO".equals(usuario.getRol())) {

            throw new IllegalArgumentException(
                    "Seleccione un rol válido.");
        }
    }

    // REGISTRAR USUARIO
    public boolean registrar(int idAdministrador,
            Usuario usuario, char[] clave) throws Exception {

        verificarPermisos(idAdministrador);

        validarDatos(usuario);

        if (dao.existeUsuario(usuario.getNombreUsuario())) {

            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe.");
        }

        usuario.setClaveHash(generarHash(clave));
        usuario.setActivo(true);

        try {

            return dao.registrar(usuario);

        } finally {

            usuario.setClaveHash(null);
        }
    }

    // LISTAR USUARIOS
    public List<Usuario> listar(int idAdministrador)
            throws SQLException {

        verificarPermisos(idAdministrador);

        return dao.listar();
    }

    // ACTUALIZAR USUARIO
    public boolean actualizar(int idAdministrador,
            Usuario usuario) throws SQLException {

        verificarPermisos(idAdministrador);

        validarDatos(usuario);

        if (idAdministrador == usuario.getIdUsuario()
                && !"ADMINISTRADOR".equals(usuario.getRol())) {

            throw new IllegalArgumentException(
                    "No puede cambiar su propio rol de administrador.");
        }

        for (Usuario existente : dao.listar()) {

            if (existente.getNombreUsuario().equals(
                    usuario.getNombreUsuario())
                    && existente.getIdUsuario()
                    != usuario.getIdUsuario()) {

                throw new IllegalArgumentException(
                        "El nombre de usuario ya está registrado.");
            }
        }

        return dao.actualizar(usuario);
    }

    // ACTIVAR O DESACTIVAR USUARIO
    public boolean cambiarEstado(int idAdministrador,
            int idUsuario, boolean activo) throws SQLException {

        verificarPermisos(idAdministrador);

        if (idAdministrador == idUsuario && !activo) {

            throw new IllegalArgumentException(
                    "No puede desactivar su propia cuenta.");
        }

        return dao.cambiarEstado(idUsuario, activo);
    }

    // CAMBIAR CONTRASEÑA
    public boolean cambiarClave(int idAdministrador,
            int idUsuario, char[] nuevaClave) throws Exception {

        verificarPermisos(idAdministrador);

        String hash = generarHash(nuevaClave);

        return dao.cambiarClave(idUsuario, hash);
    }
}
