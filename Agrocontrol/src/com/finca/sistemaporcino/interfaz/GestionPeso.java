
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.RegistroPeso;
import com.finca.sistemaporcino.servicio.RegistroPesoServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GestionPeso extends JFrame {

    private final int idUsuario;
    private final String rol;

    private final RegistroPesoServicio servicio =
            new RegistroPesoServicio();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final Map<Integer, String> codigosAnimales =
            new HashMap<>();

    private JComboBox<AnimalItem> cbAnimal;
    private JComboBox<AnimalItem> cbFiltroAnimal;

    private JTextField txtPeso;
    private JTextField txtFecha;
    private JTextArea txtObservaciones;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;

    private JLabel lblGanancia;

    private JTable tabla;
    private DefaultTableModel modelo;

    private int idRegistroSeleccionado = -1;

    // CONSTRUCTOR

    public GestionPeso(int idUsuario, String rol) {

        this.idUsuario = idUsuario;
        this.rol = rol;

        setTitle("AgroControl - Registro de peso");
        setSize(1200, 700);
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
                "REGISTRO DE PESO PORCINO",
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

        txtPeso = new JTextField(12);

        txtFecha = new JTextField(
                LocalDate.now().toString(), 12
        );

        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        JPanel panelDatos = new JPanel(
                new GridLayout(2, 3, 10, 10)
        );

        panelDatos.add(new JLabel("Animal:"));
        panelDatos.add(new JLabel("Peso (kg):"));
        panelDatos.add(new JLabel("Fecha (AAAA-MM-DD):"));

        panelDatos.add(cbAnimal);
        panelDatos.add(txtPeso);
        panelDatos.add(txtFecha);

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
                        "Datos del pesaje"
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

        // BOTONES

        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrar = new JButton("Cerrar");

        btnEliminar.setEnabled(
                "ADMINISTRADOR".equals(rol)
        );

        btnActualizar.setEnabled(false);

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        // FILTRO DE ANIMALES

        JButton btnHistorial =
                new JButton("Consultar historial");

        JButton btnMostrarTodos =
                new JButton("Mostrar todos");

        JButton btnGanancia =
                new JButton("Calcular ganancia");

        JPanel panelFiltro = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelFiltro.setBorder(
                BorderFactory.createTitledBorder(
                        "Consultar registros"
                )
        );

        panelFiltro.add(new JLabel("Animal:"));
        panelFiltro.add(cbFiltroAnimal);
        panelFiltro.add(btnHistorial);
        panelFiltro.add(btnMostrarTodos);
        panelFiltro.add(btnGanancia);

        // TABLA DE REGISTROS

        String[] columnas = {

            "ID Registro",
            "ID Animal",
            "Código animal",
            "Fecha",
            "Peso (kg)",
            "Observaciones",
            "ID Usuario"
        };

        modelo = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(
                    int fila, int columna) {

                return false;
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
                        "Registros de peso"
                )
        );

        // ETIQUETA DE GANANCIA DE PESO

        lblGanancia = new JLabel(
                "Ganancia de peso: sin calcular"
        );

        lblGanancia.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelInferior.add(lblGanancia);

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
                e -> registrarPeso()
        );

        btnActualizar.addActionListener(
                e -> actualizarPeso()
        );

        btnEliminar.addActionListener(
                e -> eliminarPeso()
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

        btnGanancia.addActionListener(
                e -> calcularGanancia()
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

        public AnimalItem(int idAnimal, String codigo) {

            this.idAnimal = idAnimal;
            this.codigo = codigo;
        }

        public int getIdAnimal() {

            return idAnimal;
        }

        @Override
        public String toString() {

            return codigo;
        }
    }

    // CARGAR ANIMALES DESDE POSTGRESQL

    private void cargarAnimales() {

        try {

            cbAnimal.removeAllItems();
            cbFiltroAnimal.removeAllItems();

            codigosAnimales.clear();

            cbAnimal.addItem(
                    new AnimalItem(
                            0, "Seleccione un animal")
            );

            cbFiltroAnimal.addItem(
                    new AnimalItem(
                            0, "Seleccione un animal")
            );

            List<Animal> animales = animalDAO.listar();

            for (Animal animal : animales) {

                codigosAnimales.put(
                        animal.getIdAnimal(),
                        animal.getCodigo()
                );

                AnimalItem item = new AnimalItem(
                        animal.getIdAnimal(),
                        animal.getCodigo()
                );

                cbFiltroAnimal.addItem(item);

                // SOLO ANIMALES ACTIVOS PARA NUEVOS PESAJES

                if ("ACTIVO".equals(animal.getEstado())) {

                    cbAnimal.addItem(item);
                }
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // OBTENER LOS DATOS DEL FORMULARIO

    private RegistroPeso obtenerDatos() {

        AnimalItem animal =
                (AnimalItem) cbAnimal.getSelectedItem();

        if (animal == null || animal.getIdAnimal() == 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal."
            );
        }

        String pesoTexto = txtPeso.getText()
                .trim().replace(',', '.');

        if (pesoTexto.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes ingresar el peso."
            );
        }

        BigDecimal peso;

        try {

            peso = new BigDecimal(pesoTexto);

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Ingresa un peso numérico válido."
            );
        }

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

        return new RegistroPeso(
                animal.getIdAnimal(),
                idUsuario,
                fecha,
                peso,
                txtObservaciones.getText().trim()
        );
    }

    // REGISTRAR PESO

    private void registrarPeso() {

        try {

            RegistroPeso registro = obtenerDatos();

            boolean registrado =
                    servicio.registrar(idUsuario, registro);

            if (registrado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Peso registrado correctamente."
                );

                cargarRegistros();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el peso."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CARGAR TODOS LOS REGISTROS

    private void cargarRegistros() {

        try {

            List<RegistroPeso> registros =
                    servicio.listar(idUsuario);

            limpiarCampos();

            modelo.setRowCount(0);

            for (RegistroPeso registro : registros) {

                agregarFila(registro);
            }

            lblGanancia.setText(
                    "Ganancia de peso: sin calcular"
            );

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // AGREGAR REGISTRO A LA TABLA

    private void agregarFila(RegistroPeso registro) {

        String codigo = codigosAnimales.getOrDefault(
                registro.getIdAnimal(),
                "ID: " + registro.getIdAnimal()
        );

        Object[] fila = {

            registro.getIdRegistro(),
            registro.getIdAnimal(),
            codigo,
            registro.getFecha(),
            registro.getPeso(),
            registro.getObservaciones(),
            registro.getIdUsuario()
        };

        modelo.addRow(fila);
    }

    // CONSULTAR HISTORIAL DE UN ANIMAL

    private void consultarHistorial() {

        try {

            AnimalItem animal =
                    (AnimalItem) cbFiltroAnimal.getSelectedItem();

            if (animal == null
                    || animal.getIdAnimal() == 0) {

                throw new IllegalArgumentException(
                        "Selecciona un animal para consultar."
                );
            }

            List<RegistroPeso> registros =
                    servicio.listarPorAnimal(
                            idUsuario,
                            animal.getIdAnimal()
                    );

            limpiarCampos();

            modelo.setRowCount(0);

            for (RegistroPeso registro : registros) {

                agregarFila(registro);
            }

            lblGanancia.setText(
                    "Ganancia de peso: sin calcular"
            );

            if (registros.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Este animal no tiene pesajes registrados."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // SELECCIONAR UN REGISTRO DE LA TABLA

    private void seleccionarRegistro() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            idRegistroSeleccionado = -1;
            btnActualizar.setEnabled(false);
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

        txtPeso.setText(
                modelo.getValueAt(fila, 4).toString()
        );

        Object observaciones =
                modelo.getValueAt(fila, 5);

        txtObservaciones.setText(
                observaciones == null
                        ? "" : observaciones.toString()
        );

        // NO CAMBIAR EL ANIMAL ORIGINAL DEL PESAJE

        cbAnimal.setEnabled(false);

        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);
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

        // PERMITIR CONSULTAR PESAJES DE ANIMALES INACTIVOS

        AnimalItem item = new AnimalItem(
                idAnimal,
                codigosAnimales.getOrDefault(
                        idAnimal, "ID: " + idAnimal)
        );

        cbAnimal.addItem(item);
        cbAnimal.setSelectedItem(item);
    }

    // ACTUALIZAR REGISTRO DE PESO

    private void actualizarPeso() {

        try {

            if (idRegistroSeleccionado == -1) {

                throw new IllegalArgumentException(
                        "Selecciona un registro de la tabla."
                );
            }

            RegistroPeso registro = obtenerDatos();

            registro.setIdRegistro(
                    idRegistroSeleccionado
            );

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas guardar los cambios del pesaje?",
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

    // ELIMINAR REGISTRO DE PESO

    private void eliminarPeso() {

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

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas eliminar este registro de peso?\n"
                    + "Esta acción no se puede deshacer.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {

                return;
            }

            boolean eliminado = servicio.eliminar(
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

    // CALCULAR GANANCIA DE PESO

    private void calcularGanancia() {

        try {

            AnimalItem animal =
                    (AnimalItem) cbFiltroAnimal.getSelectedItem();

            if (animal == null
                    || animal.getIdAnimal() == 0) {

                throw new IllegalArgumentException(
                        "Selecciona un animal para calcular."
                );
            }

            BigDecimal ganancia =
                    servicio.calcularGananciaPeso(
                            idUsuario,
                            animal.getIdAnimal()
                    );

            String signo =
                    ganancia.signum() > 0 ? "+" : "";

            lblGanancia.setText(
                    "Variación de peso de "
                    + animal
                    + ": "
                    + signo
                    + ganancia.toPlainString()
                    + " kg"
            );

            if (ganancia.signum() > 0) {

                lblGanancia.setForeground(
                        new Color(0, 128, 0)
                );

            } else if (ganancia.signum() < 0) {

                lblGanancia.setForeground(Color.RED);

            } else {

                lblGanancia.setForeground(
                        UIManager.getColor("Label.foreground")
                );
            }

        } catch (Exception e) {

            lblGanancia.setText(
                    "Ganancia de peso: sin calcular"
            );

            mostrarError(e);
        }
    }

    // LIMPIAR FORMULARIO

    private void limpiarCampos() {

        idRegistroSeleccionado = -1;

        tabla.clearSelection();

        cbAnimal.setEnabled(true);

        if (cbAnimal.getItemCount() > 0) {

            cbAnimal.setSelectedIndex(0);
        }

        txtPeso.setText("");

        txtFecha.setText(
                LocalDate.now().toString()
        );

        txtObservaciones.setText("");

        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);

        txtPeso.requestFocus();
    }

    // MOSTRAR MENSAJES DE ERROR

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
