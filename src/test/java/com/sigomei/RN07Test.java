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
 * RN-07: Para equipos con criticidad Alta se requiere tecnico con
 *         nivel de certificacion II (2) o III (3).
 */
@DisplayName("RN-07 Certificacion requerida para equipos de criticidad Alta")
class RN07Test {

    private Equipo equipoAlta;
    private Equipo equipoBaja;

    @BeforeEach
    void setUp() {
        equipoAlta = new Equipo(1, "Turbina T1",    "Mecanico", "GE",    "Alta");
        equipoBaja = new Equipo(2, "Ventilador V1", "Mecanico", "Delta", "Baja");
    }

    @Test
    @DisplayName("Positivo: tecnico nivel II asignado a equipo criticidad Alta")
    void debePermitirOrdenConTecnicoNivelDosParaEquipoAlta() {
        // GIVEN
        Tecnico nivelII = new Tecnico(1, "Elena Vega", "Mecanico", "Activo", 2);
        Orden orden = new Orden(0, "Revision turbina", "Programada",
                equipoAlta.getIdEquipo(), nivelII.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(nivelII), List.of(equipoAlta), List.of());

        // WHEN + THEN
        assertDoesNotThrow(() -> service.registrarOrden(orden));
    }

    @Test
    @DisplayName("Positivo: tecnico nivel I asignado a equipo criticidad Baja")
    void debePermitirOrdenConTecnicoNivelUnoParaEquipoBaja() {
        // GIVEN
        Tecnico nivelI = new Tecnico(2, "Omar Cruz", "Mecanico", "Activo", 1);
        Orden orden = new Orden(0, "Limpieza ventilador", "Programada",
                equipoBaja.getIdEquipo(), nivelI.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(nivelI), List.of(equipoBaja), List.of());

        // WHEN + THEN
        assertDoesNotThrow(() -> service.registrarOrden(orden));
    }

    @Test
    @DisplayName("Negativo: tecnico nivel I no puede atender equipo criticidad Alta")
    void debeLanzarExcepcionConTecnicoNivelUnoParaEquipoAlta() {
        // GIVEN
        Tecnico nivelI = new Tecnico(3, "Omar Cruz", "Mecanico", "Activo", 1);
        Orden orden = new Orden(0, "Revision turbina", "Programada",
                equipoAlta.getIdEquipo(), nivelI.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(nivelI), List.of(equipoAlta), List.of());

        // WHEN + THEN
        assertThrows(BusinessException.class, () -> service.registrarOrden(orden));
    }
}
