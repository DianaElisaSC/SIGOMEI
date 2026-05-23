package com.sigomei.dao;

import java.util.List;

import com.sigomei.model.Tecnico;

public interface TecnicoDAO {

    void guardar(Tecnico tecnico);

    void actualizar(Tecnico tecnico);

    void eliminar(int idTecnico);

    Tecnico buscarPorId(int idTecnico);

    List<Tecnico> listar();

    boolean tieneOrdenes(int idTecnico);
}