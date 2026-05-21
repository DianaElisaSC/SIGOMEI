package com.sigomei.model;

import java.io.Serializable;

public class Tecnico implements Serializable {

    private int    idTecnico;
    private String nombre;
    private String especialidad;       // "Electrico" | "Mecanico" | "Instrumentacion" | "Hidraulico"
    private String estatus;            // "Activo" | "Inactivo"
    private int    nivelCertificacion; // 1 | 2 | 3

    public Tecnico() {}

    public Tecnico(int idTecnico, String nombre, String especialidad,
                   String estatus, int nivelCertificacion) {
        this.idTecnico          = idTecnico;
        this.nombre             = nombre;
        this.especialidad       = especialidad;
        this.estatus            = estatus;
        this.nivelCertificacion = nivelCertificacion;
    }

    public int    getIdTecnico()          { return idTecnico; }
    public String getNombre()             { return nombre; }
    public String getEspecialidad()       { return especialidad; }
    public String getEstatus()            { return estatus; }
    public int    getNivelCertificacion() { return nivelCertificacion; }

    public void setIdTecnico(int idTecnico)                  { this.idTecnico          = idTecnico; }
    public void setNombre(String nombre)                     { this.nombre             = nombre; }
    public void setEspecialidad(String especialidad)         { this.especialidad       = especialidad; }
    public void setEstatus(String estatus)                   { this.estatus            = estatus; }
    public void setNivelCertificacion(int nivelCertificacion){ this.nivelCertificacion = nivelCertificacion; }
}
