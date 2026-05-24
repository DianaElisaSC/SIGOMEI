package com.sigomei.client;

import com.sigomei.model.Tecnico;
import com.sigomei.protocol.Request;
import com.sigomei.protocol.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la entidad Tecnico.
 */
public class TecnicoPanel extends JPanel {

    private static final String[] COLUMNS =
            {"ID", "Nombre", "RFC", "Especialidad", "Estatus", "Nivel Cert."};

    private static final String[] ESPECIALIDADES = {"Electrico", "Mecanico", "Instrumentacion", "Hidraulico"};
    private static final String[] ESTATUS        = {"Activo", "Inactivo"};
    private static final Integer[] NIVELES       = {1, 2, 3};

    private final ServerConnection  conn;
    private final DefaultTableModel model;
    private final JTable  table;
    private final JLabel  lblStatus;

    public TecnicoPanel(ServerConnection conn) {
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
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        lblStatus = new JLabel("Listo");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.ITALIC, 11f));

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnNuevo      = new JButton("Nuevo");
        JButton btnEditar     = new JButton("Editar");
        JButton btnEliminar   = new JButton("Eliminar");

        btnActualizar.addActionListener(e -> loadData());
        btnNuevo     .addActionListener(e -> addTecnico());
        btnEditar    .addActionListener(e -> editTecnico());
        btnEliminar  .addActionListener(e -> deleteTecnico());

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlTop.add(btnActualizar); pnlTop.add(lblStatus);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlBot.add(btnNuevo); pnlBot.add(btnEditar); pnlBot.add(btnEliminar);

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
            Response resp = conn.send(new Request(Request.LIST_TECNICOS));
            if (resp.isSuccess()) {
                List<Tecnico> lista = (List<Tecnico>) resp.getData();
                for (Tecnico t : lista)
                    model.addRow(new Object[]{t.getIdTecnico(), t.getNombre(), t.getRfc(),
                            t.getEspecialidad(), t.getEstatus(), t.getNivelCertificacion()});
                lblStatus.setText(lista.size() + " tecnicos cargados");
            } else {
                lblStatus.setText("Error: " + resp.getMessage());
            }
        } catch (Exception ex) {
            lblStatus.setText("Error de comunicacion: " + ex.getMessage());
        }
    }

    private void addTecnico() {
        TecnicoDialog dlg = new TecnicoDialog(SwingUtilities.getWindowAncestor(this), "Nuevo Tecnico", null);
        dlg.setVisible(true);
        if (dlg.confirmed) sendAndRefresh(new Request(Request.ADD_TECNICO, dlg.getTecnico()), "Tecnico registrado");
    }

    private void editTecnico() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un tecnico para editar."); return; }
        TecnicoDialog dlg = new TecnicoDialog(SwingUtilities.getWindowAncestor(this), "Editar Tecnico", rowToTecnico(row));
        dlg.setVisible(true);
        if (dlg.confirmed) sendAndRefresh(new Request(Request.UPDATE_TECNICO, dlg.getTecnico()), "Tecnico actualizado");
    }

    private void deleteTecnico() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un tecnico para eliminar."); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar tecnico con ID " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            sendAndRefresh(new Request(Request.DELETE_TECNICO, id), "Tecnico eliminado");
    }

    private Tecnico rowToTecnico(int row) {
        Tecnico t = new Tecnico(
                (int)    model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 3),
                (String) model.getValueAt(row, 4),
                (int)    model.getValueAt(row, 5)
        );
        t.setRfc((String) model.getValueAt(row, 2));
        return t;
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
    private class TecnicoDialog extends JDialog {
        boolean confirmed = false;
        private final JTextField         txtNombre   = new JTextField(20);
        private final JComboBox<String>  cmbEspec    = new JComboBox<>(ESPECIALIDADES);
        private final JComboBox<String>  cmbEstatus  = new JComboBox<>(ESTATUS);
        private final JComboBox<Integer> cmbNivel    = new JComboBox<>(NIVELES);
        private final JTextField         txtRfc      = new JTextField(12);
        private final int id;

        TecnicoDialog(Window owner, String title, Tecnico t) {
            super(owner, title, ModalityType.APPLICATION_MODAL);
            this.id = (t != null) ? t.getIdTecnico() : 0;
            if (t != null) {
                txtNombre.setText(t.getNombre());
                cmbEspec.setSelectedItem(t.getEspecialidad());
                cmbEstatus.setSelectedItem(t.getEstatus());
                cmbNivel.setSelectedItem(t.getNivelCertificacion());
                txtRfc.setText(t.getRfc());
            }
            JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
            form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            form.add(new JLabel("Nombre:"));      form.add(txtNombre);
            form.add(new JLabel("Especialidad:")); form.add(cmbEspec);
            form.add(new JLabel("Estatus:"));      form.add(cmbEstatus);
            form.add(new JLabel("Nivel Cert.:"));  form.add(cmbNivel);
            form.add(new JLabel("RFC:"));         form.add(txtRfc);

            JButton ok = new JButton("Aceptar"), cancel = new JButton("Cancelar");
            ok.addActionListener(e -> {
                if (txtNombre.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return;
                }
                if (txtRfc.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El RFC es obligatorio."); return;
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

        Tecnico getTecnico() {
            Tecnico t = new Tecnico(id, txtNombre.getText().trim(),
                    (String)  cmbEspec.getSelectedItem(),
                    (String)  cmbEstatus.getSelectedItem(),
                    (Integer) cmbNivel.getSelectedItem());
            t.setRfc(txtRfc.getText().trim());
            return t;
        }
    }
}
