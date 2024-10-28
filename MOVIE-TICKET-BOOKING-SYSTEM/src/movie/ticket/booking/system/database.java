/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.ticket.booking.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {
    public static Connection connectDb() {
        Connection connection = null;
        try {
            // Directly calling getConnection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviebook", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
        }
        
        return connection;
    }
}
