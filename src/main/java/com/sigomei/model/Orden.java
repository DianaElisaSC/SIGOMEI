package com.sigomei.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Orden implements Serializable {

    private int           idOrden;
    private int           idEquipo;
    private int           idTecnico;

    private String        tipoMantenimiento; // Preventivo | Correctivo

    private LocalDateTime fechaCreacion;
    private LocalDate     fechaProgramada;
    private LocalDate     fechaInicio;
    private LocalDate     fechaCierre;

    private String        descripcion;

    private BigDecimal    costoEstimado;
    private BigDecimal    costoReal;

    private String        estado; // Programada | En ejecucion | Finalizada | Cancelada

    public Orden() {
    }

    // Constructor usado en pruebas
    public Orden(int idOrden,
                 String descripcion,
                 String estado,
                 int idEquipo,
                 int idTecnico) {

        this.idOrden     = idOrden;
        this.descripcion = descripcion;
        this.estado      = estado;
        this.idEquipo    = idEquipo;
        this.idTecnico   = idTecnico;
    }

    // Constructor completo
    public Orden(int idOrden,
                 int idEquipo,
                 int idTecnico,
                 String tipoMantenimiento,
                 LocalDate fechaProgramada,
                 String descripcion,
                 BigDecimal costoEstimado,
                 String estado) {

        this.idOrden           = idOrden;
        this.idEquipo          = idEquipo;
        this.idTecnico         = idTecnico;
        this.tipoMantenimiento = tipoMantenimiento;
        this.fechaProgramada   = fechaProgramada;
        this.descripcion       = descripcion;
        this.costoEstimado     = costoEstimado;
        this.estado            = estado;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getCostoEstimado() {
        return costoEstimado;
    }

    public BigDecimal getCostoReal() {
        return costoReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdOrden(int v) {
        this.idOrden = v;
    }

    public void setIdEquipo(int v) {
        this.idEquipo = v;
    }

    public void setIdTecnico(int v) {
        this.idTecnico = v;
    }

    public void setTipoMantenimiento(String v) {
        this.tipoMantenimiento = v;
    }

    public void setFechaCreacion(LocalDateTime v) {
        this.fechaCreacion = v;
    }

    public void setFechaProgramada(LocalDate v) {
        this.fechaProgramada = v;
    }

    public void setFechaInicio(LocalDate v) {
        this.fechaInicio = v;
    }

    public void setFechaCierre(LocalDate v) {
        this.fechaCierre = v;
    }

    public void setDescripcion(String v) {
        this.descripcion = v;
    }

    public void setCostoEstimado(BigDecimal v) {
        this.costoEstimado = v;
    }

    public void setCostoReal(BigDecimal v) {
        this.costoReal = v;
    }

    public void setEstado(String v) {
        this.estado = v;
    }

    @Override
    public String toString() {

        return "[" + idOrden + "] "
                + "Equipo:" + idEquipo
                + " | Tecnico:" + idTecnico
                + " | " + tipoMantenimiento
                + " | " + estado
                + " | Prog: " + fechaProgramada;
    }
}