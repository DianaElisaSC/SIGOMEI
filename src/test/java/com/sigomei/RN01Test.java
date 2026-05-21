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
 * RN-01: La especialidad del tecnico debe coincidir con el tipo del equipo.
 */
@DisplayName("RN-01 Compatibilidad de especialidad tecnico-equipo")
class RN01Test {

    private Tecnico tecnicoElectrico;
    private Tecnico tecnicoMecanico;
    private Equipo  equipoElectrico;
    private Equipo  equipoHidraulico;

    @BeforeEach
    void setUp() {
        tecnicoElectrico = new Tecnico(1, "Ana Torres",    "Electrico",  "Activo", 1);
        tecnicoMecanico  = new Tecnico(2, "Luis Mendoza",  "Mecanico",   "Activo", 1);
        equipoElectrico  = new Equipo (1, "Motor Electrico A", "Electrico",  "Siemens", "Baja");
        equipoHidraulico = new Equipo (2, "Bomba Hidraulica B","Hidraulico", "Bosch",   "Media");
    }

    @Test
    @DisplayName("Positivo: especialidad del tecnico coincide con tipo del equipo")
    void debePermitirOrdenCuandoEspecialidadEsCompatible() {
        // GIVEN
        Orden orden = new Orden(0, "Mantenimiento preventivo", "Programada",
                equipoElectrico.getIdEquipo(), tecnicoElectrico.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(tecnicoElectrico), List.of(equipoElectrico), List.of());

        // WHEN + THEN — debe registrar sin excepcion
        assertDoesNotThrow(() -> service.registrarOrden(orden));
    }

    @Test
    @DisplayName("Negativo: especialidad del tecnico NO coincide con tipo del equipo")
    void debeLanzarExcepcionCuandoEspecialidadEsIncompatible() {
        // GIVEN: tecnico Electrico intenta atender equipo Hidraulico
        Orden orden = new Orden(0, "Mantenimiento correctivo", "Programada",
                equipoHidraulico.getIdEquipo(), tecnicoElectrico.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(tecnicoElectrico), List.of(equipoHidraulico), List.of());

        // WHEN + THEN — debe lanzar BusinessException
        assertThrows(BusinessException.class, () -> service.registrarOrden(orden));
    }
}
