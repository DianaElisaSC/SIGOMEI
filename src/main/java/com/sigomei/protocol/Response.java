package com.sigomei.protocol;

import java.io.Serializable;

/**
 * Mensaje que el servidor devuelve al cliente.
 * Indica éxito/fallo, datos opcionales y un mensaje legible.
 */
public class Response implements Serializable {

    private final boolean success;
    private final Object  data;
    private final String  message;

    public Response(boolean success, Object data, String message) {
        this.success = success;
        this.data    = data;
        this.message = message;
    }

    /** Respuesta exitosa con datos. */
    public static Response ok(Object data) {
        return new Response(true, data, "OK");
    }

    /** Respuesta exitosa sin datos. */
    public static Response ok() {
        return new Response(true, null, "OK");
    }

    /** Respuesta de error con mensaje descriptivo. */
    public static Response error(String message) {
        return new Response(false, null, message);
    }

    public boolean isSuccess() { return success; }
    public Object  getData()   { return data; }
    public String  getMessage(){ return message; }

    @Override
    public String toString() {
        return "Response{success=" + success + ", message='" + message + "'}";
    }
}
