package com.sigomei.dao.impl;

import com.sigomei.dao.OrdenDAO;
import com.sigomei.model.Orden;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAOImpl implements OrdenDAO {

    private final Connection conn;

    public OrdenDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void guardar(Orden orden) {
        String sql = "INSERT INTO orden_mantenimiento " +
                "(descripcion, estado, id_equipo, id_tecnico, " +
                "fecha_programada, fecha_inicio, fecha_cierre) " +
                "VALUES (?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, orden.getDescripcion());
            ps.setString(2, orden.getEstado());
            ps.setInt(3, orden.getIdEquipo());
            ps.setInt(4, orden.getIdTecnico());
            setDateOrNull(ps, 5, orden.getFechaProgramada());
            setDateOrNull(ps, 6, orden.getFechaInicio());
            setDateOrNull(ps, 7, orden.getFechaCierre());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    orden.setIdOrden(rs.getInt(1));
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void actualizar(Orden orden) {
        String sql = "UPDATE orden_mantenimiento SET " +
                "descripcion=?, estado=?, id_equipo=?, id_tecnico=?, " +
                "fecha_programada=?, fecha_inicio=?, fecha_cierre=? " +
                "WHERE id_orden=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, orden.getDescripcion());
            ps.setString(2, orden.getEstado());
            ps.setInt(3, orden.getIdEquipo());
            ps.setInt(4, orden.getIdTecnico());
            setDateOrNull(ps, 5, orden.getFechaProgramada());
            setDateOrNull(ps, 6, orden.getFechaInicio());
            setDateOrNull(ps, 7, orden.getFechaCierre());
            ps.setInt(8, orden.getIdOrden());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void eliminar(int idOrden) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM orden_mantenimiento WHERE id_orden=?")) {

            ps.setInt(1, idOrden);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Orden buscarPorId(int idOrden) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM orden_mantenimiento WHERE id_orden=?")) {

            ps.setInt(1, idOrden);

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
    public List<Orden> listar() {
        return listarPorQuery("SELECT * FROM orden_mantenimiento ORDER BY id_orden");
    }

    @Override
    public List<Orden> listarPorEquipo(int idEquipo) {
        return listarPorQuery("SELECT * FROM orden_mantenimiento WHERE id_equipo=? ORDER BY id_orden", idEquipo);
    }

    @Override
    public List<Orden> listarPorTecnico(int idTecnico) {
        return listarPorQuery("SELECT * FROM orden_mantenimiento WHERE id_tecnico=? ORDER BY id_orden", idTecnico);
    }

    @Override
    public List<Orden> listarPorEstado(String estado) {
        return listarPorQuery("SELECT * FROM orden_mantenimiento WHERE estado=? ORDER BY id_orden", estado);
    }

    @Override
    public boolean existeOrdenActivaParaEquipo(int idEquipo) {
        String sql = "SELECT COUNT(*) FROM orden_mantenimiento WHERE id_equipo=? " +
                "AND (estado='Programada' OR estado='En ejecucion')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Orden> listarPorQuery(String sql, Object... params) {
        List<Orden> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return lista;
    }

    private Orden mapear(ResultSet rs) throws SQLException {
        Orden o = new Orden();

        o.setIdOrden(rs.getInt("id_orden"));
        o.setIdEquipo(rs.getInt("id_equipo"));
        o.setIdTecnico(rs.getInt("id_tecnico"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setEstado(rs.getString("estado"));

        Date programada = rs.getDate("fecha_programada");
        if (programada != null) {
            o.setFechaProgramada(programada.toLocalDate());
        }

        Date inicio = rs.getDate("fecha_inicio");
        if (inicio != null) {
            o.setFechaInicio(inicio.toLocalDate());
        }

        Date cierre = rs.getDate("fecha_cierre");
        if (cierre != null) {
            o.setFechaCierre(cierre.toLocalDate());
        }

        return o;
    }

    private void setDateOrNull(PreparedStatement ps, int index, java.time.LocalDate value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }
}
