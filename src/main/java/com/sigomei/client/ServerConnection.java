package com.sigomei.client;

import com.sigomei.protocol.Request;
import com.sigomei.protocol.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Gestiona la conexión TCP con el servidor SIGOMEI.
 * Envía Request y recibe Response mediante serialización de objetos Java.
 */
public class ServerConnection {

    private final String host;
    private final int    port;

    private Socket             socket;
    private ObjectOutputStream out;
    private ObjectInputStream  in;

    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Establece la conexión con el servidor. */
    public void connect() throws IOException {
        socket = new Socket(host, port);
        out    = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in     = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Envía un Request al servidor y espera un Response.
     * @throws IOException si el canal está cerrado o hay error de red.
     * @throws ClassNotFoundException si la clase del objeto recibido no existe.
     */
    public Response send(Request request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        return (Response) in.readObject();
    }

    /** Cierra la conexión limpiamente. */
    public void close() {
        try { if (in     != null) in.close();     } catch (IOException ignored) {}
        try { if (out    != null) out.close();    } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        socket = null; out = null; in = null;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public String getHost() { return host; }
    public int    getPort() { return port; }
}
