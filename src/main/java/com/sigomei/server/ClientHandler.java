```java
package com.sigomei.server;

import com.google.gson.*;
import com.sigomei.dao.impl.EquipoDAOImpl;
import com.sigomei.dao.impl.OrdenDAOImpl;
import com.sigomei.dao.impl.TecnicoDAOImpl;
import com.sigomei.exception.BusinessException;
import com.sigomei.model.Equipo;
import com.sigomei.model.Orden;
import com.sigomei.model.Tecnico;
import com.sigomei.service.impl.EquipoServiceImpl;
import com.sigomei.service.impl.OrdenServiceImpl;
import com.sigomei.service.impl.TecnicoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class ClientHandler extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                            -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, typeOfT, context)
                            -> LocalDate.parse(json.getAsString()))
            .create();

    private final Socket socket;

    private final EquipoDAOImpl equipoDAO = new EquipoDAOImpl();
    private final TecnicoDAOImpl tecnicoDAO = new TecnicoDAOImpl();
    private final OrdenDAOImpl ordenDAO = new OrdenDAOImpl();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String remote = socket.getRemoteSocketAddress().toString();

        LOG.info("[{}] Sesión iniciada", remote);

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            String line;

            while ((line = in.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                LOG.info("[{}] Mensaje recibido: {}", remote, line);

                String response = dispatch(line);

                out.println(response);

                LOG.info("[{}] Respuesta enviada: {}", remote, response);
            }

        } catch (Exception e) {

            LOG.error("[{}] Error en sesión: {}", remote, e.getMessage(), e);

        } finally {

            try {
                socket.close();
            } catch (Exception ignored) {}

            LOG.info("[{}] Sesión finalizada", remote);
        }
    }

    // =========================================================
    // DESPACHADOR
    // =========================================================

    private String dispatch(String json) {

        try {

            JsonObject request = GSON.fromJson(json, JsonObject.class);

            String tipo = request.get("tipo").getAsString();

            JsonObject payload = request.has("payload")
                    ? request.getAsJsonObject("payload")
                    : new JsonObject();

            return switch (tipo) {

                case "PING" -> ok("pong", null);

                // EQUIPOS
                case "CREAR_EQUIPO" -> crearEquipo(payload);
                case "LISTAR_EQUIPOS" -> listarEquipos();
                case "ACTUALIZAR_EQUIPO" -> actualizarEquipo(payload);
                case "ELIMINAR_EQUIPO" -> eliminarEquipo(payload);

                // TECNICOS
                case "CREAR_TECNICO" -> crearTecnico(payload);
                case "LISTAR_TECNICOS" -> listarTecnicos();
                case "ACTUALIZAR_TECNICO" -> actualizarTecnico(payload);

                // ORDENES
                case "CREAR_ORDEN" -> crearOrden(payload);
                case "LISTAR_ORDENES" -> listarOrdenes();
                case "CAMBIAR_ESTADO_ORDEN" -> cambiarEstadoOrden(payload);
                case "REGISTRAR_CIERRE" -> registrarCierre(payload);
                case "CANCELAR_ORDEN" -> cancelarOrden(payload);
                case "HISTORIAL_EQUIPO" -> historialEquipo(payload);

                default -> error(
                        "OPERACION_DESCONOCIDA",
                        "Tipo desconocido: " + tipo
                );
            };

        } catch (Exception e) {

            LOG.error("Error procesando mensaje", e);

            return error(
                    "INTERNAL_ERROR",
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // EQUIPOS
    // =========================================================

    private String crearEquipo(JsonObject payload) {

        try {

            Equipo equipo = GSON.fromJson(payload, Equipo.class);

            EquipoServiceImpl service =
                    new EquipoServiceImpl(ordenDAO.listar());

            service.registrarEquipo(equipo);

            equipoDAO.guardar(equipo);

            return ok(
                    "Equipo registrado",
                    GSON.toJsonTree(equipo)
            );

        } catch (BusinessException e) {

            return error(
                    "REGLA_NEGOCIO",
                    e.getMessage()
            );
        }
    }

    private String listarEquipos() {

        List<Equipo> lista = equipoDAO.listar();

        return ok(
                "OK",
                GSON.toJsonTree(lista)
        );
    }

    private String actualizarEquipo(JsonObject payload) {

        Equipo equipo = GSON.fromJson(payload, Equipo.class);

        equipoDAO.actualizar(equipo);

        return ok(
                "Equipo actualizado",
                GSON.toJsonTree(equipo)
        );
    }

    private String eliminarEquipo(JsonObject payload) {

        int idEquipo = payload.get("idEquipo").getAsInt();

        try {

            EquipoServiceImpl service =
                    new EquipoServiceImpl(ordenDAO.listar());

            service.eliminarEquipo(idEquipo);

            equipoDAO.eliminar(idEquipo);

            return ok(
                    "Equipo eliminado",
                    null
            );

        } catch (BusinessException e) {

            return error(
                    "EQUIPO_CON_ORDENES",
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // TECNICOS
    // =========================================================

    private String crearTecnico(JsonObject payload) {

        try {

            Tecnico tecnico = GSON.fromJson(payload, Tecnico.class);

            TecnicoServiceImpl service = new TecnicoServiceImpl();

            service.registrarTecnico(tecnico);

            tecnicoDAO.guardar(tecnico);

            return ok(
                    "Tecnico registrado",
                    GSON.toJsonTree(tecnico)
            );

        } catch (BusinessException e) {

            return error(
                    "REGLA_NEGOCIO",
                    e.getMessage()
            );
        }
    }

    private String listarTecnicos() {

        List<Tecnico> lista = tecnicoDAO.listar();

        return ok(
                "OK",
                GSON.toJsonTree(lista)
        );
    }

    private String actualizarTecnico(JsonObject payload) {

        Tecnico tecnico = GSON.fromJson(payload, Tecnico.class);

        tecnicoDAO.actualizar(tecnico);

        return ok(
                "Tecnico actualizado",
                GSON.toJsonTree(tecnico)
        );
    }

    // =========================================================
    // ORDENES
    // =========================================================

    private String crearOrden(JsonObject payload) {

        try {

            Orden orden = GSON.fromJson(payload, Orden.class);

            OrdenServiceImpl service =
                    new OrdenServiceImpl(
                            tecnicoDAO.listar(),
                            equipoDAO.listar(),
                            ordenDAO.listar()
                    );

            service.registrarOrden(orden);

            ordenDAO.guardar(orden);

            return ok(
                    "Orden registrada",
                    GSON.toJsonTree(orden)
            );

        } catch (BusinessException e) {

            return error(
                    "REGLA_NEGOCIO",
                    e.getMessage()
            );
        }
    }

    private String listarOrdenes() {

        List<Orden> lista = ordenDAO.listar();

        return ok(
                "OK",
                GSON.toJsonTree(lista)
        );
    }

    private String cambiarEstadoOrden(JsonObject payload) {

        int idOrden = payload.get("idOrden").getAsInt();

        String nuevoEstado =
                payload.get("nuevoEstado").getAsString();

        try {

            List<Orden> ordenes = ordenDAO.listar();

            OrdenServiceImpl service =
                    new OrdenServiceImpl(
                            tecnicoDAO.listar(),
                            equipoDAO.listar(),
                            ordenes
                    );

            service.actualizarEstado(idOrden, nuevoEstado);

            Orden actualizada = ordenes.stream()
                    .filter(o -> o.getIdOrden() == idOrden)
                    .findFirst()
                    .orElseThrow();

            ordenDAO.actualizar(actualizada);

            return ok(
                    "Estado actualizado",
                    GSON.toJsonTree(actualizada)
            );

        } catch (BusinessException e) {

            return error(
                    "TRANSICION_INVALIDA",
                    e.getMessage()
            );
        }
    }

    private String registrarCierre(JsonObject payload) {

        int idOrden =
                payload.get("idOrden").getAsInt();

        LocalDate fechaInicio =
                LocalDate.parse(payload.get("fechaInicio").getAsString());

        LocalDate fechaCierre =
                LocalDate.parse(payload.get("fechaCierre").getAsString());

        try {

            List<Orden> ordenes = ordenDAO.listar();

            OrdenServiceImpl service =
                    new OrdenServiceImpl(
                            tecnicoDAO.listar(),
                            equipoDAO.listar(),
                            ordenes
                    );

            service.registrarCierre(
                    idOrden,
                    fechaInicio,
                    fechaCierre
            );

            Orden actualizada = ordenes.stream()
                    .filter(o -> o.getIdOrden() == idOrden)
                    .findFirst()
                    .orElseThrow();

            ordenDAO.actualizar(actualizada);

            return ok(
                    "Cierre registrado",
                    GSON.toJsonTree(actualizada)
            );

        } catch (BusinessException e) {

            return error(
                    "ERROR_CIERRE",
                    e.getMessage()
            );
        }
    }

    private String cancelarOrden(JsonObject payload) {

        int idOrden =
                payload.get("idOrden").getAsInt();

        try {

            List<Orden> ordenes = ordenDAO.listar();

            OrdenServiceImpl service =
                    new OrdenServiceImpl(
                            tecnicoDAO.listar(),
                            equipoDAO.listar(),
                            ordenes
                    );

            service.actualizarEstado(
                    idOrden,
                    "Cancelada"
            );

            Orden actualizada = ordenes.stream()
                    .filter(o -> o.getIdOrden() == idOrden)
                    .findFirst()
                    .orElseThrow();

            ordenDAO.actualizar(actualizada);

            return ok(
                    "Orden cancelada",
                    GSON.toJsonTree(actualizada)
            );

        } catch (BusinessException e) {

            return error(
                    "CANCELACION_INVALIDA",
                    e.getMessage()
            );
        }
    }

    private String historialEquipo(JsonObject payload) {

        int idEquipo =
                payload.get("idEquipo").getAsInt();

        List<Orden> lista =
                ordenDAO.listarPorEquipo(idEquipo);

        return ok(
                "OK",
                GSON.toJsonTree(lista)
        );
    }

    // =========================================================
    // RESPUESTAS JSON
    // =========================================================

    private String ok(String mensaje, JsonElement payload) {

        JsonObject response = new JsonObject();

        response.addProperty("status", "OK");
        response.addProperty("mensaje", mensaje);

        response.add(
                "payload",
                payload != null ? payload : JsonNull.INSTANCE
        );

        return GSON.toJson(response);
    }

    private String error(String codigo, String mensaje) {

        JsonObject response = new JsonObject();

        response.addProperty("status", "ERROR");
        response.addProperty("codigoError", codigo);
        response.addProperty("mensaje", mensaje);

        response.add("payload", JsonNull.INSTANCE);

        return GSON.toJson(response);
    }
}
```

