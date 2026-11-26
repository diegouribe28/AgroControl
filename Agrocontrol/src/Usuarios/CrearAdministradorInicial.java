
package Usuarios;

import com.finca.sistemaporcino.conexion.Conexion;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class CrearAdministradorInicial {

    private static final int ITERACIONES = 600000;
    private static final int LONGITUD_HASH = 256;

    public static void main(String[] args) {

        String nombre = JOptionPane.showInputDialog(
                null, "Nombre del administrador:");

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        String usuario = JOptionPane.showInputDialog(
                null, "Nombre de usuario:");

        if (usuario == null || usuario.trim().isEmpty()) {
            return;
        }

        JPasswordField campoClave = new JPasswordField();

        int opcion = JOptionPane.showConfirmDialog(
                null,
                campoClave,
                "Contraseña (mínimo 12 caracteres)",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        char[] clave = campoClave.getPassword();
        char[] confirmacion = new char[0];

        try {
            if (clave.length < 12) {
                JOptionPane.showMessageDialog(
                        null,
                        "La contraseña debe tener al menos 12 caracteres.");
                return;
            }

            JPasswordField campoConfirmacion = new JPasswordField();

            opcion = JOptionPane.showConfirmDialog(
                    null,
                    campoConfirmacion,
                    "Confirma la contraseña",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            confirmacion = campoConfirmacion.getPassword();

            if (!Arrays.equals(clave, confirmacion)) {
                JOptionPane.showMessageDialog(
                        null, "Las contraseñas no coinciden.");
                return;
            }

            String hash = generarHash(clave);

            try (Connection conexion = Conexion.conectar()) {

                String consulta =
                        "SELECT 1 FROM usuario WHERE nombre_usuario = ?";

                try (PreparedStatement verificar =
                        conexion.prepareStatement(consulta)) {

                    verificar.setString(1, usuario.trim());

                    try (ResultSet resultado = verificar.executeQuery()) {
                        if (resultado.next()) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Ese nombre de usuario ya existe.");
                            return;
                        }
                    }
                }

                String sql =
                        "INSERT INTO usuario "
                        + "(nombre, nombre_usuario, clave_hash, rol, activo) "
                        + "VALUES (?, ?, ?, 'ADMINISTRADOR', TRUE)";

                try (PreparedStatement sentencia =
                        conexion.prepareStatement(sql)) {

                    sentencia.setString(1, nombre.trim());
                    sentencia.setString(2, usuario.trim());
                    sentencia.setString(3, hash);

                    sentencia.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Administrador creado correctamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al crear el administrador:\n"
                    + e.getMessage());
        } finally {
            Arrays.fill(clave, '\0');
            Arrays.fill(confirmacion, '\0');
        }
    }

    private static String generarHash(char[] clave) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec especificacion = new PBEKeySpec(
                clave, salt, ITERACIONES, LONGITUD_HASH);

        byte[] hashBytes;

        try {
            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            hashBytes = fabrica.generateSecret(especificacion).getEncoded();
        } finally {
            especificacion.clearPassword();
        }

        Base64.Encoder codificador = Base64.getEncoder();

        return "pbkdf2_sha256$" + ITERACIONES + "$"
                + codificador.encodeToString(salt) + "$"
                + codificador.encodeToString(hashBytes);
    }
}
