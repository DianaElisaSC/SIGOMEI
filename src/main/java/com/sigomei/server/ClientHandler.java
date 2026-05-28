package com.sigomei.server;

import com.sigomei.config.ConexionBD;
import com.sigomei.dao.impl.EquipoDAOImpl;
import com.sigomei.dao.impl.OrdenDAOImpl;
import com.sigomei.dao.impl.TecnicoDAOImpl;
import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.protocol.Request;
import com.sigomei.protocol.Response;
import com.sigomei.service.impl.EquipoServiceImpl;
import com.sigomei.service.impl.OrdenServiceImpl;
import com.sigomei.service.impl.TecnicoServiceImpl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/**
 * Maneja la sesión de un cliente conectado.
 * Lee objetos Request y responde con objetos Response
 * usando serialización Java sobre TCP.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private EquipoDAOImpl equipoDAO;
    private TecnicoDAOImpl tecnicoDAO;
    private OrdenDAOImpl ordenDAO;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("[" + remote + "] Sesion iniciada");

        try (
            Connection conn = ConexionBD.conectar();
            // OOS primero + flush para evitar deadlock al crear OIS
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            out.flush();

            equipoDAO = new EquipoDAOImpl(conn);
            tecnicoDAO = new TecnicoDAOImpl(conn);
            ordenDAO = new OrdenDAOImpl(conn);

            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                Request req;
                while ((req = (Request) in.readObject()) != null) {
                    Response resp = dispatch(req);
                    out.writeObject(resp);
                    out.flush();
                    out.reset(); // evita acumular caché de objetos serializados
                }

            }
        } catch (Exception e) {
            // Fin de sesión normal (cliente cerró conexión) o error de red
            System.out.println("[" + remote + "] Sesion finalizada: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    // =========================================================
    // DESPACHADOR
    // =========================================================

    private Response dispatch(Request req) {
        try {
            return switch (req.getCommand()) {

                // EQUIPOS
                case Request.LIST_EQUIPOS  -> Response.ok(equipoDAO.listar());
                case Request.ADD_EQUIPO    -> addEquipo((Equipo) req.getPayload());
                case Request.UPDATE_EQUIPO -> updateEquipo((Equipo) req.getPayload());
                case Request.DELETE_EQUIPO -> deleteEquipo((Integer) req.getPayload());

                // TECNICOS
                case Request.LIST_TECNICOS  -> Response.ok(tecnicoDAO.listar());
                case Request.ADD_TECNICO    -> addTecnico((Tecnico) req.getPayload());
                case Request.UPDATE_TECNICO -> updateTecnico((Tecnico) req.getPayload());
                case Request.DELETE_TECNICO -> deleteTecnico((Integer) req.getPayload());

                // ORDENES
                case Request.LIST_ORDENES      -> Response.ok(ordenDAO.listar());
                case Request.ADD_ORDEN         -> addOrden((Orden) req.getPayload());
                case Request.ACTUALIZAR_ESTADO -> actualizarEstado((Object[]) req.getPayload());
                case Request.REGISTRAR_CIERRE  -> registrarCierre((Object[]) req.getPayload());
                case Request.DELETE_ORDEN      -> deleteOrden((Integer) req.getPayload());

                default -> Response.error("Comando desconocido: " + req.getCommand());
            };
        } catch (Exception e) {
            return Response.error("Error interno: " + e.getMessage());
        }
    }

    // =========================================================
    // EQUIPOS
    // =========================================================

    private Response addEquipo(Equipo equipo) {
        try {
            new EquipoServiceImpl(ordenDAO.listar()).registrarEquipo(equipo);
            equipoDAO.guardar(equipo);
            return Response.ok();
        } catch (BusinessException e) {
            return Response.error(e.getMessage());
        }
    }

    private Response updateEquipo(Equipo equipo) {
        equipoDAO.actualizar(equipo);
        return Response.ok();
    }

    private Response deleteEquipo(int id) {
        try {
            new EquipoServiceImpl(ordenDAO.listar()).eliminarEquipo(id);
            equipoDAO.eliminar(id);
            return Response.ok();
        } catch (BusinessException e) {
            return Response.error(e.getMessage());
        }
    }

    // =========================================================
    // TECNICOS
    // =========================================================

    private Response addTecnico(Tecnico tecnico) {
        try {
            new TecnicoServiceImpl().registrarTecnico(tecnico);
            tecnicoDAO.guardar(tecnico);
            return Response.ok();
        } catch (RuntimeException e) {
            if ("RFC_DUPLICADO".equals(e.getMessage())) {
                return Response.error("RFC duplicado: ya existe un técnico con ese RFC.");
            }
            return Response.error("Error interno: " + e.getMessage());
        } catch (BusinessException e) {
            return Response.error(e.getMessage());
        }
    }

    private Response updateTecnico(Tecnico tecnico) {
        tecnicoDAO.actualizar(tecnico);
        return Response.ok();
    }

    private Response deleteTecnico(int id) {
    try {
        // Verificar que no tenga órdenes registradas (RN-04)
        boolean tieneOrdenes = ordenDAO.listar().stream()
                .anyMatch(o -> o.getIdTecnico() == id);
        if (tieneOrdenes) {
            return Response.error("No se puede eliminar el tecnico porque tiene ordenes de mantenimiento registradas.");
        }
        tecnicoDAO.eliminar(id);
        return Response.ok();
    } catch (Exception e) {
        return Response.error("Error interno: " + e.getMessage());
    }
}

    // =========================================================
    // ORDENES
    // =========================================================

    private Response addOrden(Orden orden) {
        try {
            new OrdenServiceImpl(
                    tecnicoDAO.listar(),
                    equipoDAO.listar(),
                    ordenDAO.listar()
            ).registrarOrden(orden);
            ordenDAO.guardar(orden);
            return Response.ok();
        } catch (BusinessException e) {
            return Response.error(e.getMessage());
        }
    }

    private Response actualizarEstado(Object[] payload) {
        int    id     = (Integer) payload[0];
        String estado = (String)  payload[1];
        try {
            List<Orden> ordenes = ordenDAO.listar();
            new OrdenServiceImpl(
                    tecnicoDAO.listar(),
                    equipoDAO.listar(),
                    ordenes
            ).actualizarEstado(id, estado);
            // El servicio modifica el objeto Orden dentro de la lista por referencia
            Orden actualizada = ordenes.stream()
                    .filter(o -> o.getIdOrden() == id)
                    .findFirst()
                    .orElseThrow();
            ordenDAO.actualizar(actualizada);
            return Response.ok();
        } catch (BusinessException e) {
            return Response.error(e.getMessage());
        }
    }

    private Response registrarCierre(Object[] payload) {
        int       id    = (Integer)   payload[0];
        LocalDate inicio = (LocalDate) payload[1];
        LocalDate cierre = (LocalDate) payload[2];
        try {
            List<Orden> ordenes = ordenDAO.listar();
            new OrdenServiceImpl(
                    tecnicoDAO.listar(),
                    equipoDAO.listar(),
                    ordenes
            ).registrarCierre(id, inicio, cierre);
            Orden actualizada = ordenes.stream()
                    .filter(o -> o.getIdOrden() == id)
                    .findFirst()
                    .orElseThrow();
            ordenDAO.actualizar(actualizada);
            return Response.ok();
        } catch (BusinessException e) {
            return Response.error(e.getMessage());
        }
    }

    private Response deleteOrden(int id) {
        ordenDAO.eliminar(id);
        return Response.ok();
    }
}
