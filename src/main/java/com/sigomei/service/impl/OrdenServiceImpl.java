package com.sigomei.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.service.OrdenService;

public class OrdenServiceImpl implements OrdenService {

    private final List<Tecnico> tecnicos;
    private final List<Equipo>  equipos;
    private final List<Orden>   ordenes;

    public OrdenServiceImpl() {
        this.tecnicos = new ArrayList<>();
        this.equipos  = new ArrayList<>();
        this.ordenes  = new ArrayList<>();
    }

    public OrdenServiceImpl(List<Tecnico> tecnicos, List<Equipo> equipos,
                            List<Orden> ordenes) {
        this.tecnicos = new ArrayList<>(tecnicos);
        this.equipos  = new ArrayList<>(equipos);
        this.ordenes  = new ArrayList<>(ordenes);
    }

    @Override
    public void registrarOrden(Orden orden) throws BusinessException {
        Tecnico tecnico = tecnicos.stream()
                .filter(t -> t.getIdTecnico() == orden.getIdTecnico())
                .findFirst()
                .orElseThrow(() -> new BusinessException("Tecnico no encontrado"));

        Equipo equipo = equipos.stream()
                .filter(e -> e.getIdEquipo() == orden.getIdEquipo())
                .findFirst()
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // RN-03: Tecnico debe estar Activo
        if (!"Activo".equals(tecnico.getEstatus())) {
            throw new BusinessException("El tecnico esta Inactivo.");
        }

        // RN-01: Especialidad debe coincidir con tipo de equipo
        if (!tecnico.getEspecialidad().equalsIgnoreCase(equipo.getTipo())) {
            throw new BusinessException("La especialidad del tecnico no coincide con el tipo del equipo.");
        }

        // RN-07: Criticidad Alta requiere certificacion nivel II o III
        if ("Alta".equals(equipo.getCriticidad()) && tecnico.getNivelCertificacion() < 2) {
            throw new BusinessException("Equipo Alta requiere certificacion nivel II o III.");
        }

        // RN-02: No puede haber dos ordenes activas para el mismo equipo en la misma fecha
        boolean duplicada = ordenes.stream()
                .anyMatch(o -> o.getIdEquipo() == orden.getIdEquipo()
                        && ("Programada".equalsIgnoreCase(o.getEstado())
                            || "En ejecucion".equalsIgnoreCase(o.getEstado()))
                        && (o.getFechaProgramada() == null && orden.getFechaProgramada() == null
                            || o.getFechaProgramada() != null
                            && o.getFechaProgramada().equals(orden.getFechaProgramada())));
        if (duplicada) {
            throw new BusinessException("Ya existe una orden activa para ese equipo en esa fecha.");
        }

        ordenes.add(orden);
    }

    @Override
    public void actualizarEstado(int idOrden, String nuevoEstado) throws BusinessException {
        Orden orden = ordenes.stream()
                .filter(o -> o.getIdOrden() == idOrden)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Orden no encontrada"));

        String actual = orden.getEstado();
        boolean valida = false;

        // RN-08: transiciones validas
        switch (actual) {
            case "Programada":
                valida = "En ejecucion".equals(nuevoEstado) || "Cancelada".equals(nuevoEstado);
                break;
            case "En ejecucion":
                valida = "Finalizada".equals(nuevoEstado) || "Cancelada".equals(nuevoEstado);
                break;
            default:
                valida = false;
        }

        if ("Programada".equals(actual) && "Finalizada".equals(nuevoEstado)) {
            throw new BusinessException("Transicion invalida. No se puede pasar de Programada a Finalizada directamente.");
        }

        if (!valida) {
            throw new BusinessException("Transicion invalida: " + actual + " -> " + nuevoEstado);
        }

        orden.setEstado(nuevoEstado);
    }

    @Override
    public void registrarCierre(int idOrden, LocalDate fechaInicio, LocalDate fechaCierre)
            throws BusinessException {
        Orden orden = ordenes.stream()
                .filter(o -> o.getIdOrden() == idOrden)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Orden no encontrada"));

        // RN-06: Solo en ordenes Finalizadas
        if (!"Finalizada".equals(orden.getEstado())) {
            throw new BusinessException("Solo se registra cierre en ordenes Finalizadas.");
        }

        // RN-05: fechaProgramada <= fechaInicio <= fechaCierre
        LocalDate programada = orden.getFechaProgramada();
        if (programada != null && fechaInicio.isBefore(programada)) {
            throw new BusinessException("fechaInicio no puede ser anterior a fechaProgramada.");
        }
        if (fechaCierre.isBefore(fechaInicio)) {
            throw new BusinessException("fechaCierre no puede ser anterior a fechaInicio.");
        }

        orden.setFechaInicio(fechaInicio);
        orden.setFechaCierre(fechaCierre);
    }
}