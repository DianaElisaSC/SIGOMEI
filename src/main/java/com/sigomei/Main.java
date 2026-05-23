package com.sigomei;

import com.sigomei.config.ConexionBD;
import com.sigomei.dao.EquipoDAO;
import com.sigomei.dao.impl.EquipoDAOImpl;
import com.sigomei.model.Equipo;

import java.sql.Connection;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Connection conn = ConexionBD.conectar();

        EquipoDAO dao = new EquipoDAOImpl(conn);

        List<Equipo> equipos = dao.listar();

        for (Equipo e : equipos) {

            System.out.println(
                    e.getIdEquipo() + " - " +
                    e.getNombre() + " - " +
                    e.getMarca()
            );
        }
    }
}