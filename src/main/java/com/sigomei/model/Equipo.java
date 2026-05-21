package com.sigomei.model;

import java.io.Serializable;

public class Equipo implements Serializable {

    private int    idEquipo;
    private String nombre;
    private String tipo;        // "Electrico" | "Mecanico" | "Instrumentacion" | "Hidraulico"
    private String marca;
    private String criticidad;  // "Baja" | "Media" | "Alta"

    public Equipo() {}

    public Equipo(int idEquipo, String nombre, String tipo,
                  String marca, String criticidad) {
        this.idEquipo   = idEquipo;
        this.nombre     = nombre;
        this.tipo       = tipo;
        this.marca      = marca;
        this.criticidad = criticidad;
    }

    public int    getIdEquipo()   { return idEquipo; }
    public String getNombre()     { return nombre; }
    public String getTipo()       { return tipo; }
    public String getMarca()      { return marca; }
    public String getCriticidad() { return criticidad; }

    public void setIdEquipo(int idEquipo)        { this.idEquipo   = idEquipo; }
    public void setNombre(String nombre)         { this.nombre     = nombre; }
    public void setTipo(String tipo)             { this.tipo       = tipo; }
    public void setMarca(String marca)           { this.marca      = marca; }
    public void setCriticidad(String criticidad) { this.criticidad = criticidad; }
}
