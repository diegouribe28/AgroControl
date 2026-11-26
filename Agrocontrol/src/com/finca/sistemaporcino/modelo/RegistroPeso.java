
package com.finca.sistemaporcino.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroPeso {

    // ATRIBUTOS

    private int idRegistro;
    private int idAnimal;
    private int idUsuario;
    private LocalDate fecha;
    private BigDecimal peso;
    private String observaciones;

    // CONSTRUCTOR VACÍO

    public RegistroPeso() {
    }

    // CONSTRUCTOR PARA REGISTRAR UN NUEVO PESAJE

    public RegistroPeso(int idAnimal,
                        int idUsuario,
                        LocalDate fecha,
                        BigDecimal peso,
                        String observaciones) {

        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.peso = peso;
        this.observaciones = observaciones;
    }

    // CONSTRUCTOR COMPLETO

    public RegistroPeso(int idRegistro,
                        int idAnimal,
                        int idUsuario,
                        LocalDate fecha,
                        BigDecimal peso,
                        String observaciones) {

        this.idRegistro = idRegistro;
        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.peso = peso;
        this.observaciones = observaciones;
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

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
