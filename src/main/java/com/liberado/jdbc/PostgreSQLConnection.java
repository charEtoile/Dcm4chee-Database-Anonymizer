package com.liberado.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
    https://jdbc.postgresql.org/documentation/84/load.html
 */

/**
 * Created by admin on 21/06/2017.
 */
public class PostgreSQLConnection{

    /**
     * Connection URL
     */
    private static String url = "jdbc:postgresql://localhost:5432/pacsdb";

    /**
     * Database user
     */
    private static String user = "pacs";

    /**
     * Database user password
     */
    private static String passwd = "pacs";

    /**
     * Connection object
     */
    private static Connection connect = null;

    /**
     * Instanciates of returns a database connection
     * @return An single instance of the database connection
     */
    public static Connection getInstance(){
        if(connect == null){
            try {
                // Implicitly load the driver
                Class.forName("org.postgresql.Driver");
                connect = DriverManager.getConnection(url, user, passwd);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return connect;
    }
}