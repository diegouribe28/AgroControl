
package com.finca.sistemaporcino.servicio;

import com.finca.sistemaporcino.dao.RegistroAlimentacionDAO;
import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.dao.UsuarioDAO;

import com.finca.sistemaporcino.modelo.RegistroAlimentacion;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.Usuario;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RegistroAlimentacionServicio {

    private final RegistroAlimentacionDAO alimentacionDAO =
            new RegistroAlimentacionDAO();

    private final AnimalDAO animalDAO =
            new AnimalDAO();

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAO();

    // VERIFICAR USUARIO AUTENTICADO

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

    // VERIFICAR PERMISOS DE ADMINISTRADOR

    private void verificarAdministrador(int idUsuario)
            throws SQLException {

        Usuario usuario = verificarUsuario(idUsuario);

        if (!"ADMINISTRADOR".equals(usuario.getRol())) {

            throw new SecurityException(
                    "Solo el administrador puede eliminar "
                    + "registros de alimentación."
            );
        }
    }

    // BUSCAR ANIMAL

    private Animal buscarAnimal(int idAnimal) {

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

    // VALIDAR DATOS DE ALIMENTACIÓN

    private Animal validarDatos(
            RegistroAlimentacion registro) {

        if (registro == null) {

            throw new IllegalArgumentException(
                    "El registro no puede estar vacío."
            );
        }

        // VALIDAR ANIMAL

        if (registro.getIdAnimal() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal."
            );
        }

        Animal animal = buscarAnimal(
                registro.getIdAnimal()
        );

        // VALIDAR FECHA Y HORA

        if (registro.getFecha() == null) {

            throw new IllegalArgumentException(
                    "La fecha y hora son obligatorias."
            );
        }

        if (registro.getFecha()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "La fecha y hora no pueden ser futuras."
            );
        }

        if (animal.getFechaNacimiento() != null
                && registro.getFecha().toLocalDate()
                        .isBefore(animal.getFechaNacimiento())) {

            throw new IllegalArgumentException(
                    "La fecha de alimentación no puede ser "
                    + "anterior al nacimiento del animal."
            );
        }

        // VALIDAR TIPO DE ALIMENTO

        if (registro.getTipoAlimento() == null
                || registro.getTipoAlimento()
                        .trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes ingresar el tipo de alimento."
            );
        }

        String tipoAlimento =
                registro.getTipoAlimento().trim();

        if (tipoAlimento.length() > 80) {

            throw new IllegalArgumentException(
                    "El tipo de alimento no puede superar "
                    + "los 80 caracteres."
            );
        }

        registro.setTipoAlimento(tipoAlimento);

        // VALIDAR CANTIDAD DE ALIMENTO

        if (registro.getCantidadKg() == null) {

            throw new IllegalArgumentException(
                    "Debes ingresar la cantidad de alimento."
            );
        }

        if (registro.getCantidadKg()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        if (registro.getCantidadKg()
                .compareTo(new BigDecimal("99999.999")) > 0) {

            throw new IllegalArgumentException(
                    "La cantidad supera el máximo permitido."
            );
        }

        if (registro.getCantidadKg()
                .stripTrailingZeros().scale() > 3) {

            throw new IllegalArgumentException(
                    "La cantidad debe tener máximo "
                    + "tres cifras decimales."
            );
        }

        // VALIDAR OBSERVACIONES

        if (registro.getObservaciones() != null) {

            String observaciones =
                    registro.getObservaciones().trim();

            registro.setObservaciones(
                    observaciones.isEmpty()
                            ? null : observaciones
            );
        }

        return animal;
    }

    // REGISTRAR ALIMENTACIÓN

    public boolean registrar(
            int idUsuario,
            RegistroAlimentacion registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        Animal animal = validarDatos(registro);

        if (!"ACTIVO".equals(animal.getEstado())) {

            throw new IllegalArgumentException(
                    "Solo se puede registrar alimentación "
                    + "de animales activos."
            );
        }

        registro.setIdUsuario(idUsuario);

        return alimentacionDAO.registrar(registro);
    }

    // LISTAR TODOS LOS REGISTROS

    public List<RegistroAlimentacion> listar(
            int idUsuario) throws SQLException {

        verificarUsuario(idUsuario);

        return alimentacionDAO.listar();
    }

    // CONSULTAR HISTORIAL DE UN ANIMAL

    public List<RegistroAlimentacion> listarPorAnimal(
            int idUsuario, int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return alimentacionDAO.listarPorAnimal(idAnimal);
    }

    // ACTUALIZAR REGISTRO DE ALIMENTACIÓN

    public boolean actualizar(
            int idUsuario,
            RegistroAlimentacion registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        if (registro == null
                || registro.getIdRegistro() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro "
                    + "de alimentación."
            );
        }

        RegistroAlimentacion existente =
                alimentacionDAO.buscarPorId(
                        registro.getIdRegistro()
                );

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro de alimentación no existe."
            );
        }

        // CONSERVAR EL ANIMAL Y EL USUARIO ORIGINAL

        registro.setIdAnimal(
                existente.getIdAnimal()
        );

        registro.setIdUsuario(
                existente.getIdUsuario()
        );

        validarDatos(registro);

        return alimentacionDAO.actualizar(registro);
    }

    // ELIMINAR REGISTRO DE ALIMENTACIÓN

    public boolean eliminar(
            int idUsuario, int idRegistro)
            throws SQLException {

        verificarAdministrador(idUsuario);

        if (idRegistro <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro "
                    + "de alimentación."
            );
        }

        RegistroAlimentacion existente =
                alimentacionDAO.buscarPorId(idRegistro);

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro de alimentación no existe."
            );
        }

        return alimentacionDAO.eliminar(idRegistro);
    }

    // CALCULAR CANTIDAD ACUMULADA DE ALIMENTO

    public BigDecimal calcularConsumoTotal(
            int idUsuario, int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return alimentacionDAO.calcularConsumoTotal(
                idAnimal
        );
    }
}
