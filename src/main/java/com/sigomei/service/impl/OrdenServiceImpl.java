package com.sigomei.service.impl;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.service.OrdenService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenServiceImpl implements OrdenService {

    private final List<Tecnico> tecnicos;
    private final List<Equipo>  equipos;
    private final List<Orden>   ordenes;

    public OrdenServiceImpl() {
        this.tecnicos = new ArrayList<>();
        this.equipos  = new ArrayList<>();
        this.ordenes  = new ArrayList<>();
    }

    /** Constructor para pruebas: inyecta contexto sin necesidad de DAOs */
    public OrdenServiceImpl(List<Tecnico> tecnicos, List<Equipo> equipos,
                            List<Orden> ordenes) {
        this.tecnicos = new ArrayList<>(tecnicos);
        this.equipos  = new ArrayList<>(equipos);
        this.ordenes  = new ArrayList<>(ordenes);
    }

    @Override
    public void registrarOrden(Orden orden) throws BusinessException {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void actualizarEstado(int idOrden, String nuevoEstado)
            throws BusinessException {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void registrarCierre(int idOrden, LocalDate fechaInicio,
                                LocalDate fechaCierre) throws BusinessException {
        throw new UnsupportedOperationException("No implementado");
    }
}
