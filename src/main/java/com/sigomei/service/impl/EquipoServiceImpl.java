package com.sigomei.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.service.EquipoService;

public class EquipoServiceImpl implements EquipoService {

    private final List<Orden>  ordenes;
    private final List<Equipo> equipos;

    public EquipoServiceImpl() {
        this.ordenes  = new ArrayList<>();
        this.equipos  = new ArrayList<>();
    }

    public EquipoServiceImpl(List<Orden> ordenes) {
        this.ordenes  = new ArrayList<>(ordenes);
        this.equipos  = new ArrayList<>();
    }

    @Override
    public void registrarEquipo(Equipo equipo) throws BusinessException {
        if (equipo == null) {
            throw new BusinessException("El equipo no puede ser nulo.");
        }
        equipos.add(equipo);
    }

    @Override
    public void eliminarEquipo(int idEquipo) throws BusinessException {
        // RN-04: No eliminar si tiene ordenes registradas
        boolean tieneOrdenes = ordenes.stream()
                .anyMatch(o -> o.getIdEquipo() == idEquipo);
        if (tieneOrdenes) {
            throw new BusinessException("No se puede eliminar equipo con ordenes registradas.");
        }
    }
}