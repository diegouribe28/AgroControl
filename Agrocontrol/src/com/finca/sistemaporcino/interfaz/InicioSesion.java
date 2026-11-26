
package com.finca.sistemaporcino.interfaz;
import com.finca.sistemaporcino.conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import Usuarios.VerificadorClave;

public class InicioSesion extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;

    public InicioSesion() {

        setTitle("AgroControl - Inicio de sesión");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel lblTitulo = new JLabel(
                "AgroControl - Sistema de Gestión Porcina",
                JLabel.CENTER
        );

        JLabel lblUsuario = new JLabel("Nombre de usuario:");
        JLabel lblClave = new JLabel("Contraseña:");

        txtUsuario = new JTextField();
        txtClave = new JPasswordField();

        btnIngresar = new JButton("Iniciar sesión");

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));

        panelCampos.add(lblUsuario);
        panelCampos.add(txtUsuario);
        panelCampos.add(lblClave);
        panelCampos.add(txtClave);

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnIngresar);

        add(lblTitulo, BorderLayout.NORTH);
        add(panelCampos, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> iniciarSesion());

        getRootPane().setDefaultButton(btnIngresar);
    }

    private void iniciarSesion() {

        String usuario = txtUsuario.getText().trim();
        char[] clave = txtClave.getPassword();

        if (usuario.isEmpty() || clave.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Escribe el usuario y la contraseña.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            Arrays.fill(clave, '\0');
            return;
        }

        String sql = "SELECT id_usuario, nombre, nombre_usuario, clave_hash, rol "
        + "FROM usuario "
        + "WHERE nombre_usuario = ? AND activo = TRUE";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, usuario);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {

                    String hashGuardado = resultado.getString("clave_hash");

                    boolean claveCorrecta =
                            VerificadorClave.verificar(clave, hashGuardado);

                    if (claveCorrecta) {

                        String nombre = resultado.getString("nombre");
                        String rol = resultado.getString("rol");
                        int idUsuario = resultado.getInt("id_usuario");

                        JOptionPane.showMessageDialog(
                                this,
                                "Bienvenido a AgroControl, " + nombre
                        );

                        MenuPrincipal menu = new MenuPrincipal(idUsuario,nombre, rol);
                        menu.setVisible(true);
                        dispose();

                    } else {
                        mostrarErrorAcceso();
                    }

                } else {
                    mostrarErrorAcceso();
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible consultar la base de datos.\n"
                    + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            Arrays.fill(clave, '\0');
            txtClave.setText("");
        }
    }

    private void mostrarErrorAcceso() {

        JOptionPane.showMessageDialog(
                this,
                "Usuario o contraseña incorrectos, "
                + "o la cuenta está inactiva.",
                "Acceso denegado",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            InicioSesion ventana = new InicioSesion();
            ventana.setVisible(true);
        });
    }
}
