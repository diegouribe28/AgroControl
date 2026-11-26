
package com.finca.sistemaporcino.interfaz;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.Dimension;

public class MenuPrincipal extends JFrame {

    private final int idUsuario;

    public MenuPrincipal(
            int idUsuario, String nombre, String rol) {

        this.idUsuario = idUsuario;

        setTitle("AgroControl - Menú principal");

        setSize(850, 550);

        setMinimumSize(new Dimension(700, 450));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        // ENCABEZADO

        JLabel lblTitulo = new JLabel(
                "AGROCONTROL",
                JLabel.CENTER
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        JLabel lblSubtitulo = new JLabel(
                "Sistema de Gestión y Control Porcino",
                JLabel.CENTER
        );

        JLabel lblBienvenida = new JLabel(
                "Bienvenido: " + nombre
                + " | Rol: " + rol,
                JLabel.CENTER
        );

        JPanel panelEncabezado =
                new JPanel(new GridLayout(3, 1, 5, 5));

        panelEncabezado.setBorder(
                javax.swing.BorderFactory
                        .createEmptyBorder(15, 10, 15, 10)
        );

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(lblSubtitulo);
        panelEncabezado.add(lblBienvenida);

        // BOTONES DEL MENÚ

        JButton btnAnimales =
                new JButton("Gestión de animales");

        JButton btnPeso =
                new JButton("Registro de peso");

        JButton btnReproduccion =
                new JButton("Reproducción");

        JButton btnSanidad =
                new JButton("Registro sanitario");

        JButton btnAlimentacion =
                new JButton("Alimentación");

        JButton btnUsuarios =
                new JButton("Gestión de usuarios");

        JButton btnHistorial =
                new JButton("Historial de etapas");

        JButton btnCerrarSesion =
                new JButton("Cerrar sesión");

        // PANEL DE OPCIONES

        JPanel panelOpciones =
                new JPanel(new GridLayout(4, 2, 15, 15));

        panelOpciones.setBorder(
                javax.swing.BorderFactory
                        .createEmptyBorder(10, 25, 25, 25)
        );

        panelOpciones.add(btnAnimales);
        panelOpciones.add(btnPeso);
        panelOpciones.add(btnReproduccion);
        panelOpciones.add(btnSanidad);
        panelOpciones.add(btnAlimentacion);
        panelOpciones.add(btnUsuarios);
        panelOpciones.add(btnHistorial);
        panelOpciones.add(btnCerrarSesion);

        add(panelEncabezado, BorderLayout.NORTH);

        add(panelOpciones, BorderLayout.CENTER);

        // PERMISOS SEGÚN EL ROL

        btnUsuarios.setEnabled(
                "ADMINISTRADOR".equals(rol)
        );

        // GESTIÓN DE ANIMALES

        btnAnimales.addActionListener(e -> {

            GestionAnimales ventana =
                    new GestionAnimales(idUsuario, rol);

            ventana.setVisible(true);
        });

        // REGISTRO DE PESO

        btnPeso.addActionListener(e -> {

            GestionPeso ventana =
                    new GestionPeso(idUsuario, rol);

            ventana.setVisible(true);
        });

        // REPRODUCCIÓN

        btnReproduccion.addActionListener(e -> {

            GestionReproduccion ventana =
                    new GestionReproduccion(idUsuario, rol);

            ventana.setVisible(true);
        });

        // REGISTRO SANITARIO

        btnSanidad.addActionListener(e -> {

            GestionSanitario ventana =
                    new GestionSanitario(idUsuario, rol);

            ventana.setVisible(true);
        });

        // ALIMENTACIÓN

        btnAlimentacion.addActionListener(e -> {

            GestionAlimentacion ventana =
                    new GestionAlimentacion(idUsuario, rol);

            ventana.setVisible(true);
        });

        // GESTIÓN DE USUARIOS

        btnUsuarios.addActionListener(e -> {

            if ("ADMINISTRADOR".equals(rol)) {

                GestionUsuarios ventana =
                        new GestionUsuarios(idUsuario);

                ventana.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No tiene permisos para administrar usuarios.",
                        "Acceso denegado",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // HISTORIAL DE ETAPAS

        btnHistorial.addActionListener(e -> {

            GestionHistorialEtapas ventana =
                    new GestionHistorialEtapas(
                            idUsuario, rol
                    );

            ventana.setVisible(true);
        });

        // CERRAR SESIÓN

        btnCerrarSesion.addActionListener(e -> {

            int confirmacion =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Deseas cerrar la sesión?",
                            "Cerrar sesión",
                            JOptionPane.YES_NO_OPTION
                    );

            if (confirmacion != JOptionPane.YES_OPTION) {

                return;
            }

            // CERRAR TODAS LAS VENTANAS DEL SISTEMA

            for (Window ventana : Window.getWindows()) {

                if (ventana instanceof GestionUsuarios
                        || ventana instanceof GestionAnimales
                        || ventana instanceof GestionPeso
                        || ventana instanceof GestionAlimentacion
                        || ventana instanceof GestionSanitario
                        || ventana instanceof GestionReproduccion
                        || ventana instanceof GestionHistorialEtapas) {

                    ventana.dispose();
                }
            }

            dispose();

            InicioSesion inicio = new InicioSesion();

            inicio.setVisible(true);
        });
    }
}
