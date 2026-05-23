package com.sigomei.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConexionBD {

    private static String url;
    private static String user;
    private static String password;

    static {

        try {

            Properties props = new Properties();

            InputStream input =
                    ConexionBD.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error cargando configuracion BD",
                    e
            );
        }
    }

    public static Connection conectar() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    url,
                    user,
                    password
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al conectar",
                    e
            );
        }
    }
}