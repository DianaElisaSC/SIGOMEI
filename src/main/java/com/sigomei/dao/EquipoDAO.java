package com.sigomei.dao;

import java.util.List;

import com.sigomei.model.Equipo;

public interface EquipoDAO {

    void guardar(Equipo equipo);

    void actualizar(Equipo equipo);

    void eliminar(int idEquipo);

    Equipo buscarPorId(int idEquipo);

    List<Equipo> listar();

    List<Equipo> buscarPorNombreOSerie(String termino);

    boolean tieneOrdenes(int idEquipo);
}