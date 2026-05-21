package com.sigomei.service.impl;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.service.EquipoService;

import java.util.ArrayList;
import java.util.List;

public class EquipoServiceImpl implements EquipoService {

    private final List<Orden> ordenes;

    public EquipoServiceImpl() {
        this.ordenes = new ArrayList<>();
    }

    /** Constructor para pruebas: permite inyectar ordenes existentes */
    public EquipoServiceImpl(List<Orden> ordenes) {
        this.ordenes = new ArrayList<>(ordenes);
    }

    @Override
    public void registrarEquipo(Equipo equipo) throws BusinessException {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void eliminarEquipo(int idEquipo) throws BusinessException {
        throw new UnsupportedOperationException("No implementado");
    }
}
