package com.finca.sistemaporcino.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class Conexion {

    public static Connection conectar() throws SQLException {

        Properties propiedades = new Properties();

        try (FileInputStream archivo =
                new FileInputStream("config.properties")) {

            propiedades.load(archivo);

        } catch (IOException e) {
            throw new SQLException(
                    "No se pudo leer config.properties", e);
        }

        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.usuario");
        String clave = propiedades.getProperty("db.clave");

        return DriverManager.getConnection(url, usuario, clave);
    }

    public static void main(String[] args) {

        try (Connection conexion = conectar()) {

            System.out.println("Conexion exitosa con PostgreSQL.");

        } catch (SQLException e) {

            System.out.println("No se pudo conectar a PostgreSQL.");
            System.out.println(e.getMessage());
        }
    }
}