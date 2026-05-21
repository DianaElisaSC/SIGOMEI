package com.sigomei.service;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;

public interface EquipoService {

    void registrarEquipo(Equipo equipo) throws BusinessException;

    /** RN-04: lanza BusinessException si el equipo tiene ordenes registradas */
    void eliminarEquipo(int idEquipo) throws BusinessException;
}
