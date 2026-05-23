package com.sigomei.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Equipo implements Serializable {

    private int       idEquipo;
    private String    nombre;
    private String    tipo;            // Electrico | Mecanico | Instrumentacion | Hidraulico
    private String    marca;
    private String    modelo;
    private String    numeroSerie;
    private String    ubicacionPlanta;
    private LocalDate fechaInstalacion;
    private String    estadoOperativo; // Activo | Mantenimiento | Fuera de operacion
    private String    criticidad;      // Baja | Media | Alta

    public Equipo() {}

    // Constructor usado en pruebas (compatible con tests existentes)
    public Equipo(int idEquipo, String nombre, String tipo,
                  String marca, String criticidad) {
        this.idEquipo   = idEquipo;
        this.nombre     = nombre;
        this.tipo       = tipo;
        this.marca      = marca;
        this.criticidad = criticidad;
    }

    // Constructor completo para CRUD real
    public Equipo(int idEquipo, String nombre, String tipo, String marca,
                  String modelo, String numeroSerie, String ubicacionPlanta,
                  LocalDate fechaInstalacion, String estadoOperativo, String criticidad) {

        this.idEquipo         = idEquipo;
        this.nombre           = nombre;
        this.tipo             = tipo;
        this.marca            = marca;
        this.modelo           = modelo;
        this.numeroSerie      = numeroSerie;
        this.ubicacionPlanta  = ubicacionPlanta;
        this.fechaInstalacion = fechaInstalacion;
        this.estadoOperativo  = estadoOperativo;
        this.criticidad       = criticidad;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public String getUbicacionPlanta() {
        return ubicacionPlanta;
    }

    public LocalDate getFechaInstalacion() {
        return fechaInstalacion;
    }

    public String getEstadoOperativo() {
        return estadoOperativo;
    }

    public String getCriticidad() {
        return criticidad;
    }

    public void setIdEquipo(int v) {
        this.idEquipo = v;
    }

    public void setNombre(String v) {
        this.nombre = v;
    }

    public void setTipo(String v) {
        this.tipo = v;
    }

    public void setMarca(String v) {
        this.marca = v;
    }

    public void setModelo(String v) {
        this.modelo = v;
    }

    public void setNumeroSerie(String v) {
        this.numeroSerie = v;
    }

    public void setUbicacionPlanta(String v) {
        this.ubicacionPlanta = v;
    }

    public void setFechaInstalacion(LocalDate v) {
        this.fechaInstalacion = v;
    }

    public void setEstadoOperativo(String v) {
        this.estadoOperativo = v;
    }

    public void setCriticidad(String v) {
        this.criticidad = v;
    }

    @Override
    public String toString() {

        return "[" + idEquipo + "] "
                + nombre
                + " | "
                + tipo
                + " | "
                + marca
                + " | Criticidad: "
                + criticidad
                + " | Estado: "
                + estadoOperativo;
    }
}