package com.sigomei.client;

import com.sigomei.model.Equipo;
import com.sigomei.protocol.Request;
import com.sigomei.protocol.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la entidad Equipo.
 * Comunica todas las operaciones al servidor via ServerConnection.
 */
public class EquipoPanel extends JPanel {

    private static final String[] COLUMNS =
            {"ID", "Nombre", "Tipo", "Marca", "Criticidad"};

    private static final String[] TIPOS       = {"Electrico", "Mecanico", "Instrumentacion", "Hidraulico"};
    private static final String[] CRITICIDADES= {"Baja", "Media", "Alta"};

    private final ServerConnection conn;
    private final DefaultTableModel model;
    private final JTable  table;
    private final JLabel  lblStatus;

    public EquipoPanel(ServerConnection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ── Tabla ──────────────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // ── Barra de estado ────────────────────────────────────────────────
        lblStatus = new JLabel("Listo");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.ITALIC, 11f));

        // ── Botones ────────────────────────────────────────────────────────
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnNuevo      = new JButton("Nuevo");
        JButton btnEditar     = new JButton("Editar");
        JButton btnEliminar   = new JButton("Eliminar");

        btnActualizar.addActionListener(e -> loadData());
        btnNuevo     .addActionListener(e -> addEquipo());
        btnEditar    .addActionListener(e -> editEquipo());
        btnEliminar  .addActionListener(e -> deleteEquipo());

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlTop.add(btnActualizar);
        pnlTop.add(lblStatus);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlBot.add(btnNuevo); pnlBot.add(btnEditar); pnlBot.add(btnEliminar);

        add(pnlTop,                          BorderLayout.NORTH);
        add(new JScrollPane(table),           BorderLayout.CENTER);
        add(pnlBot,                           BorderLayout.SOUTH);

        loadData();
    }

    // ── Cargar datos ────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void loadData() {
        model.setRowCount(0);
        if (!conn.isConnected()) { lblStatus.setText("Sin conexion con el servidor"); return; }
        try {
            Response resp = conn.send(new Request(Request.LIST_EQUIPOS));
            if (resp.isSuccess()) {
                List<Equipo> lista = (List<Equipo>) resp.getData();
                for (Equipo e : lista)
                    model.addRow(new Object[]{e.getIdEquipo(), e.getNombre(), e.getTipo(), e.getMarca(), e.getCriticidad()});
                lblStatus.setText(lista.size() + " equipos cargados");
            } else {
                lblStatus.setText("Error: " + resp.getMessage());
            }
        } catch (Exception ex) {
            lblStatus.setText("Error de comunicacion: " + ex.getMessage());
        }
    }

    // ── Agregar equipo ──────────────────────────────────────────────────────
    private void addEquipo() {
        EquipoDialog dlg = new EquipoDialog(SwingUtilities.getWindowAncestor(this), "Nuevo Equipo", null);
        dlg.setVisible(true);
        if (dlg.confirmed) {
            Equipo eq = dlg.getEquipo();
            sendAndRefresh(new Request(Request.ADD_EQUIPO, eq), "Equipo registrado correctamente");
        }
    }

    // ── Editar equipo ───────────────────────────────────────────────────────
    private void editEquipo() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un equipo para editar."); return; }
        Equipo sel = rowToEquipo(row);
        EquipoDialog dlg = new EquipoDialog(SwingUtilities.getWindowAncestor(this), "Editar Equipo", sel);
        dlg.setVisible(true);
        if (dlg.confirmed) {
            sendAndRefresh(new Request(Request.UPDATE_EQUIPO, dlg.getEquipo()), "Equipo actualizado");
        }
    }

    // ── Eliminar equipo ─────────────────────────────────────────────────────
    private void deleteEquipo() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un equipo para eliminar."); return; }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el equipo con ID " + id + "?", "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION)
            sendAndRefresh(new Request(Request.DELETE_EQUIPO, id), "Equipo eliminado");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private Equipo rowToEquipo(int row) {
        return new Equipo(
                (int)    model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (String) model.getValueAt(row, 3),
                (String) model.getValueAt(row, 4)
        );
    }

    private void sendAndRefresh(Request req, String okMsg) {
        if (!conn.isConnected()) { lblStatus.setText("Sin conexion con el servidor"); return; }
        try {
            Response resp = conn.send(req);
            if (resp.isSuccess()) { lblStatus.setText(okMsg); loadData(); }
            else                   JOptionPane.showMessageDialog(this, "Error: " + resp.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de comunicacion:\n" + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Diálogo de formulario
    // ═══════════════════════════════════════════════════════════════════════
    private class EquipoDialog extends JDialog {
        boolean confirmed = false;
        private final JTextField  txtNombre = new JTextField(20);
        private final JComboBox<String> cmbTipo       = new JComboBox<>(TIPOS);
        private final JTextField  txtMarca  = new JTextField(20);
        private final JComboBox<String> cmbCriticidad = new JComboBox<>(CRITICIDADES);
        private final int id;

        EquipoDialog(Window owner, String title, Equipo equipo) {
            super(owner, title, ModalityType.APPLICATION_MODAL);
            this.id = (equipo != null) ? equipo.getIdEquipo() : 0;
            if (equipo != null) {
                txtNombre.setText(equipo.getNombre());
                cmbTipo.setSelectedItem(equipo.getTipo());
                txtMarca.setText(equipo.getMarca());
                cmbCriticidad.setSelectedItem(equipo.getCriticidad());
            }
            JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
            form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            form.add(new JLabel("Nombre:"));    form.add(txtNombre);
            form.add(new JLabel("Tipo:"));      form.add(cmbTipo);
            form.add(new JLabel("Marca:"));     form.add(txtMarca);
            form.add(new JLabel("Criticidad:")); form.add(cmbCriticidad);

            JButton btnOk     = new JButton("Aceptar");
            JButton btnCancel = new JButton("Cancelar");
            btnOk.addActionListener(e -> {
                if (txtNombre.getText().trim().isEmpty() || txtMarca.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre y Marca son obligatorios.");
                    return;
                }
                confirmed = true; dispose();
            });
            btnCancel.addActionListener(e -> dispose());

            JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pnlBtn.add(btnOk); pnlBtn.add(btnCancel);

            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER); add(pnlBtn, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }

        Equipo getEquipo() {
            return new Equipo(id,
                    txtNombre.getText().trim(),
                    (String) cmbTipo.getSelectedItem(),
                    txtMarca.getText().trim(),
                    (String) cmbCriticidad.getSelectedItem());
        }
    }
}
