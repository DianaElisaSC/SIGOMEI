package com.sigomei.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación cliente SIGOMEI.
 * Contiene tres pestañas (Equipos, Tecnicos, Ordenes) y una barra
 * de estado con información de la conexión al servidor.
 */
public class MainFrame extends JFrame {

    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = 5000;

    private final ServerConnection conn;
    private final JLabel lblConexion;
    private final JTabbedPane tabs;

    public MainFrame() {
        super("SIGOMEI — Sistema de Gestion de Ordenes de Mantenimiento");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);

        conn = new ServerConnection(DEFAULT_HOST, DEFAULT_PORT);

        // ── Barra de estado ────────────────────────────────────────────────
        lblConexion = new JLabel("  Desconectado");
        lblConexion.setForeground(Color.RED);
        lblConexion.setFont(lblConexion.getFont().deriveFont(Font.BOLD, 11f));

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("Servidor:"));
        statusBar.add(lblConexion);

        // ── Menú ───────────────────────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();

        JMenu menuConexion = new JMenu("Conexion");
        JMenuItem miConectar    = new JMenuItem("Conectar al servidor");
        JMenuItem miDesconectar = new JMenuItem("Desconectar");
        JMenuItem miSalir       = new JMenuItem("Salir");
        miConectar   .addActionListener(e -> conectar());
        miDesconectar.addActionListener(e -> desconectar());
        miSalir      .addActionListener(e -> salir());
        menuConexion.add(miConectar); menuConexion.add(miDesconectar);
        menuConexion.addSeparator();  menuConexion.add(miSalir);
        menuBar.add(menuConexion);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem miAcerca = new JMenuItem("Acerca de SIGOMEI");
        miAcerca.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "SIGOMEI v1.0\nSistema de Gestion de Ordenes de Mantenimiento\n" +
                "Desarrollo de Sistemas en Red — E4\nUniversidad Veracruzana",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(miAcerca);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);

        // ── Pestañas ───────────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.addTab("Equipos",  new EquipoPanel(conn));
        tabs.addTab("Tecnicos", new TecnicoPanel(conn));
        tabs.addTab("Ordenes",  new OrdenPanel(conn));

        // ── Layout principal ───────────────────────────────────────────────
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs,      BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        // ── Cierre limpio ──────────────────────────────────────────────────
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });

        // Intentar conexión al iniciar
        conectar();
    }

    /** Intenta conectar al servidor. Muestra un diálogo si falla. */
    private void conectar() {
        if (conn.isConnected()) { actualizarEstado(); return; }
        try {
            conn.connect();
            actualizarEstado();
            refrescarPestanas();
        } catch (Exception ex) {
            actualizarEstado();
            JOptionPane.showMessageDialog(this,
                "No se pudo conectar a " + DEFAULT_HOST + ":" + DEFAULT_PORT + "\n" +
                "Verifique que el servidor este en ejecucion.\n\n" + ex.getMessage(),
                "Sin conexion", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Desconecta del servidor. */
    private void desconectar() {
        conn.close();
        actualizarEstado();
    }

    /** Actualiza el indicador de estado en la barra inferior. */
    private void actualizarEstado() {
        if (conn.isConnected()) {
            lblConexion.setText("  Conectado a " + conn.getHost() + ":" + conn.getPort());
            lblConexion.setForeground(new Color(0, 128, 0));
        } else {
            lblConexion.setText("  Desconectado");
            lblConexion.setForeground(Color.RED);
        }
    }

    /** Recarga los datos en todas las pestañas. */
    private void refrescarPestanas() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            Component c = tabs.getComponentAt(i);
            if (c instanceof EquipoPanel)  ((EquipoPanel)  c).revalidate();
            if (c instanceof TecnicoPanel) ((TecnicoPanel) c).revalidate();
            if (c instanceof OrdenPanel)   ((OrdenPanel)   c).revalidate();
        }
    }

    private void salir() {
        conn.close();
        dispose();
        System.exit(0);
    }
}
