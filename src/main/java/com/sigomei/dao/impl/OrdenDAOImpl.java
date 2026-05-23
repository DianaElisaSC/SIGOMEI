package com.sigomei.dao.impl;

import com.sigomei.dao.OrdenDAO;
import com.sigomei.model.Orden;

import java.sql.Connection;
import java.util.List;

public class OrdenDAOImpl implements OrdenDAO {

    private final Connection conn;

    public OrdenDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void guardar(Orden orden) {

    }

    @Override
    public void actualizar(Orden orden) {

    }

    @Override
    public void eliminar(int idOrden) {

    }

    @Override
    public Orden buscarPorId(int idOrden) {
        return null;
    }

    @Override
    public List<Orden> listar() {
        return null;
    }

    @Override
    public List<Orden> listarPorEquipo(int idEquipo) {
        return null;
    }

    @Override
    public List<Orden> listarPorTecnico(int idTecnico) {
        return null;
    }

    @Override
    public List<Orden> listarPorEstado(String estado) {
        return null;
    }

    @Override
    public boolean existeOrdenActivaParaEquipo(int idEquipo) {
        return false;
    }
}