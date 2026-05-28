package com.sigomei.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket =
                    new ServerSocket(5000);

            System.out.println(
                    "Servidor iniciado..."
            );

        
        ExecutorService pool = Executors.newFixedThreadPool(10);

    while (true) {
        
        Socket socket = serverSocket.accept();

        System.out.println("Cliente conectado");

        ClientHandler handler = new ClientHandler(socket);  

        pool.execute(handler);  
    }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}