
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.RegistroSanitario;
import com.finca.sistemaporcino.servicio.RegistroSanitarioServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GestionSanitario extends JFrame {

    private final int idUsuario;
    private final String rol;

    private final RegistroSanitarioServicio servicio =
            new RegistroSanitarioServicio();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final Map<Integer, String> codigosAnimales =
            new HashMap<>();

    private JComboBox<AnimalItem> cbAnimal;
    private JComboBox<AnimalItem> cbFiltroAnimal;
    private JComboBox<String> cbTipo;

    private JTextField txtFecha;
    private JTextArea txtDescripcion;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;

    private JLabel lblTotal;

    private JTable tabla;
    private DefaultTableModel modelo;

    private int idRegistroSeleccionado = -1;

    // CONSTRUCTOR

    public GestionSanitario(int idUsuario, String rol) {

        this.idUsuario = idUsuario;
        this.rol = rol;

        setTitle("AgroControl - Registro sanitario");
        setSize(1250, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarAnimales();
        cargarRegistros();
    }

    // CREAR INTERFAZ GRÁFICA

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel(
                "GESTIÓN SANITARIA PORCINA",
                JLabel.CENTER
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        JLabel lblUsuario = new JLabel(
                "Usuario: " + idUsuario
                + " | Rol: " + rol,
                JLabel.CENTER
        );

        JPanel panelEncabezado = new JPanel(
                new GridLayout(2, 1, 5, 5)
        );

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(lblUsuario);

        // CAMPOS DEL FORMULARIO

        cbAnimal = new JComboBox<>();
        cbFiltroAnimal = new JComboBox<>();

        cbTipo = new JComboBox<>(new String[]{

            "Seleccione un tipo",
            "VACUNACIÓN",
            "DESPARASITACIÓN",
            "ENFERMEDAD",
            "TRATAMIENTO",
            "MEDICACIÓN",
            "REVISIÓN VETERINARIA",
            "LESIÓN",
            "OTRO"
        });

        txtFecha = new JTextField(
                LocalDate.now().toString()
        );

        txtDescripcion = new JTextArea(4, 30);

        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        JPanel panelDatos = new JPanel(
                new GridLayout(3, 2, 10, 8)
        );

        panelDatos.add(new JLabel("Animal:"));
        panelDatos.add(cbAnimal);

        panelDatos.add(new JLabel("Tipo de registro:"));
        panelDatos.add(cbTipo);

        panelDatos.add(
                new JLabel("Fecha (AAAA-MM-DD):")
        );

        panelDatos.add(txtFecha);

        // DESCRIPCIÓN

        JPanel panelDescripcion = new JPanel(
                new BorderLayout(5, 5)
        );

        panelDescripcion.add(
                new JLabel("Descripción:"),
                BorderLayout.NORTH
        );

        panelDescripcion.add(
                new JScrollPane(txtDescripcion),
                BorderLayout.CENTER
        );

        JPanel panelFormulario = new JPanel(
                new BorderLayout(10, 10)
        );

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos del registro sanitario"
                )
        );

        panelFormulario.add(
                panelDatos,
                BorderLayout.NORTH
        );

        panelFormulario.add(
                panelDescripcion,
                BorderLayout.CENTER
        );

        // BOTONES CRUD

        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrar = new JButton("Cerrar");

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        // FILTROS

        JButton btnHistorial =
                new JButton("Consultar historial");

        JButton btnMostrarTodos =
                new JButton("Mostrar todos");

        JButton btnContar =
                new JButton("Contar registros");

        JButton btnRecargar =
                new JButton("Recargar animales");

        JPanel panelFiltro = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelFiltro.setBorder(
                BorderFactory.createTitledBorder(
                        "Consultar historial sanitario"
                )
        );

        panelFiltro.add(new JLabel("Animal:"));
        panelFiltro.add(cbFiltroAnimal);
        panelFiltro.add(btnHistorial);
        panelFiltro.add(btnMostrarTodos);
        panelFiltro.add(btnContar);
        panelFiltro.add(btnRecargar);

        // TABLA DE REGISTROS

        String[] columnas = {

            "ID Registro",
            "ID Animal",
            "Código animal",
            "Fecha",
            "Tipo",
            "Descripción",
            "ID Usuario"
        };

        modelo = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(
                    int fila, int columna) {

                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {

                if (columna == 0
                        || columna == 1
                        || columna == 6) {

                    return Integer.class;
                }

                return String.class;
            }
        };

        tabla = new JTable(modelo);

        tabla.setRowHeight(25);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setAutoCreateRowSorter(true);

        tabla.getColumnModel()
                .getColumn(5).setPreferredWidth(350);

        JScrollPane scrollTabla =
                new JScrollPane(tabla);

        scrollTabla.setBorder(
                BorderFactory.createTitledBorder(
                        "Registros sanitarios"
                )
        );

        // CONTADOR DE REGISTROS

        lblTotal = new JLabel(
                "Total de registros: 0"
        );

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelInferior.add(lblTotal);

        // ORGANIZAR PANELES

        JPanel panelSuperior = new JPanel(
                new BorderLayout(5, 5)
        );

        panelSuperior.add(
                panelEncabezado,
                BorderLayout.NORTH
        );

        panelSuperior.add(
                panelFormulario,
                BorderLayout.CENTER
        );

        panelSuperior.add(
                panelBotones,
                BorderLayout.SOUTH
        );

        JPanel panelCentral = new JPanel(
                new BorderLayout(5, 5)
        );

        panelCentral.add(
                panelFiltro,
                BorderLayout.NORTH
        );

        panelCentral.add(
                scrollTabla,
                BorderLayout.CENTER
        );

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(10, 10)
        );

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        panelPrincipal.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                panelCentral,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);

        // EVENTOS DE LOS BOTONES

        btnRegistrar.addActionListener(
                e -> registrarSanitario()
        );

        btnActualizar.addActionListener(
                e -> actualizarSanitario()
        );

        btnEliminar.addActionListener(
                e -> eliminarSanitario()
        );

        btnLimpiar.addActionListener(
                e -> limpiarCampos()
        );

        btnCerrar.addActionListener(
                e -> dispose()
        );

        btnHistorial.addActionListener(
                e -> consultarHistorial()
        );

        btnMostrarTodos.addActionListener(
                e -> cargarRegistros()
        );

        btnContar.addActionListener(
                e -> contarRegistros()
        );

        btnRecargar.addActionListener(e -> {

            limpiarCampos();
            cargarAnimales();
            cargarRegistros();
        });

        cbFiltroAnimal.addActionListener(e -> {

            lblTotal.setText(
                    "Registros en tabla: "
                    + modelo.getRowCount()
            );
        });

        tabla.getSelectionModel()
                .addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                seleccionarRegistro();
            }
        });
    }

    // CLASE INTERNA PARA MOSTRAR ANIMALES

    private static class AnimalItem {

        private final int idAnimal;
        private final String codigo;
        private final boolean activo;

        public AnimalItem(
                int idAnimal,
                String codigo,
                boolean activo) {

            this.idAnimal = idAnimal;
            this.codigo = codigo;
            this.activo = activo;
        }

        public int getIdAnimal() {

            return idAnimal;
        }

        public boolean isActivo() {

            return activo;
        }

        @Override
        public String toString() {

            return codigo;
        }
    }

    // CARGAR ANIMALES

    private void cargarAnimales() {

        try {

            List<Animal> animales = animalDAO.listar();

            cbAnimal.removeAllItems();
            cbFiltroAnimal.removeAllItems();

            codigosAnimales.clear();

            cbAnimal.addItem(
                    new AnimalItem(
                            0, "Seleccione un animal", false)
            );

            cbFiltroAnimal.addItem(
                    new AnimalItem(
                            0, "Seleccione un animal", false)
            );

            for (Animal animal : animales) {

                boolean activo =
                        "ACTIVO".equals(animal.getEstado());

                codigosAnimales.put(
                        animal.getIdAnimal(),
                        animal.getCodigo()
                );

                AnimalItem item = new AnimalItem(

                        animal.getIdAnimal(),
                        animal.getCodigo(),
                        activo
                );

                cbFiltroAnimal.addItem(item);

                if (activo) {

                    cbAnimal.addItem(item);
                }
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // OBTENER DATOS DEL FORMULARIO

    private RegistroSanitario obtenerDatos() {

        AnimalItem animal =
                (AnimalItem) cbAnimal.getSelectedItem();

        if (animal == null
                || animal.getIdAnimal() == 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal."
            );
        }

        if (cbTipo.getSelectedIndex() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un tipo de registro."
            );
        }

        String tipo =
                cbTipo.getSelectedItem().toString();

        LocalDate fecha;

        try {

            fecha = LocalDate.parse(
                    txtFecha.getText().trim()
            );

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "Fecha incorrecta. Usa AAAA-MM-DD."
            );
        }

        String descripcion =
                txtDescripcion.getText().trim();

        if (descripcion.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes ingresar una descripción."
            );
        }

        return new RegistroSanitario(

                animal.getIdAnimal(),
                idUsuario,
                fecha,
                tipo,
                descripcion
        );
    }

    // REGISTRAR EVENTO SANITARIO

    private void registrarSanitario() {

        try {

            RegistroSanitario registro =
                    obtenerDatos();

            boolean registrado =
                    servicio.registrar(idUsuario, registro);

            if (registrado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registro sanitario guardado correctamente."
                );

                cargarRegistros();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar el registro."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CARGAR TODOS LOS REGISTROS

    private void cargarRegistros() {

        try {

            List<RegistroSanitario> registros =
                    servicio.listar(idUsuario);

            limpiarCampos();

            modelo.setRowCount(0);

            for (RegistroSanitario registro : registros) {

                agregarFila(registro);
            }

            lblTotal.setText(
                    "Total de registros: "
                    + registros.size()
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // AGREGAR FILA A LA TABLA

    private void agregarFila(RegistroSanitario registro) {

        String codigo = codigosAnimales.getOrDefault(
                registro.getIdAnimal(),
                "ID: " + registro.getIdAnimal()
        );

        Object[] fila = {

            registro.getIdRegistro(),
            registro.getIdAnimal(),
            codigo,
            registro.getFecha().toString(),
            registro.getTipo(),
            registro.getDescripcion(),
            registro.getIdUsuario()
        };

        modelo.addRow(fila);
    }

    // CONSULTAR HISTORIAL POR ANIMAL

    private void consultarHistorial() {

        try {

            AnimalItem animal =
                    (AnimalItem) cbFiltroAnimal
                            .getSelectedItem();

            if (animal == null
                    || animal.getIdAnimal() == 0) {

                throw new IllegalArgumentException(
                        "Selecciona un animal para consultar."
                );
            }

            List<RegistroSanitario> registros =
                    servicio.listarPorAnimal(

                            idUsuario,
                            animal.getIdAnimal()
                    );

            limpiarCampos();

            modelo.setRowCount(0);

            for (RegistroSanitario registro : registros) {

                agregarFila(registro);
            }

            lblTotal.setText(

                    "Registros sanitarios de "
                    + animal
                    + ": "
                    + registros.size()
            );

            if (registros.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "El animal no tiene registros sanitarios."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // SELECCIONAR REGISTRO DE LA TABLA

    private void seleccionarRegistro() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            limpiarCampos();
            return;
        }

        fila = tabla.convertRowIndexToModel(fila);

        idRegistroSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        int idAnimal = Integer.parseInt(
                modelo.getValueAt(fila, 1).toString()
        );

        seleccionarAnimalCombo(idAnimal);

        txtFecha.setText(
                modelo.getValueAt(fila, 3).toString()
        );

        String tipo =
                modelo.getValueAt(fila, 4).toString();

        seleccionarTipo(tipo);

        txtDescripcion.setText(
                modelo.getValueAt(fila, 5).toString()
        );

        cbAnimal.setEnabled(false);

        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);

        btnEliminar.setEnabled(
                "ADMINISTRADOR".equals(rol)
        );
    }

    // SELECCIONAR ANIMAL EN EL COMBOBOX

    private void seleccionarAnimalCombo(int idAnimal) {

        for (int i = 0; i < cbAnimal.getItemCount(); i++) {

            AnimalItem item = cbAnimal.getItemAt(i);

            if (item.getIdAnimal() == idAnimal) {

                cbAnimal.setSelectedIndex(i);
                return;
            }
        }

        // PERMITIR EDITAR REGISTROS HISTÓRICOS

        AnimalItem item = new AnimalItem(

                idAnimal,

                codigosAnimales.getOrDefault(
                        idAnimal, "ID: " + idAnimal),

                false
        );

        cbAnimal.addItem(item);
        cbAnimal.setSelectedItem(item);
    }

    // SELECCIONAR TIPO DE REGISTRO

    private void seleccionarTipo(String tipo) {

        for (int i = 1; i < cbTipo.getItemCount(); i++) {

            if (tipo.equals(cbTipo.getItemAt(i))) {

                cbTipo.setSelectedIndex(i);
                return;
            }
        }

        // CONSERVAR TIPOS EXISTENTES EN POSTGRESQL

        cbTipo.addItem(tipo);

        cbTipo.setSelectedItem(tipo);
    }

    // ACTUALIZAR REGISTRO SANITARIO

    private void actualizarSanitario() {

        try {

            if (idRegistroSeleccionado == -1) {

                throw new IllegalArgumentException(
                        "Selecciona un registro de la tabla."
                );
            }

            RegistroSanitario registro =
                    obtenerDatos();

            registro.setIdRegistro(
                    idRegistroSeleccionado
            );

            int confirmacion =
                    JOptionPane.showConfirmDialog(

                            this,
                            "¿Deseas guardar los cambios?",
                            "Confirmar actualización",
                            JOptionPane.YES_NO_OPTION
                    );

            if (confirmacion != JOptionPane.YES_OPTION) {

                return;
            }

            boolean actualizado =
                    servicio.actualizar(
                            idUsuario, registro
                    );

            if (actualizado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registro sanitario actualizado."
                );

                cargarRegistros();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar el registro."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // ELIMINAR REGISTRO SANITARIO

    private void eliminarSanitario() {

        try {

            if (!"ADMINISTRADOR".equals(rol)) {

                throw new SecurityException(
                        "No tienes permisos para eliminar."
                );
            }

            if (idRegistroSeleccionado == -1) {

                throw new IllegalArgumentException(
                        "Selecciona un registro de la tabla."
                );
            }

            int confirmacion =
                    JOptionPane.showConfirmDialog(

                            this,
                            "¿Deseas eliminar este registro?\n"
                            + "Esta acción no se puede deshacer.",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (confirmacion != JOptionPane.YES_OPTION) {

                return;
            }

            boolean eliminado =
                    servicio.eliminar(

                            idUsuario,
                            idRegistroSeleccionado
                    );

            if (eliminado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registro sanitario eliminado."
                );

                cargarRegistros();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar el registro."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONTAR REGISTROS SANITARIOS

    private void contarRegistros() {

        try {

            AnimalItem animal =
                    (AnimalItem) cbFiltroAnimal
                            .getSelectedItem();

            if (animal == null
                    || animal.getIdAnimal() == 0) {

                throw new IllegalArgumentException(
                        "Selecciona un animal."
                );
            }

            int total =
                    servicio.contarRegistrosPorAnimal(

                            idUsuario,
                            animal.getIdAnimal()
                    );

            lblTotal.setText(

                    "Total histórico de "
                    + animal
                    + ": "
                    + total
                    + " registros"
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // LIMPIAR FORMULARIO

    private void limpiarCampos() {

        idRegistroSeleccionado = -1;

        if (tabla.getSelectedRow() != -1) {

            tabla.clearSelection();
        }

        cbAnimal.setEnabled(true);

        // RETIRAR ANIMALES INACTIVOS DEL FORMULARIO

        for (int i = cbAnimal.getItemCount() - 1;
                i >= 0; i--) {

            AnimalItem item = cbAnimal.getItemAt(i);

            if (item.getIdAnimal() != 0
                    && !item.isActivo()) {

                cbAnimal.removeItemAt(i);
            }
        }

        if (cbAnimal.getItemCount() > 0) {

            cbAnimal.setSelectedIndex(0);
        }

        cbTipo.setSelectedIndex(0);

        txtFecha.setText(
                LocalDate.now().toString()
        );

        txtDescripcion.setText("");

        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    // MOSTRAR ERRORES

    private void mostrarError(Exception e) {

        JOptionPane.showMessageDialog(

                this,
                e.getMessage(),
                "AgroControl - Error",
                JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }
}
