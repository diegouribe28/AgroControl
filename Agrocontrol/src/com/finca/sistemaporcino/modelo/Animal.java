package com.finca.sistemaporcino.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Animal {

    private int idAnimal;
    private String codigo;
    private String sexo;
    private String raza;
    private LocalDate fechaNacimiento;
    private String etapa;
    private String estado;
    private LocalDateTime fechaRegistro;
    private int idUsuarioRegistro;

    // Constructor para registrar un animal nuevo
    public Animal(String codigo, String sexo, String raza,
                  LocalDate fechaNacimiento, String etapa,
                  int idUsuarioRegistro) {

        this.codigo = codigo;
        this.sexo = sexo;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.etapa = etapa;
        this.estado = "ACTIVO";
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    // Constructor para consultar un animal existente
    public Animal(int idAnimal, String codigo, String sexo,
                  String raza, LocalDate fechaNacimiento,
                  String etapa, String estado,
                  LocalDateTime fechaRegistro,
                  int idUsuarioRegistro) {

        this.idAnimal = idAnimal;
        this.codigo = codigo;
        this.sexo = sexo;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.etapa = etapa;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSexo() {
        return sexo;
    }

    public String getRaza() {
        return raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getEtapa() {
        return etapa;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public int getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }
}
