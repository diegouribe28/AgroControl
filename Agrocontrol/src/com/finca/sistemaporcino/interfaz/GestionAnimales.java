
package com.finca.sistemaporcino.interfaz;

import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.modelo.Animal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GestionAnimales extends JFrame {

    private final int idUsuario;
    private final String rol;

    private JTextField txtCodigo;
    private JTextField txtRaza;
    private JTextField txtFechaNacimiento;
    private JTextField txtBuscar;

    private JComboBox<String> cbSexo;
    private JComboBox<String> cbEtapa;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnDesactivar;
    private JButton btnEliminar;

    private JTable tabla;
    private DefaultTableModel modelo;

    private int idAnimalSeleccionado = -1;
    private String estadoSeleccionado = "";

    public GestionAnimales(int idUsuario, String rol) {

        this.idUsuario = idUsuario;
        this.rol = rol;

        setTitle("AgroControl - Gestión de animales");
        setSize(1200, 700);
        setMinimumSize(new Dimension(900, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // TÍTULO DE LA VENTANA

        JLabel lblTitulo = new JLabel(
                "GESTIÓN DE ANIMALES PORCINOS",
                JLabel.CENTER
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        JLabel lblUsuario = new JLabel(
                "Usuario que registra: " + idUsuario
                + " | Rol: " + rol,
                JLabel.CENTER
        );

        // CAMPOS DEL FORMULARIO

        txtCodigo = new JTextField(12);
        txtRaza = new JTextField(12);
        txtFechaNacimiento = new JTextField(12);

        cbSexo = new JComboBox<>(
                new String[]{"MACHO", "HEMBRA"}
        );

        cbEtapa = new JComboBox<>(
                new String[]{"CRECIMIENTO", "ENGORDE"}
        );

        JPanel panelFormulario = new JPanel(
                new GridLayout(2, 5, 8, 8)
        );

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos del animal"
                )
        );

        panelFormulario.add(new JLabel("Código:"));
        panelFormulario.add(new JLabel("Sexo:"));
        panelFormulario.add(new JLabel("Raza:"));
        panelFormulario.add(new JLabel("Fecha nacimiento:"));
        panelFormulario.add(new JLabel("Etapa:"));

        panelFormulario.add(txtCodigo);
        panelFormulario.add(cbSexo);
        panelFormulario.add(txtRaza);
        panelFormulario.add(txtFechaNacimiento);
        panelFormulario.add(cbEtapa);

        // BOTONES DE ADMINISTRACIÓN

        btnRegistrar = new JButton("Registrar animal");
        btnActualizar = new JButton("Actualizar");
        btnDesactivar = new JButton("Activar / Desactivar");
        btnEliminar = new JButton("Eliminar");

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnHistorial = new JButton("Historial de etapas");

        // PERMISOS SEGÚN EL ROL

        btnEliminar.setEnabled(esAdministrador());
        btnDesactivar.setEnabled(esAdministrador());

        // PANEL DE BÚSQUEDA

        txtBuscar = new JTextField(15);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar todos");

        JPanel panelBusqueda = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelBusqueda.setBorder(
                BorderFactory.createTitledBorder(
                        "Buscar animales"
                )
        );

        panelBusqueda.add(new JLabel("Código:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);

        // CREAR TABLA

        String[] columnas = {
            "ID",
            "Código",
            "Sexo",
            "Raza",
            "Fecha nacimiento",
            "Etapa",
            "Estado",
            "Fecha registro"
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

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        tabla.getSelectionModel()
                .addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                seleccionarAnimal();
            }
        });

        JScrollPane desplazamiento =
                new JScrollPane(tabla);

        desplazamiento.setBorder(
                BorderFactory.createTitledBorder(
                        "Animales registrados"
                )
        );

        // PANEL DE ACCIONES

        JPanel panelAcciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelAcciones.add(btnRegistrar);
        panelAcciones.add(btnActualizar);
        panelAcciones.add(btnDesactivar);
        panelAcciones.add(btnEliminar);
        panelAcciones.add(btnHistorial);
        panelAcciones.add(btnLimpiar);

        // PANEL DE ENCABEZADO

        JPanel panelEncabezado = new JPanel(
                new BorderLayout(5, 5)
        );

        panelEncabezado.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 5, 10
                )
        );

        panelEncabezado.add(
                lblTitulo,
                BorderLayout.NORTH
        );

        panelEncabezado.add(
                lblUsuario,
                BorderLayout.CENTER
        );

        // PANEL SUPERIOR

        JPanel panelSuperior = new JPanel(
                new BorderLayout(5, 5)
        );

        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 10, 5, 10
                )
        );

        panelSuperior.add(
                panelFormulario,
                BorderLayout.NORTH
        );

        JPanel panelCentroSuperior = new JPanel(
                new BorderLayout(5, 5)
        );

        panelCentroSuperior.add(
                panelBusqueda,
                BorderLayout.CENTER
        );

        panelCentroSuperior.add(
                panelAcciones,
                BorderLayout.SOUTH
        );

        panelSuperior.add(
                panelCentroSuperior,
                BorderLayout.CENTER
        );

        // PANEL PRINCIPAL

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(5, 5)
        );

        panelPrincipal.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                desplazamiento,
                BorderLayout.CENTER
        );

        add(panelEncabezado, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);

        // EVENTOS DE LOS BOTONES

        btnRegistrar.addActionListener(
                e -> registrarAnimal()
        );

        btnActualizar.addActionListener(
                e -> actualizarAnimal()
        );

        btnBuscar.addActionListener(
                e -> buscarAnimal()
        );

        btnMostrarTodos.addActionListener(e -> {

            txtBuscar.setText("");
            limpiarCampos();
            cargarAnimales();
        });

        btnDesactivar.addActionListener(
                e -> desactivarAnimal()
        );

        btnEliminar.addActionListener(
                e -> eliminarAnimal()
        );

        btnLimpiar.addActionListener(
                e -> limpiarCampos()
        );
        
        btnHistorial.addActionListener(e -> {

    GestionHistorialEtapas ventana =
            new GestionHistorialEtapas(idUsuario, rol);

    ventana.setVisible(true);
});

        // CARGAR ANIMALES AL ABRIR LA VENTANA

        cargarAnimales();
    }

    // VERIFICAR SI EL USUARIO ES ADMINISTRADOR

    private boolean esAdministrador() {

        return "ADMINISTRADOR".equals(rol);
    }

    // VALIDAR PERMISOS

    private boolean validarPermisos() {

        if (!esAdministrador()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permisos para realizar esta operación.",
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    // SELECCIONAR ANIMAL DE LA TABLA

    private void seleccionarAnimal() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            idAnimalSeleccionado = -1;
            estadoSeleccionado = "";

            btnDesactivar.setText(
                    "Activar / Desactivar"
            );

            return;
        }

        fila = tabla.convertRowIndexToModel(fila);

        idAnimalSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        txtCodigo.setText(
                modelo.getValueAt(fila, 1).toString()
        );

        cbSexo.setSelectedItem(
                modelo.getValueAt(fila, 2).toString()
        );

        Object raza = modelo.getValueAt(fila, 3);

        txtRaza.setText(
                raza == null ? "" : raza.toString()
        );

        Object fecha = modelo.getValueAt(fila, 4);

        txtFechaNacimiento.setText(
                fecha == null ? "" : fecha.toString()
        );

        cbEtapa.setSelectedItem(
                modelo.getValueAt(fila, 5).toString()
        );

        estadoSeleccionado =
                modelo.getValueAt(fila, 6).toString();

        if ("ACTIVO".equals(estadoSeleccionado)) {

            btnDesactivar.setText("Desactivar");

        } else if ("INACTIVO".equals(estadoSeleccionado)) {

            btnDesactivar.setText("Activar");

        } else {

            btnDesactivar.setText("Activar / Desactivar");
        }
    }

    // REGISTRAR ANIMAL

    private void registrarAnimal() {

        String codigo = txtCodigo.getText().trim();
        String raza = txtRaza.getText().trim();

        String fechaTexto =
                txtFechaNacimiento.getText().trim();

        String sexo =
                cbSexo.getSelectedItem().toString();

        String etapa =
                cbEtapa.getSelectedItem().toString();

        // VALIDAR CÓDIGO

        if (codigo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes escribir el código del animal."
            );

            txtCodigo.requestFocus();
            return;
        }

        // VALIDAR RAZA

        if (raza.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes escribir la raza del animal."
            );

            txtRaza.requestFocus();
            return;
        }

        // VALIDAR FECHA

        if (fechaTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes escribir la fecha de nacimiento."
            );

            txtFechaNacimiento.requestFocus();
            return;
        }

        LocalDate fechaNacimiento;

        try {

            fechaNacimiento =
                    LocalDate.parse(fechaTexto);

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Fecha incorrecta. Usa AAAA-MM-DD."
            );

            txtFechaNacimiento.requestFocus();
            return;
        }

        if (fechaNacimiento.isAfter(LocalDate.now())) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fecha no puede ser futura."
            );

            txtFechaNacimiento.requestFocus();
            return;
        }

        Animal animal = new Animal(
                codigo,
                sexo,
                raza,
                fechaNacimiento,
                etapa,
                idUsuario
        );

        AnimalDAO dao = new AnimalDAO();

        boolean registrado = dao.registrar(animal);

        if (registrado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Animal registrado correctamente."
            );

            limpiarCampos();
            cargarAnimales();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar el animal.\n"
                    + "Revisa Output en NetBeans.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // CONSULTAR TODOS LOS ANIMALES

    private void cargarAnimales() {

        tabla.clearSelection();

        idAnimalSeleccionado = -1;
        estadoSeleccionado = "";

        modelo.setRowCount(0);

        AnimalDAO dao = new AnimalDAO();

        List<Animal> animales = dao.listar();

        for (Animal animal : animales) {

            agregarFila(animal);
        }

        ajustarColumnas();
    }

    // BUSCAR ANIMAL POR CÓDIGO

    private void buscarAnimal() {

        String codigo = txtBuscar.getText().trim();

        if (codigo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Escribe el código que deseas buscar."
            );

            txtBuscar.requestFocus();
            return;
        }

        limpiarCampos();

        modelo.setRowCount(0);

        AnimalDAO dao = new AnimalDAO();

        List<Animal> animales = dao.listar();

        for (Animal animal : animales) {

            if (animal.getCodigo()
                    .equalsIgnoreCase(codigo)) {

                agregarFila(animal);
            }
        }

        if (modelo.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró ese animal."
            );
        }

        ajustarColumnas();
    }

    // AGREGAR FILA A LA TABLA

    private void agregarFila(Animal animal) {

        Object[] fila = {

            animal.getIdAnimal(),
            animal.getCodigo(),
            animal.getSexo(),
            animal.getRaza(),
            animal.getFechaNacimiento(),
            animal.getEtapa(),
            animal.getEstado(),
            animal.getFechaRegistro()
        };

        modelo.addRow(fila);
    }

    // AJUSTAR ANCHO DE LAS COLUMNAS

    private void ajustarColumnas() {

        int[] anchos = {

            50, 100, 90, 120,
            150, 120, 100, 180
        };

        for (int i = 0; i < anchos.length; i++) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }
    }

    // ACTUALIZAR ANIMAL

    private void actualizarAnimal() {

        if (idAnimalSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona un animal de la tabla."
            );

            return;
        }

        String codigo = txtCodigo.getText().trim();
        String raza = txtRaza.getText().trim();

        String fechaTexto =
                txtFechaNacimiento.getText().trim();

        if (codigo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El código del animal es obligatorio."
            );

            txtCodigo.requestFocus();
            return;
        }

        if (raza.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La raza es obligatoria."
            );

            txtRaza.requestFocus();
            return;
        }

        if (fechaTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fecha de nacimiento es obligatoria."
            );

            txtFechaNacimiento.requestFocus();
            return;
        }

        LocalDate fechaNacimiento;

        try {

            fechaNacimiento =
                    LocalDate.parse(fechaTexto);

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Usa el formato AAAA-MM-DD."
            );

            txtFechaNacimiento.requestFocus();
            return;
        }

        if (fechaNacimiento.isAfter(LocalDate.now())) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fecha de nacimiento no puede ser futura."
            );

            return;
        }

        Animal animal = new Animal(

                idAnimalSeleccionado,
                codigo,
                cbSexo.getSelectedItem().toString(),
                raza,
                fechaNacimiento,
                cbEtapa.getSelectedItem().toString(),
                estadoSeleccionado,
                null,
                idUsuario
        );

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas guardar los cambios del animal "
                + codigo + "?",
                "Confirmar actualización",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {

            return;
        }

        AnimalDAO dao = new AnimalDAO();

        boolean actualizado = dao.actualizar(animal);

        if (actualizado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Animal actualizado correctamente."
            );

            limpiarCampos();
            cargarAnimales();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo actualizar el animal.\n"
                    + "Revisa Output en NetBeans.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ACTIVAR O DESACTIVAR ANIMAL

    private void desactivarAnimal() {

        // SOLO EL ADMINISTRADOR TIENE PERMISO

        if (!validarPermisos()) {

            return;
        }

        if (idAnimalSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona un animal de la tabla."
            );

            return;
        }

        String estadoActual = estadoSeleccionado;

        String nuevoEstado;

        if ("ACTIVO".equals(estadoActual)) {

            nuevoEstado = "INACTIVO";

        } else if ("INACTIVO".equals(estadoActual)) {

            nuevoEstado = "ACTIVO";

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Este animal tiene estado "
                    + estadoActual
                    + " y no se puede activar o desactivar "
                    + "con este botón."
            );

            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cambiar el estado de "
                + estadoActual + " a " + nuevoEstado + "?",
                "Confirmar cambio de estado",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {

            return;
        }

        AnimalDAO dao = new AnimalDAO();

        boolean cambiado =
                dao.cambiarEstado(idAnimalSeleccionado);

        if (cambiado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Estado cambiado correctamente."
            );

            limpiarCampos();
            cargarAnimales();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cambiar el estado del animal.\n"
                    + "Revisa Output en NetBeans.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ELIMINAR ANIMAL

    
private void eliminarAnimal() {

    // VERIFICAR PERMISOS

    if (!validarPermisos()) {

        return;
    }

    // VERIFICAR ANIMAL SELECCIONADO

    if (idAnimalSeleccionado == -1) {

        JOptionPane.showMessageDialog(
                this,
                "Primero selecciona un animal de la tabla."
        );

        return;
    }

    // CONFIRMAR ELIMINACIÓN

    int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas eliminar físicamente este animal?\n"
            + "Esta acción no se puede deshacer.\n\n"
            + "Si tiene registros históricos asociados,\n"
            + "PostgreSQL impedirá su eliminación.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (confirmacion != JOptionPane.YES_OPTION) {

        return;
    }

    AnimalDAO dao = new AnimalDAO();

    boolean eliminado =
            dao.eliminar(idAnimalSeleccionado);

    if (eliminado) {

        JOptionPane.showMessageDialog(
                this,
                "Animal eliminado correctamente."
        );

        limpiarCampos();
        cargarAnimales();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo eliminar el animal.\n\n"
                + "Puede tener registros asociados de:\n"
                + "- Historial de etapas.\n"
                + "- Registro de peso.\n"
                + "- Alimentación.\n"
                + "- Registro sanitario.\n"
                + "- Reproducción.\n\n"
                + "Si deseas retirarlo de las operaciones,\n"
                + "utiliza la opción Desactivar.\n\n"
                + "Consulta Output en NetBeans "
                + "para conocer el error exacto.",
                "Eliminación no realizada",
                JOptionPane.WARNING_MESSAGE
        );
    }
}


    // LIMPIAR FORMULARIO

    private void limpiarCampos() {

        idAnimalSeleccionado = -1;
        estadoSeleccionado = "";

        tabla.clearSelection();

        txtCodigo.setText("");
        txtRaza.setText("");
        txtFechaNacimiento.setText("");

        cbSexo.setSelectedIndex(0);
        cbEtapa.setSelectedIndex(0);

        btnDesactivar.setText("Activar / Desactivar");

        txtCodigo.requestFocus();
    }
}
