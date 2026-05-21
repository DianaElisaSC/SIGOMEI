package com.sigomei;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.service.impl.OrdenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RN-02: No puede existir mas de una orden activa para el mismo equipo
 *         en la misma fecha programada.
 */
@DisplayName("RN-02 Unicidad de ordenes activas por equipo y fecha")
class RN02Test {

    private Tecnico tecnico;
    private Equipo  equipo;

    @BeforeEach
    void setUp() {
        tecnico = new Tecnico(1, "Pedro Saenz", "Mecanico", "Activo", 1);
        equipo  = new Equipo (1, "Compresor C1", "Mecanico", "Atlas", "Media");
    }

    @Test
    @DisplayName("Positivo: equipo sin orden activa, debe registrar la orden")
    void debePermitirOrdenCuandoEquipoNoTieneOrdenActiva() {
        // GIVEN: no hay ordenes activas para el equipo
        Orden nueva = new Orden(0, "Primera revision", "Programada",
                equipo.getIdEquipo(), tecnico.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(tecnico), List.of(equipo), List.of());

        // WHEN + THEN
        assertDoesNotThrow(() -> service.registrarOrden(nueva));
    }

    @Test
    @DisplayName("Negativo: ya existe una orden activa para el mismo equipo")
    void debeLanzarExcepcionCuandoEquipoYaTieneOrdenActiva() {
        // GIVEN: ya existe una orden activa para el mismo equipo
        Orden ordenExistente = new Orden(1, "Orden previa", "Programada",
                equipo.getIdEquipo(), tecnico.getIdTecnico());
        Orden ordenDuplicada = new Orden(0, "Orden duplicada", "Programada",
                equipo.getIdEquipo(), tecnico.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(tecnico), List.of(equipo), List.of(ordenExistente));

        // WHEN + THEN
        assertThrows(BusinessException.class, () -> service.registrarOrden(ordenDuplicada));
    }
}
