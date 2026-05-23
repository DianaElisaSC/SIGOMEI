package com.sigomei.dao;

import java.util.List;

import com.sigomei.model.Orden;

public interface OrdenDAO {

    void guardar(Orden orden);

    void actualizar(Orden orden);

    void eliminar(int idOrden);

    Orden buscarPorId(int idOrden);

    List<Orden> listar();

    List<Orden> listarPorEquipo(int idEquipo);

    List<Orden> listarPorTecnico(int idTecnico);

    List<Orden> listarPorEstado(String estado);

    boolean existeOrdenActivaParaEquipo(int idEquipo);
}