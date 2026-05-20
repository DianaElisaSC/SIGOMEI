package com.sigomei.service;

import com.sigomei.model.Orden;

public interface OrdenService {

    void registrarOrden(Orden orden);

    void actualizarEstado(int idOrden,
                          String estado);

}