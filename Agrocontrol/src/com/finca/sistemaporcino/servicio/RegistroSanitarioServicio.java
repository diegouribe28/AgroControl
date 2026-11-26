
package com.finca.sistemaporcino.servicio;

import com.finca.sistemaporcino.dao.RegistroSanitarioDAO;
import com.finca.sistemaporcino.dao.AnimalDAO;
import com.finca.sistemaporcino.dao.UsuarioDAO;

import com.finca.sistemaporcino.modelo.RegistroSanitario;
import com.finca.sistemaporcino.modelo.Animal;
import com.finca.sistemaporcino.modelo.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegistroSanitarioServicio {

    private final RegistroSanitarioDAO sanitarioDAO =
            new RegistroSanitarioDAO();

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
                    + "registros sanitarios."
            );
        }
    }

    // BUSCAR ANIMAL

    private Animal buscarAnimal(int idAnimal)
            throws SQLException {

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

    // VALIDAR DATOS DEL REGISTRO SANITARIO

    private Animal validarDatos(RegistroSanitario registro)
            throws SQLException {

        if (registro == null) {

            throw new IllegalArgumentException(
                    "El registro sanitario no puede estar vacío."
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

        // VALIDAR FECHA

        if (registro.getFecha() == null) {

            throw new IllegalArgumentException(
                    "La fecha es obligatoria."
            );
        }

        if (registro.getFecha().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha no puede ser futura."
            );
        }

        if (animal.getFechaNacimiento() != null
                && registro.getFecha().isBefore(
                        animal.getFechaNacimiento())) {

            throw new IllegalArgumentException(
                    "La fecha del registro sanitario no puede "
                    + "ser anterior al nacimiento del animal."
            );
        }

        // VALIDAR TIPO DE REGISTRO

        if (registro.getTipo() == null
                || registro.getTipo().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes ingresar el tipo de registro sanitario."
            );
        }

        String tipo = registro.getTipo().trim();

        if (tipo.length() > 60) {

            throw new IllegalArgumentException(
                    "El tipo de registro no puede superar "
                    + "los 60 caracteres."
            );
        }

        registro.setTipo(tipo);

        // VALIDAR DESCRIPCIÓN

        if (registro.getDescripcion() == null
                || registro.getDescripcion().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La descripción del registro sanitario "
                    + "es obligatoria."
            );
        }

        registro.setDescripcion(
                registro.getDescripcion().trim()
        );

        return animal;
    }

    // REGISTRAR EVENTO SANITARIO

    public boolean registrar(
            int idUsuario,
            RegistroSanitario registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        Animal animal = validarDatos(registro);

        if (!"ACTIVO".equals(animal.getEstado())) {

            throw new IllegalArgumentException(
                    "Solo se pueden crear registros sanitarios "
                    + "para animales activos."
            );
        }

        registro.setIdUsuario(idUsuario);

        return sanitarioDAO.registrar(registro);
    }

    // LISTAR TODOS LOS REGISTROS SANITARIOS

    public List<RegistroSanitario> listar(int idUsuario)
            throws SQLException {

        verificarUsuario(idUsuario);

        return sanitarioDAO.listar();
    }

    // CONSULTAR HISTORIAL SANITARIO POR ANIMAL

    public List<RegistroSanitario> listarPorAnimal(
            int idUsuario, int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return sanitarioDAO.listarPorAnimal(idAnimal);
    }

    // ACTUALIZAR REGISTRO SANITARIO

    public boolean actualizar(
            int idUsuario,
            RegistroSanitario registro)
            throws SQLException {

        verificarUsuario(idUsuario);

        if (registro == null
                || registro.getIdRegistro() <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro sanitario."
            );
        }

        RegistroSanitario existente =
                sanitarioDAO.buscarPorId(
                        registro.getIdRegistro()
                );

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro sanitario no existe."
            );
        }

        // CONSERVAR ANIMAL Y USUARIO ORIGINALES

        registro.setIdAnimal(
                existente.getIdAnimal()
        );

        registro.setIdUsuario(
                existente.getIdUsuario()
        );

        validarDatos(registro);

        return sanitarioDAO.actualizar(registro);
    }

    // ELIMINAR REGISTRO SANITARIO

    public boolean eliminar(
            int idUsuario, int idRegistro)
            throws SQLException {

        verificarAdministrador(idUsuario);

        if (idRegistro <= 0) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un registro sanitario."
            );
        }

        RegistroSanitario existente =
                sanitarioDAO.buscarPorId(idRegistro);

        if (existente == null) {

            throw new IllegalArgumentException(
                    "El registro sanitario no existe."
            );
        }

        return sanitarioDAO.eliminar(idRegistro);
    }

    // CONTAR REGISTROS SANITARIOS POR ANIMAL

    public int contarRegistrosPorAnimal(
            int idUsuario, int idAnimal)
            throws SQLException {

        verificarUsuario(idUsuario);

        buscarAnimal(idAnimal);

        return sanitarioDAO.contarRegistrosPorAnimal(
                idAnimal
        );
    }
}
