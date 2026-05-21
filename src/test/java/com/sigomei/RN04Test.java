package com.sigomei;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Orden;
import com.sigomei.service.impl.EquipoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RN-04: Un equipo no puede eliminarse si tiene ordenes de mantenimiento registradas.
 */
@DisplayName("RN-04 Proteccion de eliminacion con ordenes registradas")
class RN04Test {

    @Test
    @DisplayName("Positivo: equipo sin ordenes puede ser eliminado")
    void debeEliminarEquipoCuandoNoTieneOrdenes() {
        // GIVEN: equipo id=1 sin ninguna orden asociada
        EquipoServiceImpl service = new EquipoServiceImpl(List.of());

        // WHEN + THEN
        assertDoesNotThrow(() -> service.eliminarEquipo(1));
    }

    @Test
    @DisplayName("Negativo: equipo con ordenes registradas no puede eliminarse")
    void debeLanzarExcepcionAlEliminarEquipoConOrdenes() {
        // GIVEN: equipo id=1 tiene una orden asociada
        Orden orden = new Orden(10, "Mantenimiento pendiente", "Programada", 1, 1);
        EquipoServiceImpl service = new EquipoServiceImpl(List.of(orden));

        // WHEN + THEN
        assertThrows(BusinessException.class, () -> service.eliminarEquipo(1));
    }
}
