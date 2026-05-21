package com.sigomei.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Orden implements Serializable {

    private int       idOrden;
    private String    descripcion;
    private String    estado;          // "Programada" | "En ejecucion" | "Finalizada" | "Cancelada"
    private int       idEquipo;
    private int       idTecnico;
    private LocalDate fechaProgramada;
    private LocalDate fechaInicio;
    private LocalDate fechaCierre;

    public Orden() {}

    public Orden(int idOrden, String descripcion, String estado,
                 int idEquipo, int idTecnico) {
        this.idOrden     = idOrden;
        this.descripcion = descripcion;
        this.estado      = estado;
        this.idEquipo    = idEquipo;
        this.idTecnico   = idTecnico;
    }

    public int       getIdOrden()        { return idOrden; }
    public String    getDescripcion()    { return descripcion; }
    public String    getEstado()         { return estado; }
    public int       getIdEquipo()       { return idEquipo; }
    public int       getIdTecnico()      { return idTecnico; }
    public LocalDate getFechaProgramada(){ return fechaProgramada; }
    public LocalDate getFechaInicio()    { return fechaInicio; }
    public LocalDate getFechaCierre()    { return fechaCierre; }

    public void setIdOrden(int idOrden)              { this.idOrden       = idOrden; }
    public void setDescripcion(String descripcion)   { this.descripcion   = descripcion; }
    public void setEstado(String estado)             { this.estado        = estado; }
    public void setIdEquipo(int idEquipo)            { this.idEquipo      = idEquipo; }
    public void setIdTecnico(int idTecnico)          { this.idTecnico     = idTecnico; }
    public void setFechaProgramada(LocalDate f)      { this.fechaProgramada = f; }
    public void setFechaInicio(LocalDate f)          { this.fechaInicio   = f; }
    public void setFechaCierre(LocalDate f)          { this.fechaCierre   = f; }
}
