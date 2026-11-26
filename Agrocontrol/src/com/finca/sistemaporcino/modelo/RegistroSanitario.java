
package com.finca.sistemaporcino.modelo;

import java.time.LocalDate;

public class RegistroSanitario {

    // ATRIBUTOS

    private int idRegistro;
    private int idAnimal;
    private int idUsuario;
    private LocalDate fecha;
    private String tipo;
    private String descripcion;

    // CONSTRUCTOR VACÍO

    public RegistroSanitario() {
    }

    // CONSTRUCTOR PARA NUEVOS REGISTROS

    public RegistroSanitario(
            int idAnimal,
            int idUsuario,
            LocalDate fecha,
            String tipo,
            String descripcion) {

        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // CONSTRUCTOR COMPLETO

    public RegistroSanitario(
            int idRegistro,
            int idAnimal,
            int idUsuario,
            LocalDate fecha,
            String tipo,
            String descripcion) {

        this.idRegistro = idRegistro;
        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // GETTERS Y SETTERS

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
