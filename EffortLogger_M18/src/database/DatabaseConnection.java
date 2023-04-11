package database;

import java.sql.*;

public class DatabaseConnection {
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public DatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        // Load the MySQL JDBC driver class
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Open a connection to the database
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException {
        // Close the database connection
        if (connection != null) {
            connection.close();
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        // Execute an SQL query and return the result set
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public void executeUpdate(String sql) throws SQLException {
        // Execute an SQL update
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }
}
