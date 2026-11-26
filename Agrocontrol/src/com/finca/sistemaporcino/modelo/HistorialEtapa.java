
package com.finca.sistemaporcino.modelo;

import java.time.LocalDateTime;

public class HistorialEtapa {

    // ATRIBUTOS

    private int idHistorial;
    private int idAnimal;
    private int idUsuario;

    private String etapaAnterior;
    private String etapaNueva;

    private LocalDateTime fechaCambio;

    private String observaciones;

    // CONSTRUCTOR VACÍO

    public HistorialEtapa() {

    }

    // CONSTRUCTOR PARA NUEVOS REGISTROS

    public HistorialEtapa(
            int idAnimal,
            int idUsuario,
            String etapaAnterior,
            String etapaNueva,
            LocalDateTime fechaCambio,
            String observaciones) {

        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.etapaAnterior = etapaAnterior;
        this.etapaNueva = etapaNueva;
        this.fechaCambio = fechaCambio;
        this.observaciones = observaciones;
    }

    // CONSTRUCTOR COMPLETO

    public HistorialEtapa(
            int idHistorial,
            int idAnimal,
            int idUsuario,
            String etapaAnterior,
            String etapaNueva,
            LocalDateTime fechaCambio,
            String observaciones) {

        this.idHistorial = idHistorial;
        this.idAnimal = idAnimal;
        this.idUsuario = idUsuario;
        this.etapaAnterior = etapaAnterior;
        this.etapaNueva = etapaNueva;
        this.fechaCambio = fechaCambio;
        this.observaciones = observaciones;
    }

    // GETTERS Y SETTERS

    public int getIdHistorial() {

        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {

        this.idHistorial = idHistorial;
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

    public String getEtapaAnterior() {

        return etapaAnterior;
    }

    public void setEtapaAnterior(String etapaAnterior) {

        this.etapaAnterior = etapaAnterior;
    }

    public String getEtapaNueva() {

        return etapaNueva;
    }

    public void setEtapaNueva(String etapaNueva) {

        this.etapaNueva = etapaNueva;
    }

    public LocalDateTime getFechaCambio() {

        return fechaCambio;
    }

    public void setFechaCambio(
            LocalDateTime fechaCambio) {

        this.fechaCambio = fechaCambio;
    }

    public String getObservaciones() {

        return observaciones;
    }

    public void setObservaciones(String observaciones) {

        this.observaciones = observaciones;
    }
}
