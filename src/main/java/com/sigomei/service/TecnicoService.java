package com.sigomei.service;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Tecnico;

public interface TecnicoService {

    void registrarTecnico(Tecnico tecnico) throws BusinessException;
}
