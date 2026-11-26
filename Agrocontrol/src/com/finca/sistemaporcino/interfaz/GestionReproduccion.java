
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.Reproduccion;
import com.finca.sistemaporcino.servicio.ReproduccionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GestionReproduccion extends JFrame {

    private final int idUsuario;
    private final String rol;

    private final ReproduccionServicio servicio =
            new ReproduccionServicio();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final Map<Integer, String> codigosCerdas =
            new HashMap<>();

    private JComboBox<CerdaItem> cbCerda;
    private JComboBox<CerdaItem> cbFiltroCerda;
    private JComboBox<String> cbResultado;

    private JTextField txtFechaServicio;
    private JTextField txtFechaProbable;
    private JTextField txtFechaReal;

    private JTextArea txtObservaciones;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;

    private JTable tabla;
    private DefaultTableModel modelo;

    private JLabel lblTotal;

    private int idReproduccionSeleccionado = -1;

    // CONSTRUCTOR

    public GestionReproduccion(int idUsuario, String rol) {

        this.idUsuario = idUsuario;
        this.rol = rol;

        setTitle("AgroControl - Reproducción");
        setSize(1350, 800);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarCerdas();
        cargarRegistros();
    }

    // INICIALIZAR INTERFAZ

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel(
                "GESTIÓN REPRODUCTIVA PORCINA",
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

        JPanel panelEncabezado =
                new JPanel(new GridLayout(2, 1, 5, 5));

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(lblUsuario);

        // CAMPOS DEL FORMULARIO

        cbCerda = new JComboBox<>();
        cbFiltroCerda = new JComboBox<>();

        cbResultado = new JComboBox<>(new String[]{

            "PENDIENTE",
            "PARTO",
            "FALSA_GESTACION",
            "NO_GESTANTE",
            "PERDIDA_GESTACIONAL"
        });

        txtFechaServicio = new JTextField(
                LocalDate.now().toString()
        );

        txtFechaProbable = new JTextField(
                servicio.calcularFechaProbableParto(
                        LocalDate.now()).toString()
        );

        txtFechaReal = new JTextField();

        txtFechaReal.setEnabled(false);

        txtObservaciones = new JTextArea(3, 30);

        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        JButton btnCalcular =
                new JButton("Calcular fecha probable");

        JPanel panelDatos =
                new JPanel(new GridLayout(6, 2, 10, 7));

        panelDatos.add(new JLabel("Cerda:"));
        panelDatos.add(cbCerda);

        panelDatos.add(new JLabel("Resultado:"));
        panelDatos.add(cbResultado);

        panelDatos.add(
                new JLabel("Fecha servicio (AAAA-MM-DD):")
        );

        panelDatos.add(txtFechaServicio);

        panelDatos.add(
                new JLabel("Fecha probable de parto:")
        );

        panelDatos.add(txtFechaProbable);

        panelDatos.add(
                new JLabel("Fecha real de parto:")
        );

        panelDatos.add(txtFechaReal);

        panelDatos.add(new JLabel("Cálculo de gestación:"));
        panelDatos.add(btnCalcular);

        JPanel panelObservaciones =
                new JPanel(new BorderLayout(5, 5));

        panelObservaciones.add(
                new JLabel("Observaciones:"),
                BorderLayout.NORTH
        );

        panelObservaciones.add(
                new JScrollPane(txtObservaciones),
                BorderLayout.CENTER
        );

        JPanel panelFormulario =
                new JPanel(new BorderLayout(10, 10));

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos reproductivos"
                )
        );

        panelFormulario.add(
                panelDatos, BorderLayout.NORTH
        );

        panelFormulario.add(
                panelObservaciones, BorderLayout.CENTER
        );

        // BOTONES CRUD

        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrar = new JButton("Cerrar");

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        JPanel panelBotones =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        // BOTONES DE CONSULTA

        JButton btnHistorial =
                new JButton("Consultar historial");

        JButton btnTodos =
                new JButton("Mostrar todos");

        JButton btnProximos =
                new JButton("Próximos partos");

        JButton btnVencidos =
                new JButton("Fechas vencidas");

        JButton btnContar =
                new JButton("Contar partos");

        JButton btnRecargar =
                new JButton("Recargar cerdas");

        JPanel panelFiltro =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelFiltro.setBorder(
                BorderFactory.createTitledBorder(
                        "Consultas reproductivas"
                )
        );

        panelFiltro.add(new JLabel("Cerda:"));
        panelFiltro.add(cbFiltroCerda);
        panelFiltro.add(btnHistorial);
        panelFiltro.add(btnTodos);
        panelFiltro.add(btnProximos);
        panelFiltro.add(btnVencidos);
        panelFiltro.add(btnContar);
        panelFiltro.add(btnRecargar);

        // TABLA

        String[] columnas = {

            "ID Registro",
            "ID Cerda",
            "Código cerda",
            "Fecha servicio",
            "Parto probable",
            "Parto real",
            "Resultado",
            "Observaciones",
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
                        || columna == 8) {

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
                .getColumn(6).setPreferredWidth(180);

        tabla.getColumnModel()
                .getColumn(7).setPreferredWidth(250);

        JScrollPane scrollTabla =
                new JScrollPane(tabla);

        scrollTabla.setBorder(
                BorderFactory.createTitledBorder(
                        "Registros reproductivos"
                )
        );

        // CONTADOR

        lblTotal = new JLabel(
                "Total de registros: 0"
        );

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JPanel panelInferior =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelInferior.add(lblTotal);

        // ORGANIZAR PANELES

        JPanel panelSuperior =
                new JPanel(new BorderLayout(5, 5));

        panelSuperior.add(
                panelEncabezado, BorderLayout.NORTH
        );

        panelSuperior.add(
                panelFormulario, BorderLayout.CENTER
        );

        panelSuperior.add(
                panelBotones, BorderLayout.SOUTH
        );

        JPanel panelCentral =
                new JPanel(new BorderLayout(5, 5));

        panelCentral.add(
                panelFiltro, BorderLayout.NORTH
        );

        panelCentral.add(
                scrollTabla, BorderLayout.CENTER
        );

        JPanel panelPrincipal =
                new JPanel(new BorderLayout(10, 10));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        panelPrincipal.add(
                panelSuperior, BorderLayout.NORTH
        );

        panelPrincipal.add(
                panelCentral, BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelInferior, BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);

        // EVENTOS

        btnRegistrar.addActionListener(
                e -> registrarReproduccion()
        );

        btnActualizar.addActionListener(
                e -> actualizarReproduccion()
        );

        btnEliminar.addActionListener(
                e -> eliminarReproduccion()
        );

        btnLimpiar.addActionListener(
                e -> limpiarCampos()
        );

        btnCerrar.addActionListener(
                e -> dispose()
        );

        btnCalcular.addActionListener(
                e -> calcularFechaProbable()
        );

        btnHistorial.addActionListener(
                e -> consultarHistorial()
        );

        btnTodos.addActionListener(
                e -> cargarRegistros()
        );

        btnProximos.addActionListener(
                e -> consultarProximosPartos()
        );

        btnVencidos.addActionListener(
                e -> consultarPartosVencidos()
        );

        btnContar.addActionListener(
                e -> contarPartos()
        );

        btnRecargar.addActionListener(e -> {

            limpiarCampos();
            cargarCerdas();
            cargarRegistros();
        });

        cbResultado.addActionListener(
                e -> controlarFechaReal()
        );

        txtFechaServicio.addActionListener(
                e -> calcularFechaProbable()
        );

        cbFiltroCerda.addActionListener(e -> {

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

    // CLASE INTERNA PARA MOSTRAR CERDAS

    private static class CerdaItem {

        private final int idCerda;
        private final String codigo;
        private final boolean activo;

        public CerdaItem(
                int idCerda,
                String codigo,
                boolean activo) {

            this.idCerda = idCerda;
            this.codigo = codigo;
            this.activo = activo;
        }

        public int getIdCerda() {

            return idCerda;
        }

        public boolean isActivo() {

            return activo;
        }

        @Override
        public String toString() {

            return codigo;
        }
    }

    // CARGAR CERDAS

    private void cargarCerdas() {

        try {

            List<Animal> animales = animalDAO.listar();

            cbCerda.removeAllItems();
            cbFiltroCerda.removeAllItems();

            codigosCerdas.clear();

            cbCerda.addItem(
                    new CerdaItem(
                            0, "Seleccione una cerda", false)
            );

            cbFiltroCerda.addItem(
                    new CerdaItem(
                            0, "Seleccione una cerda", false)
            );

            for (Animal animal : animales) {

                if (!"HEMBRA".equals(animal.getSexo())) {

                    continue;
                }

                boolean activo =
                        "ACTIVO".equals(animal.getEstado());

                codigosCerdas.put(
                        animal.getIdAnimal(),
                        animal.getCodigo()
                );

                CerdaItem item = new CerdaItem(

                        animal.getIdAnimal(),
                        animal.getCodigo(),
                        activo
                );

                cbFiltroCerda.addItem(item);

                if (activo) {

                    cbCerda.addItem(item);
                }
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONTROLAR FECHA REAL DE PARTO

    private void controlarFechaReal() {

        boolean esParto = "PARTO".equals(
                cbResultado.getSelectedItem()
        );

        txtFechaReal.setEnabled(esParto);

        if (!esParto) {

            txtFechaReal.setText("");
        }
    }

    // LEER Y VALIDAR FORMATO DE FECHA

    private LocalDate leerFecha(
            JTextField campo,
            String nombre,
            boolean obligatoria) {

        String texto = campo.getText().trim();

        if (texto.isEmpty()) {

            if (obligatoria) {

                throw new IllegalArgumentException(
                        nombre + " es obligatoria."
                );
            }

            return null;
        }

        try {

            return LocalDate.parse(texto);

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    nombre + ": usa el formato AAAA-MM-DD."
            );
        }
    }

    // CALCULAR FECHA PROBABLE DE PARTO

    private void calcularFechaProbable() {

        try {

            LocalDate fechaServicio = leerFecha(
                    txtFechaServicio,
                    "La fecha de servicio",
                    true
            );

            LocalDate fechaProbable =
                    servicio.calcularFechaProbableParto(
                            fechaServicio
                    );

            txtFechaProbable.setText(
                    fechaProbable.toString()
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // OBTENER DATOS DEL FORMULARIO

    private Reproduccion obtenerDatos() {

        CerdaItem cerda =
                (CerdaItem) cbCerda.getSelectedItem();

        if (cerda == null || cerda.getIdCerda() == 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar una cerda."
            );
        }

        LocalDate fechaServicio = leerFecha(

                txtFechaServicio,
                "La fecha de servicio",
                true
        );

        LocalDate fechaProbable = leerFecha(

                txtFechaProbable,
                "La fecha probable de parto",
                false
        );

        LocalDate fechaReal = leerFecha(

                txtFechaReal,
                "La fecha real de parto",
                false
        );

        String resultado =
                cbResultado.getSelectedItem().toString();

        String observaciones =
                txtObservaciones.getText().trim();

        return new Reproduccion(

                cerda.getIdCerda(),
                idUsuario,
                fechaServicio,
                fechaProbable,
                fechaReal,
                resultado,
                observaciones
        );
    }

    // REGISTRAR REPRODUCCIÓN

    private void registrarReproduccion() {

        try {

            Reproduccion registro = obtenerDatos();

            if (servicio.registrar(idUsuario, registro)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registro reproductivo guardado correctamente."
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

            mostrarRegistros(

                    servicio.listar(idUsuario),
                    "Total de registros"
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // MOSTRAR REGISTROS EN LA TABLA

    private void mostrarRegistros(
            List<Reproduccion> registros,
            String titulo) {

        limpiarCampos();

        modelo.setRowCount(0);

        for (Reproduccion registro : registros) {

            String codigo = codigosCerdas.getOrDefault(

                    registro.getIdCerda(),
                    "ID: " + registro.getIdCerda()
            );

            Object[] fila = {

                registro.getIdReproduccion(),

                registro.getIdCerda(),

                codigo,

                convertirFecha(
                        registro.getFechaServicio()),

                convertirFecha(
                        registro.getFechaProbableParto()),

                convertirFecha(
                        registro.getFechaPartoReal()),

                registro.getResultado(),

                registro.getObservaciones() == null
                        ? ""
                        : registro.getObservaciones(),

                registro.getIdUsuario()
            };

            modelo.addRow(fila);
        }

        lblTotal.setText(
                titulo + ": " + registros.size()
        );
    }

    // CONVERTIR FECHA PARA LA TABLA

    private String convertirFecha(LocalDate fecha) {

        return fecha == null ? "" : fecha.toString();
    }

    // CONSULTAR HISTORIAL

    private void consultarHistorial() {

        try {

            CerdaItem cerda = obtenerCerdaFiltro();

            mostrarRegistros(

                    servicio.listarPorCerda(
                            idUsuario, cerda.getIdCerda()),

                    "Historial reproductivo de " + cerda
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // OBTENER CERDA DEL FILTRO

    private CerdaItem obtenerCerdaFiltro() {

        CerdaItem cerda =
                (CerdaItem) cbFiltroCerda.getSelectedItem();

        if (cerda == null || cerda.getIdCerda() == 0) {

            throw new IllegalArgumentException(
                    "Selecciona una cerda en el filtro."
            );
        }

        return cerda;
    }

    // CONSULTAR PRÓXIMOS PARTOS

    private void consultarProximosPartos() {

        try {

            mostrarRegistros(

                    servicio.listarProximosPartos(idUsuario),

                    "Próximos partos estimados"
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR FECHAS VENCIDAS

    private void consultarPartosVencidos() {

        try {

            mostrarRegistros(

                    servicio.listarPartosVencidos(idUsuario),

                    "Fechas probables de parto vencidas"
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONTAR PARTOS POR CERDA

    private void contarPartos() {

        try {

            CerdaItem cerda = obtenerCerdaFiltro();

            int total = servicio.contarPartosPorCerda(

                    idUsuario,
                    cerda.getIdCerda()
            );

            lblTotal.setText(

                    "Total histórico de partos de "
                    + cerda + ": " + total
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // SELECCIONAR REGISTRO

    private void seleccionarRegistro() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            limpiarCampos();
            return;
        }

        fila = tabla.convertRowIndexToModel(fila);

        idReproduccionSeleccionado =
                Integer.parseInt(
                        modelo.getValueAt(fila, 0).toString()
                );

        int idCerda = Integer.parseInt(
                modelo.getValueAt(fila, 1).toString()
        );

        seleccionarCerdaCombo(idCerda);

        txtFechaServicio.setText(
                modelo.getValueAt(fila, 3).toString()
        );

        txtFechaProbable.setText(
                modelo.getValueAt(fila, 4).toString()
        );

        cbResultado.setSelectedItem(
                modelo.getValueAt(fila, 6).toString()
        );

        controlarFechaReal();

        txtFechaReal.setText(
                modelo.getValueAt(fila, 5).toString()
        );

        txtObservaciones.setText(
                modelo.getValueAt(fila, 7).toString()
        );

        cbCerda.setEnabled(false);

        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);

        btnEliminar.setEnabled(
                "ADMINISTRADOR".equals(rol)
        );
    }

    // SELECCIONAR CERDA EN COMBOBOX

    private void seleccionarCerdaCombo(int idCerda) {

        for (int i = 0; i < cbCerda.getItemCount(); i++) {

            CerdaItem item = cbCerda.getItemAt(i);

            if (item.getIdCerda() == idCerda) {

                cbCerda.setSelectedIndex(i);
                return;
            }
        }

        // PERMITIR EDITAR REGISTROS HISTÓRICOS

        CerdaItem item = new CerdaItem(

                idCerda,

                codigosCerdas.getOrDefault(
                        idCerda, "ID: " + idCerda),

                false
        );

        cbCerda.addItem(item);
        cbCerda.setSelectedItem(item);
    }

    // ACTUALIZAR REPRODUCCIÓN

    private void actualizarReproduccion() {

        try {

            if (idReproduccionSeleccionado == -1) {

                throw new IllegalArgumentException(
                        "Selecciona un registro de la tabla."
                );
            }

            Reproduccion registro = obtenerDatos();

            registro.setIdReproduccion(
                    idReproduccionSeleccionado
            );

            int confirmacion =
                    JOptionPane.showConfirmDialog(

                            this,
                            "¿Deseas actualizar el registro?",
                            "Confirmar actualización",
                            JOptionPane.YES_NO_OPTION
                    );

            if (confirmacion != JOptionPane.YES_OPTION) {

                return;
            }

            if (servicio.actualizar(idUsuario, registro)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registro reproductivo actualizado."
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

    // ELIMINAR REPRODUCCIÓN

    private void eliminarReproduccion() {

        try {

            if (!"ADMINISTRADOR".equals(rol)) {

                throw new SecurityException(
                        "No tienes permisos para eliminar."
                );
            }

            if (idReproduccionSeleccionado == -1) {

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

            if (servicio.eliminar(

                    idUsuario,
                    idReproduccionSeleccionado)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registro reproductivo eliminado."
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

    // LIMPIAR FORMULARIO

    private void limpiarCampos() {

        idReproduccionSeleccionado = -1;

        if (tabla.getSelectedRow() != -1) {

            tabla.clearSelection();
        }

        cbCerda.setEnabled(true);

        // RETIRAR CERDAS INACTIVAS DEL FORMULARIO

        for (int i = cbCerda.getItemCount() - 1;
                i >= 0; i--) {

            CerdaItem item = cbCerda.getItemAt(i);

            if (item.getIdCerda() != 0
                    && !item.isActivo()) {

                cbCerda.removeItemAt(i);
            }
        }

        if (cbCerda.getItemCount() > 0) {

            cbCerda.setSelectedIndex(0);
        }

        cbResultado.setSelectedItem("PENDIENTE");

        txtFechaServicio.setText(
                LocalDate.now().toString()
        );

        txtFechaProbable.setText(

                servicio.calcularFechaProbableParto(
                        LocalDate.now()).toString()
        );

        txtFechaReal.setText("");
        txtFechaReal.setEnabled(false);

        txtObservaciones.setText("");

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
