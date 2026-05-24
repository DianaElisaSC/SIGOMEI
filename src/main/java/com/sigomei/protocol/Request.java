package com.sigomei.protocol;

import java.io.Serializable;

/**
 * Mensaje que el cliente envía al servidor por el socket.
 * Contiene un comando (constante de esta misma clase) y un payload opcional.
 */
public class Request implements Serializable {

    // ── Equipos ───────────────────────────────────────────────────────────────
    public static final String LIST_EQUIPOS   = "LIST_EQUIPOS";
    public static final String ADD_EQUIPO     = "ADD_EQUIPO";
    public static final String UPDATE_EQUIPO  = "UPDATE_EQUIPO";
    public static final String DELETE_EQUIPO  = "DELETE_EQUIPO";

    // ── Técnicos ──────────────────────────────────────────────────────────────
    public static final String LIST_TECNICOS  = "LIST_TECNICOS";
    public static final String ADD_TECNICO    = "ADD_TECNICO";
    public static final String UPDATE_TECNICO = "UPDATE_TECNICO";
    public static final String DELETE_TECNICO = "DELETE_TECNICO";

    // ── Órdenes ───────────────────────────────────────────────────────────────
    public static final String LIST_ORDENES       = "LIST_ORDENES";
    public static final String ADD_ORDEN          = "ADD_ORDEN";
    public static final String ACTUALIZAR_ESTADO  = "ACTUALIZAR_ESTADO";
    public static final String REGISTRAR_CIERRE   = "REGISTRAR_CIERRE";
    public static final String DELETE_ORDEN       = "DELETE_ORDEN";

    // ─────────────────────────────────────────────────────────────────────────

    private final String command;
    private final Object payload;

    public Request(String command, Object payload) {
        this.command = command;
        this.payload = payload;
    }

    public Request(String command) {
        this(command, null);
    }

    public String getCommand() { return command; }
    public Object getPayload() { return payload; }

    @Override
    public String toString() {
        return "Request{command='" + command + "', payload=" + payload + "}";
    }
}
