package com.sigomei.server;

import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        throw new UnsupportedOperationException(
                "No implementado"
        );
    }
}
