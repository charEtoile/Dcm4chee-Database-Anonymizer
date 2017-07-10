package com.liberado.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    private static String user = "postgres";

    /**
     * Database user password
     */
    private static String passwd = "postgres";

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

    public static List<String> getTables() {
        connect = PostgreSQLConnection.getInstance();
        List<String> listOfTables = new ArrayList<String>();
        String schemaName = "public";
        String tableType[] = {"TABLE"};

        DatabaseMetaData md = null;
        try {
            md = connect.getMetaData();
            ResultSet rs = md.getTables(null, schemaName, null, tableType);
            while (rs.next()) {
                listOfTables.add(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfTables;
    }

    public static int countInTable(String tableName) {
        int count = -1;

        if (tableName != null && !tableName.isEmpty()) {
            try {
                ResultSet result = PostgreSQLConnection.getInstance().createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)
                        .executeQuery("SELECT COUNT(*) FROM " + tableName);
                if (result.first()) {
                    count = result.getInt("COUNT");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}