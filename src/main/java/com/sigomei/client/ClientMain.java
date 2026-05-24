package com.sigomei.client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del cliente SIGOMEI.
 * Lanza la ventana principal en el hilo de despacho de eventos (EDT).
 */
public class ClientMain {

    public static void main(String[] args) {
        // Aplicar look-and-feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Lanzar GUI en el EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}