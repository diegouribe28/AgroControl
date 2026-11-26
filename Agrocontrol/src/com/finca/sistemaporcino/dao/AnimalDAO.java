
package com.finca.sistemaporcino.dao;

import com.finca.sistemaporcino.conexion.Conexion;
import com.finca.sistemaporcino.modelo.Animal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    // Guarda un animal nuevo en PostgreSQL.
    public boolean registrar(Animal animal) {

        String sql = "INSERT INTO animal "
                + "(codigo, sexo, raza, fecha_nacimiento, etapa, "
                + "estado, id_usuario_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, animal.getCodigo());
            consulta.setString(2, animal.getSexo());
            consulta.setString(3, animal.getRaza());

            if (animal.getFechaNacimiento() != null) {
                consulta.setDate(
                        4,
                        Date.valueOf(animal.getFechaNacimiento())
                );
            } else {
                consulta.setNull(4, java.sql.Types.DATE);
            }

            consulta.setString(5, animal.getEtapa());
            consulta.setString(6, animal.getEstado());
            consulta.setInt(7, animal.getIdUsuarioRegistro());

            int filas = consulta.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error al registrar el animal: " + e.getMessage()
            );
            return false;
            
        }
        
    }

    // Consulta todos los animales registrados.
    public List<Animal> listar() {

        List<Animal> animales = new ArrayList<>();

        String sql = "SELECT id_animal, codigo, sexo, raza, "
                + "fecha_nacimiento, etapa, estado, "
                + "fecha_registro, id_usuario_registro "
                + "FROM animal ORDER BY id_animal";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                Date fechaSQL = resultado.getDate("fecha_nacimiento");
                java.time.LocalDate fechaNacimiento = null;

                if (fechaSQL != null) {
                    fechaNacimiento = fechaSQL.toLocalDate();
                }

                Timestamp registroSQL =
                        resultado.getTimestamp("fecha_registro");

                java.time.LocalDateTime fechaRegistro = null;

                if (registroSQL != null) {
                    fechaRegistro = registroSQL.toLocalDateTime();
                }

                Animal animal = new Animal(
                        resultado.getInt("id_animal"),
                        resultado.getString("codigo"),
                        resultado.getString("sexo"),
                        resultado.getString("raza"),
                        fechaNacimiento,
                        resultado.getString("etapa"),
                        resultado.getString("estado"),
                        fechaRegistro,
                        resultado.getInt("id_usuario_registro")
                );

                animales.add(animal);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al consultar los animales: " + e.getMessage()
            );
        }

        return animales;
    }
    
    // Actualiza los datos de un animal existente en PostgreSQL.
    public boolean actualizar(Animal animal) {

        String sql = "UPDATE animal SET "
                + "codigo = ?, "
                + "sexo = ?, "
                + "raza = ?, "
                + "fecha_nacimiento = ?, "
                + "etapa = ? "
                + "WHERE id_animal = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, animal.getCodigo());
            consulta.setString(2, animal.getSexo());
            consulta.setString(3, animal.getRaza());
            consulta.setDate(
                    4,
                    Date.valueOf(animal.getFechaNacimiento())
            );
            consulta.setString(5, animal.getEtapa());
            consulta.setInt(6, animal.getIdAnimal());

            int filas = consulta.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error al actualizar el animal: " + e.getMessage()
            );
            return false;
        }
    }

    // Desactiva un animal sin eliminarlo de PostgreSQL.
    
public boolean cambiarEstado(int idAnimal) {

    String sql = "UPDATE animal SET estado = "
            + "CASE "
            + "WHEN estado = 'ACTIVO' THEN 'INACTIVO' "
            + "WHEN estado = 'INACTIVO' THEN 'ACTIVO' "
            + "END "
            + "WHERE id_animal = ? "
            + "AND estado IN ('ACTIVO', 'INACTIVO')";

    try (Connection conexion = Conexion.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        consulta.setInt(1, idAnimal);

        int filas = consulta.executeUpdate();

        return filas > 0;

    } catch (SQLException e) {

        System.out.println(
                "Error al cambiar el estado del animal: "
                        + e.getMessage()
        );

        return false;
    }
}


    // Elimina físicamente un animal de PostgreSQL.
    public boolean eliminar(int idAnimal) {

        String sql = "DELETE FROM animal WHERE id_animal = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, idAnimal);

            int filas = consulta.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar el animal: " + e.getMessage()
            );

            return false;
        }
    }

}
