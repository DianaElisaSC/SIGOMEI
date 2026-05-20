package com.sigomei.client;

import java.net.Socket;

public class ClientMain {

    public static void main(String[] args) {

        try {

            Socket socket =
                    new Socket("localhost", 5000);

            System.out.println(
                    "Conectado al servidor"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}