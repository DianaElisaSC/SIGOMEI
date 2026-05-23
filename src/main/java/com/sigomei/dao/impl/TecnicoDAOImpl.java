package com.sigomei.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sigomei.dao.TecnicoDAO;
import com.sigomei.model.Tecnico;

public class TecnicoDAOImpl implements TecnicoDAO {

    private final Connection conn;

    public TecnicoDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void guardar(Tecnico t) {
        String sql = "INSERT INTO tecnico " +
                "(nombre_completo, rfc, telefono, correo, especialidad, " +
                "nivel_certificacion, fecha_ingreso, estatus) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getRfc());
            ps.setString(3, t.getTelefono());
            ps.setString(4, t.getCorreo());
            ps.setString(5, t.getEspecialidad());
            ps.setString(6, t.getNivelCertStr());
            ps.setDate(7, Date.valueOf(t.getFechaIngreso()));
            ps.setString(8, t.getEstatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setIdTecnico(rs.getInt(1));
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void actualizar(Tecnico t) {

        String sql = "UPDATE tecnico SET " +
                "nombre_completo=?, rfc=?, telefono=?, correo=?, " +
                "especialidad=?, nivel_certificacion=?, fecha_ingreso=?, estatus=? " +
                "WHERE id_tecnico=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getRfc());
            ps.setString(3, t.getTelefono());
            ps.setString(4, t.getCorreo());
            ps.setString(5, t.getEspecialidad());
            ps.setString(6, t.getNivelCertStr());
            ps.setDate(7, Date.valueOf(t.getFechaIngreso()));
            ps.setString(8, t.getEstatus());
            ps.setInt(9, t.getIdTecnico());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void eliminar(int idTecnico) {

        String sql = "DELETE FROM tecnico WHERE id_tecnico=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTecnico);

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Tecnico buscarPorId(int idTecnico) {

        String sql = "SELECT * FROM tecnico WHERE id_tecnico=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTecnico);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return null;
    }

    @Override
    public List<Tecnico> listar() {

        List<Tecnico> lista = new ArrayList<>();

        String sql = "SELECT * FROM tecnico ORDER BY id_tecnico";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return lista;
    }

    @Override
    public boolean tieneOrdenes(int idTecnico) {

        String sql = "SELECT COUNT(*) FROM orden_mantenimiento WHERE id_tecnico=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTecnico);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return false;
    }

    private Tecnico mapear(ResultSet rs) throws SQLException {

        Tecnico t = new Tecnico();

        t.setIdTecnico(rs.getInt("id_tecnico"));
        t.setNombre(rs.getString("nombre_completo"));
        t.setRfc(rs.getString("rfc"));
        t.setTelefono(rs.getString("telefono"));
        t.setCorreo(rs.getString("correo"));
        t.setEspecialidad(rs.getString("especialidad"));
        t.setNivelCertStr(rs.getString("nivel_certificacion"));

        Date fecha = rs.getDate("fecha_ingreso");

        if (fecha != null) {
            t.setFechaIngreso(fecha.toLocalDate());
        }

        t.setEstatus(rs.getString("estatus"));

        return t;
    }
}