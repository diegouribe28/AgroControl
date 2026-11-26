
package com.finca.sistemaporcino.modelo;

import java.time.LocalDate;

public class Reproduccion {

    // ATRIBUTOS

    private int idReproduccion;
    private int idCerda;
    private int idUsuario;

    private LocalDate fechaServicio;
    private LocalDate fechaProbableParto;
    private LocalDate fechaPartoReal;

    private String resultado;
    private String observaciones;

    // CONSTRUCTOR VACÍO

    public Reproduccion() {

        this.resultado = "PENDIENTE";
    }

    // CONSTRUCTOR PARA NUEVOS REGISTROS

    public Reproduccion(
            int idCerda,
            int idUsuario,
            LocalDate fechaServicio,
            LocalDate fechaProbableParto,
            LocalDate fechaPartoReal,
            String resultado,
            String observaciones) {

        this.idCerda = idCerda;
        this.idUsuario = idUsuario;
        this.fechaServicio = fechaServicio;
        this.fechaProbableParto = fechaProbableParto;
        this.fechaPartoReal = fechaPartoReal;
        this.resultado = resultado;
        this.observaciones = observaciones;
    }

    // CONSTRUCTOR COMPLETO

    public Reproduccion(
            int idReproduccion,
            int idCerda,
            int idUsuario,
            LocalDate fechaServicio,
            LocalDate fechaProbableParto,
            LocalDate fechaPartoReal,
            String resultado,
            String observaciones) {

        this.idReproduccion = idReproduccion;
        this.idCerda = idCerda;
        this.idUsuario = idUsuario;
        this.fechaServicio = fechaServicio;
        this.fechaProbableParto = fechaProbableParto;
        this.fechaPartoReal = fechaPartoReal;
        this.resultado = resultado;
        this.observaciones = observaciones;
    }

    // GETTERS Y SETTERS

    public int getIdReproduccion() {

        return idReproduccion;
    }

    public void setIdReproduccion(int idReproduccion) {

        this.idReproduccion = idReproduccion;
    }

    public int getIdCerda() {

        return idCerda;
    }

    public void setIdCerda(int idCerda) {

        this.idCerda = idCerda;
    }

    public int getIdUsuario() {

        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {

        this.idUsuario = idUsuario;
    }

    public LocalDate getFechaServicio() {

        return fechaServicio;
    }

    public void setFechaServicio(LocalDate fechaServicio) {

        this.fechaServicio = fechaServicio;
    }

    public LocalDate getFechaProbableParto() {

        return fechaProbableParto;
    }

    public void setFechaProbableParto(
            LocalDate fechaProbableParto) {

        this.fechaProbableParto = fechaProbableParto;
    }

    public LocalDate getFechaPartoReal() {

        return fechaPartoReal;
    }

    public void setFechaPartoReal(
            LocalDate fechaPartoReal) {

        this.fechaPartoReal = fechaPartoReal;
    }

    public String getResultado() {

        return resultado;
    }

    public void setResultado(String resultado) {

        this.resultado = resultado;
    }

    public String getObservaciones() {

        return observaciones;
    }

    public void setObservaciones(String observaciones) {

        this.observaciones = observaciones;
    }
}
