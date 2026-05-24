package com.sigomei.dao.impl;

import com.sigomei.dao.EquipoDAO;
import com.sigomei.model.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAOImpl implements EquipoDAO {

    private final Connection conn;

    public EquipoDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void guardar(Equipo e) {

        String sql = "INSERT INTO equipo_industrial " +
                "(nombre, tipo, marca, modelo, numero_serie, ubicacion_planta, " +
                "fecha_instalacion, estado_operativo, criticidad) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTipo());
            ps.setString(3, e.getMarca());
            ps.setString(4, e.getModelo());
            ps.setString(5, e.getNumeroSerie());
            ps.setString(6, e.getUbicacionPlanta());
            setDateOrNull(ps, 7, e.getFechaInstalacion());
            ps.setString(8, estadoOrDefault(e.getEstadoOperativo()));
            ps.setString(9, e.getCriticidad());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdEquipo(rs.getInt(1));
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void actualizar(Equipo e) {

        String sql =
                "UPDATE equipo_industrial SET " +
                "nombre=?, tipo=?, marca=?, modelo=?, numero_serie=?, " +
                "ubicacion_planta=?, fecha_instalacion=?, estado_operativo=?, " +
                "criticidad=? WHERE id_equipo=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTipo());
            ps.setString(3, e.getMarca());
            ps.setString(4, e.getModelo());
            ps.setString(5, e.getNumeroSerie());
            ps.setString(6, e.getUbicacionPlanta());
            setDateOrNull(ps, 7, e.getFechaInstalacion());
            ps.setString(8, estadoOrDefault(e.getEstadoOperativo()));
            ps.setString(9, e.getCriticidad());
            ps.setInt(10, e.getIdEquipo());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void eliminar(int idEquipo) {

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM equipo_industrial WHERE id_equipo=?")) {

            ps.setInt(1, idEquipo);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Equipo buscarPorId(int idEquipo) {

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM equipo_industrial WHERE id_equipo=?")) {

            ps.setInt(1, idEquipo);

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
    public List<Equipo> listar() {

        List<Equipo> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs =
                     st.executeQuery(
                             "SELECT * FROM equipo_industrial ORDER BY id_equipo")) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return lista;
    }

    @Override
    public List<Equipo> buscarPorNombreOSerie(String termino) {

        List<Equipo> lista = new ArrayList<>();

        String sql =
                "SELECT * FROM equipo_industrial " +
                "WHERE nombre LIKE ? OR numero_serie LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            String p = "%" + termino + "%";

            ps.setString(1, p);
            ps.setString(2, p);

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

    @Override
    public boolean tieneOrdenes(int idEquipo) {

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM orden_mantenimiento WHERE id_equipo=?")) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Equipo mapear(ResultSet rs) throws SQLException {

        Equipo e = new Equipo();

        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setNombre(rs.getString("nombre"));
        e.setTipo(rs.getString("tipo"));
        e.setMarca(rs.getString("marca"));
        e.setModelo(rs.getString("modelo"));
        e.setNumeroSerie(rs.getString("numero_serie"));
        e.setUbicacionPlanta(rs.getString("ubicacion_planta"));

        Date fecha = rs.getDate("fecha_instalacion");

        if (fecha != null) {
            e.setFechaInstalacion(fecha.toLocalDate());
        }

        e.setEstadoOperativo(rs.getString("estado_operativo"));
        e.setCriticidad(rs.getString("criticidad"));

        return e;
    }

    private void setDateOrNull(PreparedStatement ps, int index, java.time.LocalDate value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private String estadoOrDefault(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return "Activo";
        }
        return estado;
    }
}
