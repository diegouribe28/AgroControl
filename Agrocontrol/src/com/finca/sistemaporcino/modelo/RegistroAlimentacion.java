
package com.finca.sistemaporcino.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegistroAlimentacion {

    // ATRIBUTOS

    private int idRegistro;
    private int idAnimal;
    private int idUsuario;
    private LocalDateTime fecha;
    private String tipoAlimento;
    private BigDecimal cantidadKg;
    private String observaciones;

    // CONSTRUCTOR VACÍO

    public RegistroAlimentacion() {
    }

    // CONSTRUCTOR PARA REGISTRAR ALIMENTACIÓN

    public RegistroAlimentacion(
            int idAnimal,
            int idUsuario,
            LocalDateTime fecha,
            String tipoAlimento,
            BigDecimal cantidadKg,
            String observaciones) {

        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.tipoAlimento = tipoAlimento;
        this.cantidadKg = cantidadKg;
        this.observaciones = observaciones;
    }

    // CONSTRUCTOR COMPLETO

    public RegistroAlimentacion(
            int idRegistro,
            int idAnimal,
            int idUsuario,
            LocalDateTime fecha,
            String tipoAlimento,
            BigDecimal cantidadKg,
            String observaciones) {

        this.idRegistro = idRegistro;
        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.tipoAlimento = tipoAlimento;
        this.cantidadKg = cantidadKg;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(String tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public BigDecimal getCantidadKg() {
        return cantidadKg;
    }

    public void setCantidadKg(BigDecimal cantidadKg) {
        this.cantidadKg = cantidadKg;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
