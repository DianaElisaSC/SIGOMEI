package com.sigomei.client;

import com.sigomei.model.Orden;
import com.sigomei.protocol.Request;
import com.sigomei.protocol.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel CRUD para la entidad Orden.
 * Incluye: nueva orden, actualizar estado, registrar cierre y eliminar.
 */
public class OrdenPanel extends JPanel {

    private static final String[] COLUMNS =
            {"ID", "Descripcion", "Estado", "Equipo", "Tecnico", "F.Programada", "F.Inicio", "F.Cierre"};

    private static final String[] ESTADOS_NUEVOS = {"Programada", "En ejecucion", "Finalizada", "Cancelada"};

    private final ServerConnection  conn;
    private final DefaultTableModel model;
    private final JTable  table;
    private final JLabel  lblStatus;

    public OrdenPanel(ServerConnection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        lblStatus = new JLabel("Listo");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.ITALIC, 11f));

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnNueva      = new JButton("Nueva Orden");
        JButton btnEstado     = new JButton("Cambiar Estado");
        JButton btnCierre     = new JButton("Registrar Cierre");
        JButton btnEliminar   = new JButton("Eliminar");

        btnActualizar.addActionListener(e -> loadData());
        btnNueva     .addActionListener(e -> addOrden());
        btnEstado    .addActionListener(e -> cambiarEstado());
        btnCierre    .addActionListener(e -> registrarCierre());
        btnEliminar  .addActionListener(e -> deleteOrden());

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlTop.add(btnActualizar); pnlTop.add(lblStatus);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlBot.add(btnNueva); pnlBot.add(btnEstado);
        pnlBot.add(btnCierre); pnlBot.add(btnEliminar);

        add(pnlTop,                 BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlBot,                 BorderLayout.SOUTH);

        loadData();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        model.setRowCount(0);
        if (!conn.isConnected()) { lblStatus.setText("Sin conexion con el servidor"); return; }
        try {
            Response resp = conn.send(new Request(Request.LIST_ORDENES));
            if (resp.isSuccess()) {
                List<Orden> lista = (List<Orden>) resp.getData();
                for (Orden o : lista)
                    model.addRow(new Object[]{
                            o.getIdOrden(), o.getDescripcion(), o.getEstado(),
                            o.getIdEquipo(), o.getIdTecnico(),
                            o.getFechaProgramada(), o.getFechaInicio(), o.getFechaCierre()
                    });
                lblStatus.setText(lista.size() + " ordenes cargadas");
            } else {
                lblStatus.setText("Error: " + resp.getMessage());
            }
        } catch (Exception ex) {
            lblStatus.setText("Error de comunicacion: " + ex.getMessage());
        }
    }

    // ── Nueva orden ─────────────────────────────────────────────────────────
    private void addOrden() {
        NuevaOrdenDialog dlg = new NuevaOrdenDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.confirmed)
            sendAndRefresh(new Request(Request.ADD_ORDEN, dlg.getOrden()), "Orden registrada");
    }

    // ── Cambiar estado ───────────────────────────────────────────────────────
    private void cambiarEstado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una orden."); return; }
        int id = (int) model.getValueAt(row, 0);

        String nuevoEstado = (String) JOptionPane.showInputDialog(
                this, "Nuevo estado para la orden #" + id + ":", "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE, null, ESTADOS_NUEVOS, ESTADOS_NUEVOS[1]);
        if (nuevoEstado != null) {
            Object[] payload = {id, nuevoEstado};
            sendAndRefresh(new Request(Request.ACTUALIZAR_ESTADO, payload), "Estado actualizado a " + nuevoEstado);
        }
    }

    // ── Registrar cierre ─────────────────────────────────────────────────────
    private void registrarCierre() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una orden."); return; }
        int id = (int) model.getValueAt(row, 0);

        CierreDialog dlg = new CierreDialog(SwingUtilities.getWindowAncestor(this), id);
        dlg.setVisible(true);
        if (dlg.confirmed) {
            Object[] payload = {id, dlg.fechaInicio, dlg.fechaCierre};
            sendAndRefresh(new Request(Request.REGISTRAR_CIERRE, payload), "Cierre registrado");
        }
    }

    // ── Eliminar orden ───────────────────────────────────────────────────────
    private void deleteOrden() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una orden."); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar la orden #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            sendAndRefresh(new Request(Request.DELETE_ORDEN, id), "Orden eliminada");
    }

    private void sendAndRefresh(Request req, String okMsg) {
        if (!conn.isConnected()) { lblStatus.setText("Sin conexion con el servidor"); return; }
        try {
            Response resp = conn.send(req);
            if (resp.isSuccess()) { lblStatus.setText(okMsg); loadData(); }
            else                   JOptionPane.showMessageDialog(this, "Regla de negocio: " + resp.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de comunicacion:\n" + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Diálogo nueva orden
    // ═══════════════════════════════════════════════════════════════════════
    private static class NuevaOrdenDialog extends JDialog {
        boolean confirmed = false;
        private final JTextField txtDesc       = new JTextField(22);
        private final JTextField txtIdEquipo   = new JTextField(6);
        private final JTextField txtIdTecnico  = new JTextField(6);
        private final JTextField txtFechaProg  = new JTextField("2026-06-01", 12);

        NuevaOrdenDialog(Window owner) {
            super(owner, "Nueva Orden", ModalityType.APPLICATION_MODAL);
            JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
            form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            form.add(new JLabel("Descripcion:"));    form.add(txtDesc);
            form.add(new JLabel("ID Equipo:"));      form.add(txtIdEquipo);
            form.add(new JLabel("ID Tecnico:"));     form.add(txtIdTecnico);
            form.add(new JLabel("F.Programada (YYYY-MM-DD):")); form.add(txtFechaProg);

            JButton ok = new JButton("Aceptar"), cancel = new JButton("Cancelar");
            ok.addActionListener(e -> {
                if (txtDesc.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La descripcion es obligatoria."); return;
                }
                try { Integer.parseInt(txtIdEquipo.getText().trim());
                      Integer.parseInt(txtIdTecnico.getText().trim()); }
                catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "IDs deben ser numeros enteros."); return;
                }
                confirmed = true; dispose();
            });
            cancel.addActionListener(e -> dispose());

            JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pnlBtn.add(ok); pnlBtn.add(cancel);
            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER); add(pnlBtn, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }

        Orden getOrden() {
            Orden o = new Orden(0, txtDesc.getText().trim(), "Programada",
                    Integer.parseInt(txtIdEquipo.getText().trim()),
                    Integer.parseInt(txtIdTecnico.getText().trim()));
            o.setFechaProgramada(LocalDate.parse(txtFechaProg.getText().trim()));
            return o;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Diálogo registrar cierre
    // ═══════════════════════════════════════════════════════════════════════
    private static class CierreDialog extends JDialog {
        boolean confirmed = false;
        LocalDate fechaInicio, fechaCierre;
        private final JTextField txtInicio = new JTextField("2026-06-01", 12);
        private final JTextField txtCierre = new JTextField("2026-06-10", 12);

        CierreDialog(Window owner, int idOrden) {
            super(owner, "Registrar Cierre — Orden #" + idOrden, ModalityType.APPLICATION_MODAL);
            JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
            form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            form.add(new JLabel("Fecha Inicio (YYYY-MM-DD):")); form.add(txtInicio);
            form.add(new JLabel("Fecha Cierre (YYYY-MM-DD):")); form.add(txtCierre);

            JButton ok = new JButton("Aceptar"), cancel = new JButton("Cancelar");
            ok.addActionListener(e -> {
                try {
                    fechaInicio = LocalDate.parse(txtInicio.getText().trim());
                    fechaCierre = LocalDate.parse(txtCierre.getText().trim());
                    confirmed = true; dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Formato de fecha invalido. Use YYYY-MM-DD.");
                }
            });
            cancel.addActionListener(e -> dispose());

            JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pnlBtn.add(ok); pnlBtn.add(cancel);
            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER); add(pnlBtn, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }
    }
}
