
package com.finca.sistemaporcino.servicio;

import com.finca.sistemaporcino.dao.RegistroPesoDAO;
import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.dao.UsuarioDAO;

import com.finca.sistemaporcino.modelo.RegistroPeso;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.Usuario;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegistroPesoServicio {

    private final RegistroPesoDAO pesoDAO =
            new RegistroPesoDAO();

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
                    + "registros de peso."
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

    // VALIDAR LOS DATOS DEL PESAJE

    private void validarDatos(RegistroPeso registro) {

        if (registro == null) {

            throw new IllegalArgumentException(
                    "El registro no puede estar vacío."
            );
        }

        if (registro.getIdAnimal() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un animal."
            );
        }

        Animal animal = buscarAnimal(
                registro.getIdAnimal()
        );

        // VALIDAR FECHA

        if (registro.getFecha() == null) {

            throw new IllegalArgumentException(
                    "La fecha del pesaje es obligatoria."
            );
        }

        if (registro.getFecha().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha del pesaje no puede ser futura."
            );
        }

        if (animal.getFechaNacimiento() != null
                && registro.getFecha().isBefore(
                        animal.getFechaNacimiento())) {

            throw new IllegalArgumentException(
                    "La fecha del pesaje no puede ser anterior "
                    + "al nacimiento del animal."
            );
        }

        // VALIDAR PESO

        if (registro.getPeso() == null) {

            throw new IllegalArgumentException(
                    "Debes ingresar el peso del animal."
            );
        }

        if (registro.getPeso()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El peso debe ser mayor que cero."
            );
        }

        if (registro.getPeso()
                .compareTo(new BigDecimal("99999.99")) > 0) {

            throw new IllegalArgumentException(
                    "El peso supera el valor máximo permitido."
            );
        }

        if (registro.getPeso()
                .stripTrailingZeros().scale() > 2) {

            throw new IllegalArgumentException(
                    "El peso debe tener máximo dos decimales."
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
    }

    // REGISTRAR UN NUEVO PESAJE

    public boolean registrar(int idUsuario,
                             RegistroPeso registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        validarDatos(registro);

        Animal animal = buscarAnimal(
                registro.getIdAnimal()
        );

        if (!"ACTIVO".equals(animal.getEstado())) {

            throw new IllegalArgumentException(
                    "Solo se pueden registrar pesajes "
                    + "de animales activos."
            );
        }

        registro.setIdUsuario(idUsuario);

        return pesoDAO.registrar(registro);
    }

    // LISTAR TODOS LOS REGISTROS

    public List<RegistroPeso> listar(int idUsuario)
            throws SQLException {

        verificarUsuario(idUsuario);

        return pesoDAO.listar();
    }

    // CONSULTAR HISTORIAL DE UN ANIMAL

    public List<RegistroPeso> listarPorAnimal(
            int idUsuario, int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return pesoDAO.listarPorAnimal(idAnimal);
    }

    // ACTUALIZAR REGISTRO DE PESO

    public boolean actualizar(int idUsuario,
                              RegistroPeso registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        if (registro == null
                || registro.getIdRegistro() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro de peso."
            );
        }

        RegistroPeso existente = pesoDAO.buscarPorId(
                registro.getIdRegistro()
        );

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro de peso no existe."
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

        return pesoDAO.actualizar(registro);
    }

    // ELIMINAR REGISTRO DE PESO

    public boolean eliminar(int idUsuario,
                            int idRegistro)
            throws SQLException {

        verificarAdministrador(idUsuario);

        if (idRegistro <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro de peso."
            );
        }

        RegistroPeso existente =
                pesoDAO.buscarPorId(idRegistro);

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro de peso no existe."
            );
        }

        return pesoDAO.eliminar(idRegistro);
    }

    // CALCULAR GANANCIA DE PESO

    public BigDecimal calcularGananciaPeso(
            int idUsuario, int idAnimal)
            throws SQLException {

        List<RegistroPeso> registros =
                listarPorAnimal(idUsuario, idAnimal);

        if (registros.size() < 2) {

            throw new IllegalStateException(
                    "Se necesitan al menos dos pesajes "
                    + "para calcular la ganancia de peso."
            );
        }

        RegistroPeso ultimo =
                registros.get(registros.size() - 1);

        RegistroPeso anterior =
                registros.get(registros.size() - 2);

        return ultimo.getPeso()
                .subtract(anterior.getPeso());
    }
}
