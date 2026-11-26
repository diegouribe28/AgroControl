
package com.finca.sistemaporcino.servicio;

import com.finca.sistemaporcino.dao.ReproduccionDAO;
import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.dao.UsuarioDAO;

import com.finca.sistemaporcino.modelo.Reproduccion;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ReproduccionServicio {

    private final ReproduccionDAO reproduccionDAO =
            new ReproduccionDAO();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAO();

    private static final int DIAS_GESTACION = 114;

    // VERIFICAR USUARIO

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

    // VERIFICAR ADMINISTRADOR

    private void verificarAdministrador(int idUsuario)
            throws SQLException {

        Usuario usuario = verificarUsuario(idUsuario);

        if (!"ADMINISTRADOR".equals(usuario.getRol())) {

            throw new SecurityException(
                    "Solo el administrador puede eliminar "
                    + "registros reproductivos."
            );
        }
    }

    // BUSCAR CERDA

    private Animal buscarCerda(int idCerda)
            throws SQLException {

        List<Animal> animales = animalDAO.listar();

        for (Animal animal : animales) {

            if (animal.getIdAnimal() == idCerda) {

                if (!"HEMBRA".equals(animal.getSexo())) {

                    throw new IllegalArgumentException(
                            "El animal seleccionado debe ser hembra."
                    );
                }

                return animal;
            }
        }

        throw new IllegalArgumentException(
                "La cerda seleccionada no existe."
        );
    }

    // CALCULAR FECHA PROBABLE DE PARTO

    public LocalDate calcularFechaProbableParto(
            LocalDate fechaServicio) {

        if (fechaServicio == null) {

            throw new IllegalArgumentException(
                    "Debes ingresar la fecha de servicio."
            );
        }

        return fechaServicio.plusDays(DIAS_GESTACION);
    }

    // VALIDAR RESULTADO REPRODUCTIVO

    private boolean resultadoValido(String resultado) {

        return "PENDIENTE".equals(resultado)
                || "PARTO".equals(resultado)
                || "FALSA_GESTACION".equals(resultado)
                || "NO_GESTANTE".equals(resultado)
                || "PERDIDA_GESTACIONAL".equals(resultado);
    }

    // VALIDAR DATOS

    private Animal validarDatos(Reproduccion registro)
            throws SQLException {

        if (registro == null) {

            throw new IllegalArgumentException(
                    "El registro reproductivo no puede estar vacío."
            );
        }

        // VALIDAR CERDA

        if (registro.getIdCerda() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar una cerda."
            );
        }

        Animal cerda = buscarCerda(
                registro.getIdCerda()
        );

        // VALIDAR FECHA DE SERVICIO

        if (registro.getFechaServicio() == null) {

            throw new IllegalArgumentException(
                    "La fecha de servicio es obligatoria."
            );
        }

        if (registro.getFechaServicio()
                .isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha de servicio no puede ser futura."
            );
        }

        if (cerda.getFechaNacimiento() != null
                && registro.getFechaServicio().isBefore(
                        cerda.getFechaNacimiento())) {

            throw new IllegalArgumentException(
                    "La fecha de servicio no puede ser "
                    + "anterior al nacimiento de la cerda."
            );
        }

        // CALCULAR FECHA PROBABLE DE PARTO

        if (registro.getFechaProbableParto() == null) {

            registro.setFechaProbableParto(
                    calcularFechaProbableParto(
                            registro.getFechaServicio()
                    )
            );
        }

        // VALIDAR FECHA PROBABLE

        if (registro.getFechaProbableParto().isBefore(
                registro.getFechaServicio())) {

            throw new IllegalArgumentException(
                    "La fecha probable de parto no puede "
                    + "ser anterior a la fecha de servicio."
            );
        }

        // VALIDAR RESULTADO

        if (registro.getResultado() == null
                || registro.getResultado().trim().isEmpty()) {

            registro.setResultado("PENDIENTE");
        }

        String resultado = registro.getResultado()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!resultadoValido(resultado)) {

            throw new IllegalArgumentException(
                    "El resultado reproductivo no es válido."
            );
        }

        registro.setResultado(resultado);

        // VALIDAR FECHA REAL DE PARTO

        if (registro.getFechaPartoReal() != null) {

            if (registro.getFechaPartoReal().isBefore(
                    registro.getFechaServicio())) {

                throw new IllegalArgumentException(
                        "La fecha real de parto no puede "
                        + "ser anterior a la fecha de servicio."
                );
            }

            if (registro.getFechaPartoReal()
                    .isAfter(LocalDate.now())) {

                throw new IllegalArgumentException(
                        "La fecha real de parto no puede ser futura."
                );
            }
        }

        // SI EL RESULTADO ES PARTO

        if ("PARTO".equals(resultado)) {

            if (registro.getFechaPartoReal() == null) {

                throw new IllegalArgumentException(
                        "Debes ingresar la fecha real de parto."
                );
            }

        } else {

            if (registro.getFechaPartoReal() != null) {

                throw new IllegalArgumentException(
                        "La fecha real de parto solo se permite "
                        + "cuando el resultado es PARTO."
                );
            }
        }

        // VALIDAR OBSERVACIONES

        if (registro.getObservaciones() != null) {

            String observaciones =
                    registro.getObservaciones().trim();

            registro.setObservaciones(
                    observaciones.isEmpty()
                    ? null
                    : observaciones
            );
        }

        return cerda;
    }

    // REGISTRAR REPRODUCCIÓN

    public boolean registrar(
            int idUsuario,
            Reproduccion registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        Animal cerda = validarDatos(registro);

        if (!"ACTIVO".equals(cerda.getEstado())) {

            throw new IllegalArgumentException(
                    "Solo se pueden registrar servicios "
                    + "reproductivos de cerdas activas."
            );
        }

        // EVITAR SERVICIOS PENDIENTES DUPLICADOS

        if ("PENDIENTE".equals(registro.getResultado())
                && reproduccionDAO.existePendiente(
                        registro.getIdCerda(), 0)) {

            throw new IllegalArgumentException(
                    "La cerda ya tiene un servicio "
                    + "reproductivo pendiente. "
                    + "Debes actualizar su resultado primero."
            );
        }

        registro.setIdUsuario(idUsuario);

        return reproduccionDAO.registrar(registro);
    }

    // LISTAR TODOS LOS REGISTROS

    public List<Reproduccion> listar(int idUsuario)
            throws SQLException {

        verificarUsuario(idUsuario);

        return reproduccionDAO.listar();
    }

    // CONSULTAR HISTORIAL POR CERDA

    public List<Reproduccion> listarPorCerda(
            int idUsuario, int idCerda)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarCerda(idCerda);

        return reproduccionDAO.listarPorCerda(idCerda);
    }

    // ACTUALIZAR REGISTRO REPRODUCTIVO

    public boolean actualizar(
            int idUsuario,
            Reproduccion registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        if (registro == null
                || registro.getIdReproduccion() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro reproductivo."
            );
        }

        Reproduccion existente =
                reproduccionDAO.buscarPorId(
                        registro.getIdReproduccion()
                );

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro reproductivo no existe."
            );
        }

        // CONSERVAR CERDA Y USUARIO ORIGINALES

        registro.setIdCerda(
                existente.getIdCerda()
        );

        registro.setIdUsuario(
                existente.getIdUsuario()
        );

        validarDatos(registro);

        // VERIFICAR OTROS SERVICIOS PENDIENTES

        if ("PENDIENTE".equals(registro.getResultado())
                && reproduccionDAO.existePendiente(
                        registro.getIdCerda(),
                        registro.getIdReproduccion())) {

            throw new IllegalArgumentException(
                    "La cerda ya tiene otro servicio "
                    + "reproductivo pendiente."
            );
        }

        return reproduccionDAO.actualizar(registro);
    }

    // ELIMINAR REGISTRO REPRODUCTIVO

    public boolean eliminar(
            int idUsuario, int idReproduccion)
            throws SQLException {

        verificarAdministrador(idUsuario);

        if (idReproduccion <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro reproductivo."
            );
        }

        Reproduccion existente =
                reproduccionDAO.buscarPorId(idReproduccion);

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro reproductivo no existe."
            );
        }

        return reproduccionDAO.eliminar(idReproduccion);
    }

    // CONSULTAR PRÓXIMOS PARTOS

    public List<Reproduccion> listarProximosPartos(
            int idUsuario)
            throws SQLException {

        verificarUsuario(idUsuario);

        return reproduccionDAO.listarProximosPartos();
    }

    // CONSULTAR FECHAS DE PARTO VENCIDAS

    public List<Reproduccion> listarPartosVencidos(
            int idUsuario)
            throws SQLException {

        verificarUsuario(idUsuario);

        return reproduccionDAO.listarPartosVencidos();
    }

    // CONTAR PARTOS POR CERDA

    public int contarPartosPorCerda(
            int idUsuario, int idCerda)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarCerda(idCerda);

        return reproduccionDAO.contarPartosPorCerda(
                idCerda
        );
    }
}
