package com.sigomei.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Tecnico implements Serializable {

    private int       idTecnico;
    private String    nombre;           // nombre_completo en BD
    private String    rfc;
    private String    telefono;
    private String    correo;
    private String    especialidad;     // Electrico | Mecanico | Instrumentacion | Hidraulico
    private String    nivelCertStr;     // "I" | "II" | "III"
    private int       nivelCertificacion; // 1 | 2 | 3
    private LocalDate fechaIngreso;
    private String    estatus;          // Activo | Inactivo

    public Tecnico() {}

    // Constructor usado en pruebas
    public Tecnico(int idTecnico, String nombre,
                   String especialidad,
                   String estatus,
                   int nivelCertificacion) {

        this.idTecnico          = idTecnico;
        this.nombre             = nombre;
        this.especialidad       = especialidad;
        this.estatus            = estatus;
        this.nivelCertificacion = nivelCertificacion;
        this.nivelCertStr       =
                nivelIntToStr(nivelCertificacion);
    }

    // Constructor completo
    public Tecnico(int idTecnico, String nombre,
                   String rfc, String telefono,
                   String correo,
                   String especialidad,
                   String nivelCertStr,
                   LocalDate fechaIngreso,
                   String estatus) {

        this.idTecnico          = idTecnico;
        this.nombre             = nombre;
        this.rfc                = rfc;
        this.telefono           = telefono;
        this.correo             = correo;
        this.especialidad       = especialidad;
        this.nivelCertStr       = nivelCertStr;
        this.nivelCertificacion =
                nivelStrToInt(nivelCertStr);
        this.fechaIngreso       = fechaIngreso;
        this.estatus            = estatus;
    }

    // Conversiones
    public static int nivelStrToInt(String s) {

        if (s == null) {
            return 1;
        }

        switch (s.trim().toUpperCase()) {

            case "II":
                return 2;

            case "III":
                return 3;

            default:
                return 1;
        }
    }

    public static String nivelIntToStr(int n) {

        switch (n) {

            case 2:
                return "II";

            case 3:
                return "III";

            default:
                return "I";
        }
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getNivelCertStr() {
        return nivelCertStr;
    }

    public int getNivelCertificacion() {
        return nivelCertificacion;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setIdTecnico(int v) {
        this.idTecnico = v;
    }

    public void setNombre(String v) {
        this.nombre = v;
    }

    public void setRfc(String v) {
        this.rfc = v;
    }

    public void setTelefono(String v) {
        this.telefono = v;
    }

    public void setCorreo(String v) {
        this.correo = v;
    }

    public void setEspecialidad(String v) {
        this.especialidad = v;
    }

    public void setNivelCertStr(String v) {

        this.nivelCertStr = v;

        this.nivelCertificacion =
                nivelStrToInt(v);
    }

    public void setNivelCertificacion(int v) {

        this.nivelCertificacion = v;

        this.nivelCertStr =
                nivelIntToStr(v);
    }

    public void setFechaIngreso(LocalDate v) {
        this.fechaIngreso = v;
    }

    public void setEstatus(String v) {
        this.estatus = v;
    }

    @Override
    public String toString() {

        return "[" + idTecnico + "] "
                + nombre
                + " | "
                + especialidad
                + " | Nivel: "
                + nivelCertStr
                + " | "
                + estatus;
    }
}