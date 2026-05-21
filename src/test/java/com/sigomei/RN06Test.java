package com.sigomei;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.service.impl.OrdenServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RN-06: El registro de cierre solo esta permitido en ordenes con estado "Finalizada".
 */
@DisplayName("RN-06 Registro de cierre solo en ordenes Finalizadas")
class RN06Test {

    private static final LocalDate INICIO = LocalDate.of(2026, 5, 10);
    private static final LocalDate CIERRE = LocalDate.of(2026, 5, 20);

    private OrdenServiceImpl serviceConEstado(String estado) {
        Tecnico tecnico = new Tecnico(1, "Rosa Diaz", "Instrumentacion", "Activo", 2);
        Equipo  equipo  = new Equipo (1, "Sensor S1", "Instrumentacion", "Endress", "Alta");

        Orden orden = new Orden(1, "Calibracion anual", estado, 1, 1);
        orden.setFechaProgramada(LocalDate.of(2026, 5, 1));

        return new OrdenServiceImpl(List.of(tecnico), List.of(equipo), List.of(orden));
    }

    @Test
    @DisplayName("Positivo: orden en estado Finalizada permite registrar cierre")
    void debeRegistrarCierreCuandoOrdenEstaFinalizada() {
        OrdenServiceImpl service = serviceConEstado("Finalizada");

        assertDoesNotThrow(() -> service.registrarCierre(1, INICIO, CIERRE));
    }

    @Test
    @DisplayName("Negativo: orden en estado En ejecucion no permite registrar cierre")
    void debeLanzarExcepcionCuandoOrdenEstaEnEjecucion() {
        OrdenServiceImpl service = serviceConEstado("En ejecucion");

        assertThrows(BusinessException.class,
                () -> service.registrarCierre(1, INICIO, CIERRE));
    }

    @Test
    @DisplayName("Negativo: orden en estado Programada no permite registrar cierre")
    void debeLanzarExcepcionCuandoOrdenEstaProgramada() {
        OrdenServiceImpl service = serviceConEstado("Programada");

        assertThrows(BusinessException.class,
                () -> service.registrarCierre(1, INICIO, CIERRE));
    }
}
