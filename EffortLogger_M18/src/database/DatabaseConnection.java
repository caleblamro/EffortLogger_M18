package database;

import java.sql.*;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Employee;
import entities.Log;
import entities.Org;
import entities.Project;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;

public class DatabaseConnection {
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public DatabaseConnection() {
        this.url = "database-1.c7qrin1yhkuw.us-east-2.rds.amazonaws.com";
        this.username = "admin";
        this.password = "effortloggerm18";
    }

    public void connect() throws SQLException, ClassNotFoundException {
        // Load the MySQL JDBC driver class
        Class.forName("com.mysql.cj.jdbc.Driver");
        String jdbcUrl = "jdbc:mysql://" + url + ":" + 3306 + "/" + "effort_logger" + "?user=" + username + "&password=" + password;
        System.out.println("URL: " + jdbcUrl);
        // Open a connection to the database
        connection = DriverManager.getConnection(jdbcUrl);
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
    
    public ArrayList<Org> getOrgs() throws SQLException {
		ResultSet result = executeQuery("SELECT * FROM orgs");
		ArrayList<Org> list = new ArrayList<>();
		while(result.next()) {
			int id = result.getInt("id");
			String name = result.getString("name");
			String description = result.getString("description");
			String code = result.getString("code");
			float avg_velocity = result.getFloat("avg_velocity");
			list.add(new Org(id, name, code, description, avg_velocity));
		}
		return list;
    }
    
    public Employee signIn(String username, String password) throws UserNotFoundException, InvalidInputException, SQLException, IncorrectPasswordException {
    	if(username == null || username.isBlank() || username.isEmpty() || password == null || password.isBlank() || password.isEmpty()) {
    		throw new InvalidInputException();
    	}
    	//defend against SQL injection
    	PreparedStatement query = connection.prepareStatement("SELECT * FROM employees WHERE user_name = ?");
    	query.setString(1, username);
		ResultSet result = query.executeQuery();
		String received_hash = "";
		if(result.next()) {
			received_hash = result.getString("hashed_password");
		}else {
			throw new UserNotFoundException();
		}
		if(!Hasher.matches(password, received_hash)) {
			throw new IncorrectPasswordException();
		}
    	int id = result.getInt("id");
    	String name = result.getString("name");
    	String user_name = result.getString("user_name");
    	boolean is_manager = result.getBoolean("is_manager");
    	return new Employee(id, name, user_name, is_manager);
    }
    
    public Employee signUp(String name, String username, String password, boolean is_manager, ArrayList<Org> orgs) throws UsernameTakenException, SQLException, InvalidInputException, UserNotFoundException {
    	if(username == null || username.isBlank() || username.isEmpty() || password == null || password.isBlank() || password.isEmpty() || name == null || name.isBlank() || name.isEmpty()) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement username_taken = connection.prepareStatement("SELECT * FROM employees WHERE user_name = ?");
    	username_taken.setString(1, username);
    	ResultSet result = username_taken.executeQuery();
    	if(result.next()) {
    		throw new UsernameTakenException();
    	}
    	String hashed_password = Hasher.hashPassword(password);
    	PreparedStatement create_user = connection.prepareStatement("INSERT INTO employees (name, user_name, hashed_password, is_manager) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    	create_user.setString(1, name);
    	create_user.setString(2, username);
    	create_user.setString(3, hashed_password);
    	create_user.setBoolean(4, is_manager);
    	int rows_affected = create_user.executeUpdate();
    	int id = -1;
    	if(rows_affected == 1) {
    		ResultSet generated_keys = create_user.getGeneratedKeys();
    		if(generated_keys.next()) {
    			id = generated_keys.getInt(1);
    		}
    	}else {
    		throw new SQLException();
    	}
    	for(Org o : orgs) {
        	PreparedStatement assign_user_to_org = connection.prepareStatement("INSERT INTO employee_orgs (org_id, employee_id) VALUES (?, ?)");
        	assign_user_to_org.setInt(1, o.getID());
        	assign_user_to_org.setInt(2, id);
    		int ra = assign_user_to_org.executeUpdate();
    		if(ra != 1) {
    			//COULD NOT ADD USER TO ORG
    		}
    	}
    	return new Employee(id, name, username, is_manager);
    }
    
    public void createOrg(Org o) {
    	
    }
    
    public void logEffort(Log l) {
    	
    }
    
    public boolean addUserToOrg(Org o, Employee e) throws SQLException {
    	PreparedStatement assign_user_to_org = connection.prepareStatement("INSERT INTO employee_orgs (org_id, employee_id) VALUES (?, ?)");
    	assign_user_to_org.setInt(1, o.getID());
    	assign_user_to_org.setInt(2, e.getID());
    	int ra = assign_user_to_org.executeUpdate();
    	if(ra != 1) {
    		throw new SQLException();
    	}
		return true;
    }
    
    
}
