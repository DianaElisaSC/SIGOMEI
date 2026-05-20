package com.sigomei.dao;

import java.util.List;

import com.sigomei.model.Orden;

public interface OrdenDAO {

    void guardar(Orden orden);

    void actualizar(Orden orden);

    void eliminar(int id);

    Orden buscarPorId(int id);

    List<Orden> listar();
}