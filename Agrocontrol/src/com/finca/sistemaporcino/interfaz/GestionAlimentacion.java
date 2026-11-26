
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.RegistroAlimentacion;
import com.finca.sistemaporcino.servicio.RegistroAlimentacionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GestionAlimentacion extends JFrame {

    private final int idUsuario;
    private final String rol;

    private final RegistroAlimentacionServicio servicio =
            new RegistroAlimentacionServicio();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final Map<Integer, String> codigosAnimales =
            new HashMap<>();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);

    private JComboBox<AnimalItem> cbAnimal;
    private JComboBox<AnimalItem> cbFiltroAnimal;

    private JTextField txtTipoAlimento;
    private JTextField txtCantidad;
    private JTextField txtFecha;
    private JTextArea txtObservaciones;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;

    private JLabel lblTotal;

    private JTable tabla;
    private DefaultTableModel modelo;

    private int idRegistroSeleccionado = -1;

    // CONSTRUCTOR

    public GestionAlimentacion(int idUsuario, String rol) {

        this.idUsuario = idUsuario;
        this.rol = rol;

        setTitle("AgroControl - Registro de alimentación");
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
                "REGISTRO DE ALIMENTACIÓN PORCINA",
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

        txtTipoAlimento = new JTextField(20);
        txtCantidad = new JTextField(12);

        txtFecha = new JTextField(
                LocalDateTime.now().format(formatoFecha)
        );

        txtObservaciones = new JTextArea(3, 30);

        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        JPanel panelDatos = new JPanel(
                new GridLayout(4, 2, 10, 8)
        );

        panelDatos.add(new JLabel("Animal:"));
        panelDatos.add(cbAnimal);

        panelDatos.add(new JLabel("Tipo de alimento:"));
        panelDatos.add(txtTipoAlimento);

        panelDatos.add(new JLabel("Cantidad (kg):"));
        panelDatos.add(txtCantidad);

        panelDatos.add(
                new JLabel("Fecha (AAAA-MM-DD HH:MM:SS):")
        );

        panelDatos.add(txtFecha);

        // OBSERVACIONES

        JPanel panelObservaciones = new JPanel(
                new BorderLayout(5, 5)
        );

        panelObservaciones.add(
                new JLabel("Observaciones:"),
                BorderLayout.NORTH
        );

        panelObservaciones.add(
                new JScrollPane(txtObservaciones),
                BorderLayout.CENTER
        );

        JPanel panelFormulario = new JPanel(
                new BorderLayout(10, 10)
        );

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos de alimentación"
                )
        );

        panelFormulario.add(
                panelDatos,
                BorderLayout.NORTH
        );

        panelFormulario.add(
                panelObservaciones,
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

        JButton btnTotal =
                new JButton("Calcular total");

        JButton btnRecargar =
                new JButton("Recargar animales");

        JPanel panelFiltro = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelFiltro.setBorder(
                BorderFactory.createTitledBorder(
                        "Consultar alimentación"
                )
        );

        panelFiltro.add(new JLabel("Animal:"));
        panelFiltro.add(cbFiltroAnimal);
        panelFiltro.add(btnHistorial);
        panelFiltro.add(btnMostrarTodos);
        panelFiltro.add(btnTotal);
        panelFiltro.add(btnRecargar);

        // TABLA DE REGISTROS

        String[] columnas = {

            "ID Registro",
            "ID Animal",
            "Código animal",
            "Fecha y hora",
            "Tipo de alimento",
            "Cantidad (kg)",
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

                switch (columna) {

                    case 0:
                    case 1:
                    case 7:
                        return Integer.class;

                    case 5:
                        return BigDecimal.class;

                    default:
                        return String.class;
                }
            }
        };

        tabla = new JTable(modelo);

        tabla.setRowHeight(25);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setAutoCreateRowSorter(true);

        JScrollPane scrollTabla =
                new JScrollPane(tabla);

        scrollTabla.setBorder(
                BorderFactory.createTitledBorder(
                        "Registros de alimentación"
                )
        );

        // ETIQUETA DE ALIMENTO ACUMULADO

        lblTotal = new JLabel(
                "Alimento acumulado: sin calcular"
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
                e -> registrarAlimentacion()
        );

        btnActualizar.addActionListener(
                e -> actualizarAlimentacion()
        );

        btnEliminar.addActionListener(
                e -> eliminarAlimentacion()
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

        btnTotal.addActionListener(
                e -> calcularTotal()
        );

        btnRecargar.addActionListener(e -> {

            limpiarCampos();
            cargarAnimales();
            cargarRegistros();
        });

        cbFiltroAnimal.addActionListener(
                e -> reiniciarTotal()
        );

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

    private RegistroAlimentacion obtenerDatos() {

        AnimalItem animal =
                (AnimalItem) cbAnimal.getSelectedItem();

        if (animal == null
                || animal.getIdAnimal() == 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal."
            );
        }

        String tipoAlimento =
                txtTipoAlimento.getText().trim();

        if (tipoAlimento.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes ingresar el tipo de alimento."
            );
        }

        String cantidadTexto = txtCantidad.getText()
                .trim().replace(',', '.');

        BigDecimal cantidad;

        try {

            cantidad = new BigDecimal(cantidadTexto);

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Ingresa una cantidad numérica válida."
            );
        }

        LocalDateTime fecha;

        try {

            fecha = LocalDateTime.parse(
                    txtFecha.getText().trim(),
                    formatoFecha
            );

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "Fecha incorrecta. Usa el formato "
                    + "AAAA-MM-DD HH:MM:SS."
            );
        }

        return new RegistroAlimentacion(

                animal.getIdAnimal(),
                idUsuario,
                fecha,
                tipoAlimento,
                cantidad,
                txtObservaciones.getText().trim()
        );
    }

    // REGISTRAR ALIMENTACIÓN

    private void registrarAlimentacion() {

        try {

            RegistroAlimentacion registro =
                    obtenerDatos();

            boolean registrado =
                    servicio.registrar(idUsuario, registro);

            if (registrado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Alimentación registrada correctamente."
                );

                cargarRegistros();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar la alimentación."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CARGAR TODOS LOS REGISTROS

    private void cargarRegistros() {

        try {

            List<RegistroAlimentacion> registros =
                    servicio.listar(idUsuario);

            limpiarCampos();

            modelo.setRowCount(0);

            for (RegistroAlimentacion registro : registros) {

                agregarFila(registro);
            }

            reiniciarTotal();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // AGREGAR FILA A LA TABLA

    private void agregarFila(
            RegistroAlimentacion registro) {

        String codigo = codigosAnimales.getOrDefault(
                registro.getIdAnimal(),
                "ID: " + registro.getIdAnimal()
        );

        Object[] fila = {

            registro.getIdRegistro(),
            registro.getIdAnimal(),
            codigo,
            registro.getFecha().format(formatoFecha),
            registro.getTipoAlimento(),
            registro.getCantidadKg(),
            registro.getObservaciones(),
            registro.getIdUsuario()
        };

        modelo.addRow(fila);
    }

    // CONSULTAR HISTORIAL DE UN ANIMAL

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

            List<RegistroAlimentacion> registros =
                    servicio.listarPorAnimal(
                            idUsuario,
                            animal.getIdAnimal()
                    );

            limpiarCampos();

            modelo.setRowCount(0);

            BigDecimal total = BigDecimal.ZERO;

            for (RegistroAlimentacion registro : registros) {

                agregarFila(registro);

                total = total.add(
                        registro.getCantidadKg()
                );
            }

            mostrarTotal(animal, total);

            if (registros.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "El animal no tiene registros "
                        + "de alimentación."
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

        txtTipoAlimento.setText(
                modelo.getValueAt(fila, 4).toString()
        );

        txtCantidad.setText(
                modelo.getValueAt(fila, 5).toString()
        );

        Object observaciones =
                modelo.getValueAt(fila, 6);

        txtObservaciones.setText(
                observaciones == null
                        ? "" : observaciones.toString()
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

    // ACTUALIZAR ALIMENTACIÓN

    private void actualizarAlimentacion() {

        try {

            if (idRegistroSeleccionado == -1) {

                throw new IllegalArgumentException(
                        "Selecciona un registro de la tabla."
                );
            }

            RegistroAlimentacion registro =
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
                        "Registro actualizado correctamente."
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

    // ELIMINAR ALIMENTACIÓN

    private void eliminarAlimentacion() {

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
                        "Registro eliminado correctamente."
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

    // CALCULAR ALIMENTO ACUMULADO

    private void calcularTotal() {

        try {

            AnimalItem animal =
                    (AnimalItem) cbFiltroAnimal
                            .getSelectedItem();

            if (animal == null
                    || animal.getIdAnimal() == 0) {

                throw new IllegalArgumentException(
                        "Selecciona un animal para calcular."
                );
            }

            BigDecimal total =
                    servicio.calcularConsumoTotal(

                            idUsuario,
                            animal.getIdAnimal()
                    );

            mostrarTotal(animal, total);

        } catch (Exception e) {

            reiniciarTotal();
            mostrarError(e);
        }
    }

    // MOSTRAR TOTAL

    private void mostrarTotal(
            AnimalItem animal, BigDecimal total) {

        lblTotal.setText(

                "Alimento acumulado de "
                + animal
                + ": "
                + total.toPlainString()
                + " kg"
        );

        lblTotal.setForeground(
                new Color(0, 128, 0)
        );
    }

    // REINICIAR ETIQUETA DEL TOTAL

    private void reiniciarTotal() {

        lblTotal.setText(
                "Alimento acumulado: sin calcular"
        );

        lblTotal.setForeground(
                UIManager.getColor("Label.foreground")
        );
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

        txtTipoAlimento.setText("");
        txtCantidad.setText("");

        txtFecha.setText(
                LocalDateTime.now().format(formatoFecha)
        );

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
