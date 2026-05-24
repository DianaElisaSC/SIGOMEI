package com.sigomei.server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket =
                    new ServerSocket(8080);

            System.out.println(
                    "Servidor iniciado..."
            );

            while (true) {

                Socket socket =
                        serverSocket.accept();

                System.out.println(
                        "Cliente conectado"
                );

                ClientHandler handler =
                        new ClientHandler(socket);

                handler.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}