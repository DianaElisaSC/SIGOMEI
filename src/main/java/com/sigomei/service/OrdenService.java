package com.sigomei.service;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Orden;

import java.time.LocalDate;

public interface OrdenService {

    /** RN-01, RN-02, RN-03, RN-07 */
    void registrarOrden(Orden orden) throws BusinessException;

    /** RN-08 */
    void actualizarEstado(int idOrden, String nuevoEstado) throws BusinessException;

    /** RN-05, RN-06 */
    void registrarCierre(int idOrden, LocalDate fechaInicio, LocalDate fechaCierre)
            throws BusinessException;
}
