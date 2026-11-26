
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.modelo.Usuario;
import com.finca.sistemaporcino.servicio.UsuarioServicio;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GestionUsuarios extends JFrame {

    private final int idAdministrador;

    private final UsuarioServicio servicio =
            new UsuarioServicio();

    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JComboBox<String> cbRol;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEstado;
    private JButton btnCambiarClave;
    private JButton btnLimpiar;
    private JButton btnCerrar;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    private int idSeleccionado = -1;
    private boolean estadoSeleccionado;

    public GestionUsuarios(int idAdministrador) {

        this.idAdministrador = idAdministrador;

        setTitle("AgroControl - Gestión de usuarios");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
        cargarUsuarios();
    }

    private void inicializarComponentes() {

        txtNombre = new JTextField();
        txtUsuario = new JTextField();
        txtClave = new JPasswordField();

        cbRol = new JComboBox<>(new String[]{
            "EMPLEADO",
            "ADMINISTRADOR"
        });

        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEstado = new JButton("Activar / Desactivar");
        btnCambiarClave = new JButton("Cambiar contraseña");
        btnLimpiar = new JButton("Limpiar");
        btnCerrar = new JButton("Cerrar");

        JPanel panelCampos = new JPanel(
                new GridLayout(4, 2, 10, 10));

        panelCampos.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos del usuario"));

        panelCampos.add(new JLabel("Nombre completo:"));
        panelCampos.add(txtNombre);

        panelCampos.add(new JLabel("Nombre de usuario:"));
        panelCampos.add(txtUsuario);

        panelCampos.add(new JLabel("Contraseña:"));
        panelCampos.add(txtClave);

        panelCampos.add(new JLabel("Rol:"));
        panelCampos.add(cbRol);

        JPanel panelBotones = new JPanel(
                new FlowLayout());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEstado);
        panelBotones.add(btnCambiarClave);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        modeloTabla = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Nombre",
                    "Usuario",
                    "Rol",
                    "Estado"
                }, 0) {

            @Override
            public boolean isCellEditable(
                    int fila, int columna) {

                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);

        tablaUsuarios.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll =
                new JScrollPane(tablaUsuarios);

        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Usuarios registrados"));

        JPanel panelSuperior =
                new JPanel(new BorderLayout());

        panelSuperior.add(
                panelCampos, BorderLayout.CENTER);

        panelSuperior.add(
                panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnRegistrar.addActionListener(
                e -> registrarUsuario());

        btnActualizar.addActionListener(
                e -> actualizarUsuario());

        btnEstado.addActionListener(
                e -> cambiarEstado());

        btnCambiarClave.addActionListener(
                e -> cambiarClave());

        btnLimpiar.addActionListener(
                e -> limpiarCampos());

        btnCerrar.addActionListener(
                e -> dispose());

        tablaUsuarios.getSelectionModel()
                .addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                seleccionarUsuario();
            }
        });
    }

    // CARGAR USUARIOS EN LA TABLA
    private void cargarUsuarios() {

        try {

            modeloTabla.setRowCount(0);

            for (Usuario usuario :
                    servicio.listar(idAdministrador)) {

                modeloTabla.addRow(new Object[]{
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getNombreUsuario(),
                    usuario.getRol(),
                    usuario.isActivo()
                            ? "ACTIVO" : "INACTIVO"
                });
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // REGISTRAR USUARIO
    private void registrarUsuario() {

        char[] clave = txtClave.getPassword();

        try {

            Usuario usuario = new Usuario();

            usuario.setNombre(txtNombre.getText());

            usuario.setNombreUsuario(
                    txtUsuario.getText());

            usuario.setRol(
                    cbRol.getSelectedItem().toString());

            if (servicio.registrar(
                    idAdministrador, usuario, clave)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario registrado correctamente.");

                limpiarCampos();
                cargarUsuarios();
            }

        } catch (Exception e) {

            mostrarError(e);

        } finally {

            Arrays.fill(clave, '\0');
            txtClave.setText("");
        }
    }

    // SELECCIONAR USUARIO
    private void seleccionarUsuario() {

        int fila = tablaUsuarios.getSelectedRow();

        if (fila == -1) {

            idSeleccionado = -1;
            btnEstado.setText("Activar / Desactivar");
            return;
        }

        fila = tablaUsuarios.convertRowIndexToModel(fila);

        idSeleccionado = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString());

        txtNombre.setText(
                modeloTabla.getValueAt(fila, 1).toString());

        txtUsuario.setText(
                modeloTabla.getValueAt(fila, 2).toString());

        cbRol.setSelectedItem(
                modeloTabla.getValueAt(fila, 3).toString());

        estadoSeleccionado = modeloTabla
                .getValueAt(fila, 4)
                .toString().equals("ACTIVO");

        btnEstado.setText(
                estadoSeleccionado
                        ? "Desactivar" : "Activar");

        txtClave.setText("");
    }

    // ACTUALIZAR USUARIO
    private void actualizarUsuario() {

        if (!validarSeleccion()) {
            return;
        }

        try {

            Usuario usuario = new Usuario();

            usuario.setIdUsuario(idSeleccionado);
            usuario.setNombre(txtNombre.getText());

            usuario.setNombreUsuario(
                    txtUsuario.getText());

            usuario.setRol(
                    cbRol.getSelectedItem().toString());

            if (servicio.actualizar(
                    idAdministrador, usuario)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario actualizado correctamente.");

                limpiarCampos();
                cargarUsuarios();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // ACTIVAR O DESACTIVAR
    private void cambiarEstado() {

        if (!validarSeleccion()) {
            return;
        }

        String accion = estadoSeleccionado
                ? "desactivar" : "activar";

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea " + accion + " este usuario?",
                "Confirmar operación",
                JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            if (servicio.cambiarEstado(
                    idAdministrador,
                    idSeleccionado,
                    !estadoSeleccionado)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Estado actualizado correctamente.");

                limpiarCampos();
                cargarUsuarios();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CAMBIAR CONTRASEÑA
    private void cambiarClave() {

        if (!validarSeleccion()) {
            return;
        }

        JPasswordField campo = new JPasswordField();

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                campo,
                "Ingrese la nueva contraseña",
                JOptionPane.OK_CANCEL_OPTION);

        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        char[] clave = campo.getPassword();

        try {

            if (servicio.cambiarClave(
                    idAdministrador,
                    idSeleccionado,
                    clave)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Contraseña actualizada correctamente.");

                limpiarCampos();
            }

        } catch (Exception e) {

            mostrarError(e);

        } finally {

            Arrays.fill(clave, '\0');
            campo.setText("");
        }
    }

    // VALIDAR SELECCIÓN
    private boolean validarSeleccion() {

        if (idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un usuario de la tabla.");

            return false;
        }

        return true;
    }

    // LIMPIAR CAMPOS
    private void limpiarCampos() {

        tablaUsuarios.clearSelection();

        txtNombre.setText("");
        txtUsuario.setText("");
        txtClave.setText("");

        cbRol.setSelectedIndex(0);

        idSeleccionado = -1;

        btnEstado.setText("Activar / Desactivar");

        txtNombre.requestFocus();
    }

    // MOSTRAR ERRORES
    private void mostrarError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
