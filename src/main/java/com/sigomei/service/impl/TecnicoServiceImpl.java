package com.sigomei.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Tecnico;
import com.sigomei.service.TecnicoService;

public class TecnicoServiceImpl implements TecnicoService {

    private final List<Tecnico> tecnicos;

    public TecnicoServiceImpl() {
        this.tecnicos = new ArrayList<>();
    }

    @Override
    public void registrarTecnico(Tecnico tecnico) throws BusinessException {
        if (tecnico == null) {
            throw new BusinessException("El tecnico no puede ser nulo.");
        }
        tecnicos.add(tecnico);
    }
}