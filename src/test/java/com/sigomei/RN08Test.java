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
 * RN-08: Transiciones de estado validas:
 *   Programada -> En ejecucion -> Finalizada
 *   Cancelada solo desde Programada o En ejecucion.
 *   No se puede regresar a un estado anterior.
 */
@DisplayName("RN-08 Transiciones de estado validas")
class RN08Test {

    private Tecnico tecnico;
    private Equipo  equipo;

    @BeforeEach
    void setUp() {
        tecnico = new Tecnico(1, "Ivan Mora", "Hidraulico", "Activo", 1);
        equipo  = new Equipo (1, "Bomba B1", "Hidraulico", "Grundfos", "Media");
    }

    private OrdenServiceImpl serviceConOrden(int idOrden, String estado) {
        Orden orden = new Orden(idOrden, "Mantenimiento", estado,
                equipo.getIdEquipo(), tecnico.getIdTecnico());
        return new OrdenServiceImpl(List.of(tecnico), List.of(equipo), List.of(orden));
    }

    @Test
    @DisplayName("Positivo: Programada -> En ejecucion es valido")
    void debeCambiarEstadoDeProgamadaAEnEjecucion() {
        OrdenServiceImpl service = serviceConOrden(1, "Programada");

        assertDoesNotThrow(() -> service.actualizarEstado(1, "En ejecucion"));
    }

    @Test
    @DisplayName("Positivo: En ejecucion -> Finalizada es valido")
    void debeCambiarEstadoDeEnEjecucionAFinalizada() {
        OrdenServiceImpl service = serviceConOrden(2, "En ejecucion");

        assertDoesNotThrow(() -> service.actualizarEstado(2, "Finalizada"));
    }

    @Test
    @DisplayName("Positivo: Programada -> Cancelada es valido")
    void debeCancelarOrdenDesdeProgramada() {
        OrdenServiceImpl service = serviceConOrden(3, "Programada");

        assertDoesNotThrow(() -> service.actualizarEstado(3, "Cancelada"));
    }

    @Test
    @DisplayName("Negativo: Finalizada -> Programada es invalido")
    void debeLanzarExcepcionAlRegresarDeFinalizadaAProgramada() {
        OrdenServiceImpl service = serviceConOrden(4, "Finalizada");

        assertThrows(BusinessException.class,
                () -> service.actualizarEstado(4, "Programada"));
    }

    @Test
    @DisplayName("Negativo: Cancelada -> En ejecucion es invalido")
    void debeLanzarExcepcionAlReactivarOrdenCancelada() {
        OrdenServiceImpl service = serviceConOrden(5, "Cancelada");

        assertThrows(BusinessException.class,
                () -> service.actualizarEstado(5, "En ejecucion"));
    }
}
