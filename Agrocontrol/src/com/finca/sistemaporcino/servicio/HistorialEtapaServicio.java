
package com.finca.sistemaporcino.servicio;

import com.finca.sistemaporcino.dao.HistorialEtapaDAO;
import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.dao.UsuarioDAO;

import com.finca.sistemaporcino.modelo.HistorialEtapa;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class HistorialEtapaServicio {

    private final HistorialEtapaDAO historialDAO =
            new HistorialEtapaDAO();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAO();

    // VERIFICAR USUARIO Y PERMISOS

    private Usuario verificarUsuario(int idUsuario)
            throws SQLException {

        List<Usuario> usuarios = usuarioDAO.listar();

        for (Usuario usuario : usuarios) {

            if (usuario.getIdUsuario() == idUsuario
                    && usuario.isActivo()) {

                if ("ADMINISTRADOR".equals(usuario.getRol())
                        || "EMPLEADO".equals(usuario.getRol())) {

                    return usuario;
                }
            }
        }

        throw new SecurityException(
                "El usuario no existe o no tiene permisos."
        );
    }

    // VERIFICAR EXISTENCIA DEL ANIMAL

    private Animal buscarAnimal(int idAnimal)
            throws SQLException {

        if (idAnimal <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal."
            );
        }

        List<Animal> animales = animalDAO.listar();

        for (Animal animal : animales) {

            if (animal.getIdAnimal() == idAnimal) {

                return animal;
            }
        }

        throw new IllegalArgumentException(
                "El animal seleccionado no existe."
        );
    }

    // VALIDAR ETAPA

    private String validarEtapa(String etapa) {

        if (etapa == null || etapa.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes seleccionar una etapa."
            );
        }

        etapa = etapa.trim().toUpperCase(Locale.ROOT);

        if (!"CRECIMIENTO".equals(etapa)
                && !"ENGORDE".equals(etapa)) {

            throw new IllegalArgumentException(
                    "La etapa seleccionada no es válida."
            );
        }

        return etapa;
    }

    // VALIDAR RANGO DE FECHAS

    private void validarFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        if (fechaInicio == null || fechaFin == null) {

            throw new IllegalArgumentException(
                    "Debes ingresar ambas fechas."
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser "
                    + "posterior a la fecha final."
            );
        }

        if (fechaFin.isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha final no puede ser futura."
            );
        }
    }

    // LISTAR TODOS LOS HISTORIALES

    public List<HistorialEtapa> listar(int idUsuario)
            throws SQLException {

        verificarUsuario(idUsuario);

        return historialDAO.listar();
    }

    // CONSULTAR HISTORIAL POR ANIMAL

    public List<HistorialEtapa> listarPorAnimal(
            int idUsuario,
            int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return historialDAO.listarPorAnimal(idAnimal);
    }

    // CONSULTAR HISTORIAL POR ETAPA

    public List<HistorialEtapa> listarPorEtapa(
            int idUsuario,
            String etapa)
            throws SQLException {

        verificarUsuario(idUsuario);

        etapa = validarEtapa(etapa);

        return historialDAO.listarPorEtapa(etapa);
    }

    // CONSULTAR HISTORIAL POR FECHAS

    public List<HistorialEtapa> listarPorFechas(
            int idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws SQLException {

        verificarUsuario(idUsuario);

        validarFechas(fechaInicio, fechaFin);

        return historialDAO.listarPorFechas(
                fechaInicio,
                fechaFin
        );
    }

    // BUSCAR HISTORIAL POR ID

    public HistorialEtapa buscarPorId(
            int idUsuario,
            int idHistorial)
            throws SQLException {

        verificarUsuario(idUsuario);

        if (idHistorial <= 0) {

            throw new IllegalArgumentException(
                    "El identificador del historial no es válido."
            );
        }

        HistorialEtapa historial =
                historialDAO.buscarPorId(idHistorial);

        if (historial == null) {

            throw new IllegalArgumentException(
                    "El registro histórico no existe."
            );
        }

        return historial;
    }

    // CONTAR CAMBIOS EFECTIVOS DE ETAPA

    public int contarCambiosPorAnimal(
            int idUsuario,
            int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return historialDAO.contarCambiosPorAnimal(
                idAnimal
        );
    }

    // CONSULTAR ETAPA ACTUAL DEL ANIMAL

    public String consultarEtapaActual(
            int idUsuario,
            int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        Animal animal = buscarAnimal(idAnimal);

        return animal.getEtapa();
    }
}
