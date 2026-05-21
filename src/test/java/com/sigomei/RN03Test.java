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
 * RN-03: No se puede asignar una orden a un tecnico con estatus Inactivo.
 */
@DisplayName("RN-03 Restriccion de tecnicos inactivos")
class RN03Test {

    private Equipo equipoMecanico;

    @BeforeEach
    void setUp() {
        equipoMecanico = new Equipo(1, "Torno CNC T1", "Mecanico", "Mazak", "Baja");
    }

    @Test
    @DisplayName("Positivo: tecnico Activo puede ser asignado a una orden")
    void debePermitirOrdenConTecnicoActivo() {
        // GIVEN
        Tecnico activo = new Tecnico(1, "Maria Lopez", "Mecanico", "Activo", 1);
        Orden orden = new Orden(0, "Ajuste de precision", "Programada",
                equipoMecanico.getIdEquipo(), activo.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(activo), List.of(equipoMecanico), List.of());

        // WHEN + THEN
        assertDoesNotThrow(() -> service.registrarOrden(orden));
    }

    @Test
    @DisplayName("Negativo: tecnico Inactivo no puede ser asignado a una orden")
    void debeLanzarExcepcionCuandoTecnicoEstaInactivo() {
        // GIVEN
        Tecnico inactivo = new Tecnico(2, "Jose Reyes", "Mecanico", "Inactivo", 1);
        Orden orden = new Orden(0, "Ajuste de precision", "Programada",
                equipoMecanico.getIdEquipo(), inactivo.getIdTecnico());

        OrdenServiceImpl service = new OrdenServiceImpl(
                List.of(inactivo), List.of(equipoMecanico), List.of());

        // WHEN + THEN
        assertThrows(BusinessException.class, () -> service.registrarOrden(orden));
    }
}
