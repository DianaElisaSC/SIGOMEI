package com.sigomei;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.service.impl.OrdenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RN-05: fechaProgramada <= fechaInicio <= fechaCierre.
 */
@DisplayName("RN-05 Consistencia de fechas (programada <= inicio <= cierre)")
class RN05Test {

    private OrdenServiceImpl service;
    private Orden             ordenFinalizada;

    @BeforeEach
    void setUp() {
        Tecnico tecnico = new Tecnico(1, "Carlos Gil", "Electrico", "Activo", 1);
        Equipo  equipo  = new Equipo (1, "Panel Electrico P1", "Electrico", "ABB", "Media");

        ordenFinalizada = new Orden(1, "Revision anual", "Finalizada", 1, 1);
        ordenFinalizada.setFechaProgramada(LocalDate.of(2026, 5, 1));

        service = new OrdenServiceImpl(List.of(tecnico), List.of(equipo),
                List.of(ordenFinalizada));
    }

    @Test
    @DisplayName("Positivo: fechas en orden correcto (programada <= inicio <= cierre)")
    void debeRegistrarCierreCuandoFechasSonConsistentes() {
        // GIVEN: inicio >= programada y cierre >= inicio
        LocalDate fechaInicio  = LocalDate.of(2026, 5, 10);
        LocalDate fechaCierre  = LocalDate.of(2026, 5, 20);

        // WHEN + THEN
        assertDoesNotThrow(() -> service.registrarCierre(1, fechaInicio, fechaCierre));
    }

    @Test
    @DisplayName("Negativo: fechaCierre anterior a fechaInicio")
    void debeLanzarExcepcionCuandoFechaCierreEsAnteriorAFechaInicio() {
        // GIVEN: fechaCierre < fechaInicio
        LocalDate fechaInicio = LocalDate.of(2026, 5, 20);
        LocalDate fechaCierre = LocalDate.of(2026, 5, 10); // INVERTIDAS

        // WHEN + THEN
        assertThrows(BusinessException.class,
                () -> service.registrarCierre(1, fechaInicio, fechaCierre));
    }

    @Test
    @DisplayName("Negativo: fechaInicio anterior a fechaProgramada")
    void debeLanzarExcepcionCuandoFechaInicioEsAnteriorAFechaProgramada() {
        // GIVEN: fechaInicio < fechaProgramada (2026-05-01)
        LocalDate fechaInicio = LocalDate.of(2026, 4, 28);
        LocalDate fechaCierre = LocalDate.of(2026, 5, 15);

        // WHEN + THEN
        assertThrows(BusinessException.class,
                () -> service.registrarCierre(1, fechaInicio, fechaCierre));
    }
}
