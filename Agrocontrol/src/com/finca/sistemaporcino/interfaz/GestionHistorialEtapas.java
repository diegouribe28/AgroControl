
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.HistorialEtapa;
import com.finca.sistemaporcino.servicio.HistorialEtapaServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GestionHistorialEtapas extends JFrame {

    private final int idUsuario;

    private final HistorialEtapaServicio servicio =
            new HistorialEtapaServicio();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final Map<Integer, String> codigosAnimales =
            new HashMap<>();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss");

    private JComboBox<AnimalItem> cbAnimal;
    private JComboBox<String> cbEtapa;

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotal;
    private JLabel lblEtapaActual;
    private JLabel lblCambios;

    // CONSTRUCTOR

    public GestionHistorialEtapas(
            int idUsuario, String rol) {

        this.idUsuario = idUsuario;

        setTitle("AgroControl - Historial de etapas");

        setSize(1350, 750);

        setMinimumSize(new Dimension(1000, 600));

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        inicializarComponentes(rol);

        cargarAnimales();

        cargarHistorial();
    }

    // INICIALIZAR INTERFAZ

    private void inicializarComponentes(String rol) {

        setLayout(new BorderLayout(10, 10));

        // ENCABEZADO

        JLabel lblTitulo = new JLabel(
                "HISTORIAL DE ETAPAS",
                JLabel.CENTER);

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24));

        JLabel lblUsuario = new JLabel(
                "Usuario: " + idUsuario
                + " | Rol: " + rol,
                JLabel.CENTER);

        JPanel panelTitulo =
                new JPanel(new GridLayout(2, 1));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblUsuario);

        // CAMPOS DE CONSULTA

        cbAnimal = new JComboBox<>();

        cbEtapa = new JComboBox<>(
                new String[]{
                    "Seleccione una etapa",
                    "CRECIMIENTO",
                    "ENGORDE"
                });

        txtFechaInicio = new JTextField(
                LocalDate.now()
                        .withDayOfMonth(1)
                        .toString());

        txtFechaFin = new JTextField(
                LocalDate.now().toString());

        JPanel panelFiltros =
                new JPanel(new GridLayout(2, 4, 10, 10));

        panelFiltros.setBorder(
                BorderFactory.createTitledBorder(
                        "Filtros de consulta"));

        panelFiltros.add(new JLabel("Animal:"));
        panelFiltros.add(cbAnimal);

        panelFiltros.add(new JLabel("Etapa nueva:"));
        panelFiltros.add(cbEtapa);

        panelFiltros.add(
                new JLabel("Fecha inicial:"));

        panelFiltros.add(txtFechaInicio);

        panelFiltros.add(
                new JLabel("Fecha final:"));

        panelFiltros.add(txtFechaFin);

        // BOTONES

        JButton btnAnimal =
                new JButton("Consultar por animal");

        JButton btnEtapa =
                new JButton("Consultar por etapa");

        JButton btnFechas =
                new JButton("Consultar por fechas");

        JButton btnTodos =
                new JButton("Mostrar todos");

        JButton btnResumen =
                new JButton("Resumen del animal");

        JButton btnRecargar =
                new JButton("Recargar animales");

        JButton btnCerrar =
                new JButton("Cerrar");

        JPanel panelBotones =
                new JPanel(new GridLayout(2, 4, 8, 8));

        panelBotones.add(btnAnimal);
        panelBotones.add(btnEtapa);
        panelBotones.add(btnFechas);
        panelBotones.add(btnTodos);
        panelBotones.add(btnResumen);
        panelBotones.add(btnRecargar);
        panelBotones.add(btnCerrar);

        JPanel panelSuperior =
                new JPanel(new BorderLayout(10, 10));

        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 0, 10));

        panelSuperior.add(
                panelTitulo, BorderLayout.NORTH);

        panelSuperior.add(
                panelFiltros, BorderLayout.CENTER);

        panelSuperior.add(
                panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // TABLA DE HISTORIALES

        String[] columnas = {

            "ID Historial",
            "ID Animal",
            "Código animal",
            "Etapa anterior",
            "Etapa nueva",
            "Fecha del cambio",
            "ID Usuario",
            "Observaciones"
        };

        modeloTabla = new DefaultTableModel(
                columnas, 0) {

            @Override
            public boolean isCellEditable(
                    int fila, int columna) {

                return false;
            }

            @Override
            public Class<?> getColumnClass(
                    int columna) {

                if (columna == 0
                        || columna == 1
                        || columna == 6) {

                    return Integer.class;
                }

                return String.class;
            }
        };

        tabla = new JTable(modeloTabla);

        tabla.setRowHeight(25);

        tabla.setAutoCreateRowSorter(true);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        tabla.getTableHeader()
                .setReorderingAllowed(false);

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF);

        int[] anchos = {
            90, 80, 110, 130,
            130, 170, 90, 350
        };

        for (int i = 0; i < anchos.length; i++) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll =
                new JScrollPane(tabla);

        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Registros históricos"));

        add(scroll, BorderLayout.CENTER);

        // PANEL DE RESULTADOS

        lblTotal = new JLabel(
                "Total de registros: 0",
                JLabel.CENTER);

        lblEtapaActual = new JLabel(
                "Etapa actual: --",
                JLabel.CENTER);

        lblCambios = new JLabel(
                "Cambios efectivos: --",
                JLabel.CENTER);

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 14));

        lblEtapaActual.setFont(
                new Font("Arial", Font.BOLD, 14));

        lblCambios.setFont(
                new Font("Arial", Font.BOLD, 14));

        JPanel panelInferior =
                new JPanel(new GridLayout(1, 3));

        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 10, 15, 10));

        panelInferior.add(lblTotal);
        panelInferior.add(lblEtapaActual);
        panelInferior.add(lblCambios);

        add(panelInferior, BorderLayout.SOUTH);

        // EVENTOS

        btnAnimal.addActionListener(
                e -> consultarPorAnimal());

        btnEtapa.addActionListener(
                e -> consultarPorEtapa());

        btnFechas.addActionListener(
                e -> consultarPorFechas());

        btnTodos.addActionListener(
                e -> cargarHistorial());

        btnResumen.addActionListener(
                e -> consultarResumen());

        btnRecargar.addActionListener(e -> {

            cargarAnimales();
            cargarHistorial();
        });

        btnCerrar.addActionListener(
                e -> dispose());

        cbAnimal.addActionListener(e -> {

            limpiarResumen();
        });
    }

    // CARGAR ANIMALES

    private void cargarAnimales() {

        try {

            // VERIFICAR PERMISOS DE CONSULTA

            servicio.listar(idUsuario);

            List<Animal> animales =
                    animalDAO.listar();

            cbAnimal.removeAllItems();

            codigosAnimales.clear();

            cbAnimal.addItem(
                    new AnimalItem(
                            0, "Seleccione un animal"));

            for (Animal animal : animales) {

                codigosAnimales.put(
                        animal.getIdAnimal(),
                        animal.getCodigo());

                cbAnimal.addItem(
                        new AnimalItem(
                                animal.getIdAnimal(),
                                animal.getCodigo()
                                + " - "
                                + animal.getEstado()
                        ));
            }

            limpiarResumen();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CARGAR HISTORIAL GENERAL

    private void cargarHistorial() {

        try {

            List<HistorialEtapa> registros =
                    servicio.listar(idUsuario);

            mostrarRegistros(
                    registros,
                    "Total de registros");

            limpiarResumen();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // MOSTRAR REGISTROS EN LA TABLA

    private void mostrarRegistros(
            List<HistorialEtapa> registros,
            String titulo) {

        modeloTabla.setRowCount(0);

        for (HistorialEtapa historial : registros) {

            String etapaAnterior =
                    historial.getEtapaAnterior();

            if (etapaAnterior == null) {

                etapaAnterior = "REGISTRO INICIAL";
            }

            String codigo =
                    codigosAnimales.getOrDefault(
                            historial.getIdAnimal(),
                            String.valueOf(
                                    historial.getIdAnimal()));

            String fecha =
                    historial.getFechaCambio() == null
                    ? ""
                    : historial.getFechaCambio()
                            .format(formatoFecha);

            modeloTabla.addRow(new Object[]{

                historial.getIdHistorial(),

                historial.getIdAnimal(),

                codigo,

                etapaAnterior,

                historial.getEtapaNueva(),

                fecha,

                historial.getIdUsuario(),

                historial.getObservaciones()
            });
        }

        lblTotal.setText(
                titulo + ": " + registros.size());
    }

    // OBTENER ANIMAL SELECCIONADO

    private AnimalItem obtenerAnimalSeleccionado() {

        AnimalItem animal =
                (AnimalItem) cbAnimal.getSelectedItem();

        if (animal == null
                || animal.getIdAnimal() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal.");
        }

        return animal;
    }

    // CONSULTAR POR ANIMAL

    private void consultarPorAnimal() {

        try {

            AnimalItem animal =
                    obtenerAnimalSeleccionado();

            List<HistorialEtapa> registros =
                    servicio.listarPorAnimal(
                            idUsuario,
                            animal.getIdAnimal());

            mostrarRegistros(
                    registros,
                    "Registros del animal");

            mostrarResumen(animal.getIdAnimal());

            if (registros.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "El animal no tiene "
                        + "registros históricos.");
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR POR ETAPA

    private void consultarPorEtapa() {

        try {

            if (cbEtapa.getSelectedIndex() <= 0) {

                throw new IllegalArgumentException(
                        "Debes seleccionar una etapa.");
            }

            String etapa =
                    (String) cbEtapa.getSelectedItem();

            List<HistorialEtapa> registros =
                    servicio.listarPorEtapa(
                            idUsuario, etapa);

            mostrarRegistros(
                    registros,
                    "Registros de " + etapa);

            limpiarResumen();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR POR RANGO DE FECHAS

    private void consultarPorFechas() {

        try {

            LocalDate fechaInicio =
                    LocalDate.parse(
                            txtFechaInicio.getText()
                                    .trim());

            LocalDate fechaFin =
                    LocalDate.parse(
                            txtFechaFin.getText()
                                    .trim());

            List<HistorialEtapa> registros =
                    servicio.listarPorFechas(
                            idUsuario,
                            fechaInicio,
                            fechaFin);

            mostrarRegistros(
                    registros,
                    "Registros del período");

            limpiarResumen();

        } catch (
                java.time.format.DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Las fechas deben tener "
                    + "el formato AAAA-MM-DD.",
                    "Fecha incorrecta",
                    JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR RESUMEN DEL ANIMAL

    private void consultarResumen() {

        try {

            AnimalItem animal =
                    obtenerAnimalSeleccionado();

            mostrarResumen(animal.getIdAnimal());

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // MOSTRAR ETAPA ACTUAL Y CAMBIOS EFECTIVOS

    private void mostrarResumen(int idAnimal)
            throws Exception {

        String etapa =
                servicio.consultarEtapaActual(
                        idUsuario, idAnimal);

        int cambios =
                servicio.contarCambiosPorAnimal(
                        idUsuario, idAnimal);

        String codigo =
                codigosAnimales.getOrDefault(
                        idAnimal,
                        String.valueOf(idAnimal));

        lblEtapaActual.setText(
                codigo + " | Etapa actual: " + etapa);

        lblCambios.setText(
                "Cambios efectivos: " + cambios);

        lblEtapaActual.setForeground(
                new Color(0, 128, 0));
    }

    // LIMPIAR RESUMEN

    private void limpiarResumen() {

        lblEtapaActual.setText(
                "Etapa actual: --");

        lblCambios.setText(
                "Cambios efectivos: --");

        lblEtapaActual.setForeground(
                UIManager.getColor("Label.foreground"));
    }

    // MOSTRAR ERRORES

    private void mostrarError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

        e.printStackTrace();
    }

    // CLASE AUXILIAR PARA EL COMBOBOX

    private static class AnimalItem {

        private final int idAnimal;
        private final String codigo;

        public AnimalItem(
                int idAnimal, String codigo) {

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
}

