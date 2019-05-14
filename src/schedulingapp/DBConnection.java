/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author brandon <bcjames035@gmail.com>
 */
public class DBConnection {
    private static final String DB_NAME = "U05aBh";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + DB_NAME;
    private static final String USERNAME = "U05aBh";
    private static final String PASSWORD = "53688447769";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
   
    public static Connection conn;
    
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
    
    public static void makeConnection() throws ClassNotFoundException, SQLException, Exception
    {
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        System.out.println("Connection successful.");
    }
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception
    {
        conn.close();
        System.out.println("Connection closed.");
    }
}
