package database;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import entities.Employee;
import entities.EffortLog;
import entities.Manager;
import entities.Org;
import entities.Project;
import entities.Sprint;
import entities.Team;
import entities.UserStory;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.OrgExistsException;
import exceptions.OrgNotFoundException;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class DatabaseConnection {
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public DatabaseConnection() {
        this.url = System.getenv("URL");
        this.username = System.getenv("USERNAME");
        this.password = System.getenv("PASSWORD");
    }

    public void connect() throws SQLException, ClassNotFoundException {
        // Load the MySQL JDBC driver class
        Class.forName("com.mysql.cj.jdbc.Driver");
        String jdbcUrl = "jdbc:mysql://" + url + ":" + 3306 + "/" + "effort_logger" + "?user=" + username + "&password=" + password;
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
    /**
     * 
     * @return - list of all orgs available to join
     * @throws SQLException - something went wrong during an SQL query
     */
    public ArrayList<Org> getOrgs() throws SQLException {
		ResultSet result = executeQuery("SELECT * FROM orgs");
		ArrayList<Org> list = new ArrayList<>();
		while(result.next()) {
			int id = result.getInt("id");
			String name = result.getString("name");
			String description = result.getString("description");
			float avg_velocity = result.getFloat("avg_velocity");
			list.add(new Org(id, name, description, avg_velocity));
		}
		return list;
    }
    /**
     * Gets all the orgs an employee is a part of
     * @param e - the employee to get the orgs from (cannot be null)
     * @return - a list of orgs that this employee is a part of
     * @throws InvalidInputException - one of the inputs is invalid
     * @throws SQLException - something went wrong during an SQL query
     * @throws OrgNotFoundException - when getting an org from employee_orgs, we encountered an ID which does not exist in orgs
     */
    public ArrayList<Org> getOrgs(Employee e) throws InvalidInputException, SQLException, OrgNotFoundException {
    	if(e == null || e.getID() == -1) throw new InvalidInputException();
    	PreparedStatement get_org_ids = connection.prepareStatement("SELECT org_id FROM employee_orgs WHERE employee_id = ?");
    	get_org_ids.setInt(1, e.getID());
    	ResultSet orgs = get_org_ids.executeQuery();
    	ArrayList<Org> employee_orgs = new ArrayList<>();
    	while(orgs.next()) {
    		PreparedStatement get_org = connection.prepareStatement("SELECT * FROM orgs WHERE id = ?");
    		int org_id = orgs.getInt("org_id");
    		get_org.setInt(1, org_id);
    		ResultSet org_info = get_org.executeQuery();
    		if(!org_info.next()) throw new OrgNotFoundException();
    		String name = org_info.getString("name");
    		String description = org_info.getString("description");
    		int id = org_info.getInt("id");
    		employee_orgs.add(new Org(id, name, description));
    	}
    	return employee_orgs;
    }
    /**
     * Create a new team given the currently logged in manager, and a list of employees on the team. (you can get all employees for the currently selected org via connection.getOrgEmployees()
     * @param manager - the manager who is creating the team
     * @param employees - a list of employees selected by the manager to be on the team
     * @return - returns a new Team object if successfully created
     * @throws InvalidInputException
     * @throws SQLException 
     */
    public Team createTeam(String team_name, Manager manager, Org current_org, ArrayList<Employee> employees) throws InvalidInputException, SQLException {
    	if(team_name == null || team_name.isBlank() || team_name.isEmpty() || current_org == null || manager == null || employees == null || employees.size() == 0) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement create_user = connection.prepareStatement("INSERT INTO teams (name, manager, org_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    	create_user.setString(1, team_name);
    	create_user.setInt(2, manager.getID());
    	create_user.setInt(3, current_org.getID());
    	int rows_affected = create_user.executeUpdate();
    	if(rows_affected != 1) {
    		throw new SQLException();
    	}
    	ResultSet generated_keys = create_user.getGeneratedKeys();
    	if(!generated_keys.next()) {
    		throw new SQLException();
    	}
    	int id = generated_keys.getInt("id");
    	return new Team(id, current_org, team_name, manager, employees);
    }
    /**
     * Gets all employees from a specified org
     * @param o - the org to get all employees from (cannot be null)
     * @return - a list of employees for this org 
     * @throws SQLException - something went wrong during an SQL query
     * @throws InvalidInputException - one of the inputs is invalid
     * @throws UserNotFoundException - when getting one of the employees from the list of employee_orgs, we found an ID that does not exist in employees
     */
    public ArrayList<Employee> getOrgEmployees(Org o) throws SQLException, InvalidInputException, UserNotFoundException {
    	if(o == null) throw new InvalidInputException();
		PreparedStatement get_employees = connection.prepareStatement("SELECT employee_id FROM employee_orgs WHERE org_id = ?");
		get_employees.setInt(1, o.getID());
		ResultSet result = get_employees.executeQuery();
		ArrayList<Employee> list = new ArrayList<>();
		while(result.next()) {
			int id = result.getInt("employee_id");
			PreparedStatement get_employee = connection.prepareStatement("SELECT name, user_name, is_manager FROM employees WHERE id = ?");
			get_employee.setInt(1, id);
			ResultSet employee = get_employee.executeQuery();
			if(!employee.next()) throw new UserNotFoundException();
			String name = employee.getString("name");
			String username = employee.getString("user_name");
			boolean is_manager = employee.getBoolean("is_manager");
			if(is_manager) {
				list.add(new Manager(id, name, username));
			} else {
				list.add(new Employee(id, name, username));
			}
		}
		return list;
    }
    /**
     * Signs the user in given the username and plain text password
     * @param username - the inputed username (cannot be null, empty, or blank)
     * @param password - the inputed password (cannot be null, empty, or blank)
     * @return - an employee object (all fields set)
     * @throws UserNotFoundException - employee with this username was not found
     * @throws InvalidInputException - one of the inputs is invalid
     * @throws SQLException - something went wrong during an SQL query
     * @throws IncorrectPasswordException - inputed password did not match the hashed password from the database
     */
    public Employee signIn(String username, String password) throws UserNotFoundException, InvalidInputException, SQLException, IncorrectPasswordException {
    	if(username == null || username.isBlank() || username.isEmpty() || password == null || password.isBlank() || password.isEmpty()) {
    		throw new InvalidInputException();
    	}
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
    	if(is_manager) {
			new Manager(id, name, user_name);
		}
    	return new Employee(id, name, user_name);
    }
    /**
     * Creates new employee entry in employees
     * @param name - the full name of the employee (cannot be null, empty, or blank)
     * @param username - the selected username of the user (cannot be null, empty, or blank)
     * @param password - the chosen (and confirmed) password of the user (cannot be null, empty, or blank)
     * @param is_manager - whether they are a manager or not (must be either true or false)
     * @param orgs - a list of orgs to add the user to (can be empty)
     * @return - a new employee object with the generated keys (like ID)
     * @throws UsernameTakenException - cannot create users with same username
     * @throws SQLException - something went wrong during an SQL query
     * @throws InvalidInputException - one of the inputs is invalid
     */
    public Employee signUp(String name, String username, String password, boolean is_manager, ArrayList<Org> orgs) throws UsernameTakenException, SQLException, InvalidInputException {
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
    		int rows_affected_ = assign_user_to_org.executeUpdate();
    		if(rows_affected_ != 1) {
    			//COULD NOT ADD USER TO ORG
    		}
    	}
    	if(is_manager) {
			new Manager(id, name, username);
		}
    	return new Employee(id, name, username);
    }
    /**
     * 
     * @param name - the org's name (cannot be null, empty, or blank)
     * @param description - the org's description (cannot be null)
     * @param code - the org's code (cannot be null, empty, or blank)
     * @return - an Org object that holds the created org's data
     * @throws InvalidInputException - one of the inputs does not follow the rules above
     * @throws SQLException - something went wrong during an SQL query
     * @throws OrgExistsException - an Org with this name already exists
     */
    public Org createOrg(String name, String description, String code) throws InvalidInputException, SQLException, OrgExistsException {
    	if(name == null || name.isBlank() || name.isEmpty() || description == null || code == null || code.isBlank() || code.isEmpty()) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement name_taken = connection.prepareStatement("SELECT * FROM orgs WHERE name = ?");
    	name_taken.setString(1, username);
    	ResultSet rs = name_taken.executeQuery();
    	//We cannot add duplicate names into orgs
    	if(rs.next()) {
    		throw new OrgExistsException();
    	}
    	String hashed_code = Hasher.hashPassword(code);
    	PreparedStatement create_org = connection.prepareStatement("INSERT INTO orgs (name, description, code) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    	create_org.setString(1, name);
    	create_org.setString(2, description);
    	create_org.setString(3, hashed_code);
    	int rows_affected = create_org.executeUpdate();
    	int id = -1;
    	if(rows_affected == 1) {
    		ResultSet generated_keys = create_org.getGeneratedKeys();
    		if(!generated_keys.next()) throw new SQLException();
    		id = generated_keys.getInt(1);
    		return new Org(id, name, description, -1);
    	}
    	throw new SQLException();
    }
    
    /**
     * 
     * @param o - the org to add the employee to
     * @param e - the employee to add
     * @param inputed_code - the code that the user inputed
     * @return boolean - indicates whether the user was added to the org or not (will probably throw an exception if not)
     * @throws SQLException - thrown if something went wrong during an SQL query
     * @throws OrgNotFoundException - thrown if the org does not exist
     * @throws IncorrectPasswordException - thrown if the user entered an incorrect password
     */
    public boolean addUserToOrg(Org o, Employee e, String inputed_code) throws SQLException, OrgNotFoundException, IncorrectPasswordException {
    	PreparedStatement check_code = connection.prepareStatement("SELECT code FROM orgs WHERE name = ?");
    	check_code.setString(1, o.getName());
    	ResultSet r = check_code.executeQuery();
    	if(!r.next()) {
    		throw new OrgNotFoundException();
    	}
    	String hashed_code = r.getString("code");
    	if(!Hasher.matches(inputed_code, hashed_code)) throw new IncorrectPasswordException();
    	PreparedStatement assign_user_to_org = connection.prepareStatement("INSERT INTO employee_orgs (org_id, employee_id) VALUES (?, ?)");
    	assign_user_to_org.setInt(1, o.getID());
    	assign_user_to_org.setInt(2, e.getID());
    	int ra = assign_user_to_org.executeUpdate();
    	if(ra != 1) {
    		throw new SQLException();
    	}
		return true;
    }
    /**
     * Formats java.util.Date into the database's date format
     * @param d - the date to format
     * @return - the properly formatted string
     */
    public String formatDate(Date d) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(d);
	}
    //*********************************************************
    //------------------UNIMPLEMENTED METHODS------------------
    //*********************************************************
    
    public void createUserStory(String name, String description, Date start_date, Date est_end_date,Manager e, Org o, Project p) {
    	
    }
    //assign a user story to a project
    public void assignUserStory(Project p, UserStory u) {
    	
    }
    //assign user story to a team's backlog
    public void assignUserStory(Team t, Manager m, UserStory u) {
    	
    }
    //assign a user story to a sprint (will update this user story's assigned_to_sprint to true, and will set the est_end_date to the sprint's end date
    public void assignUserStory(Sprint p, Manager m, UserStory u) {
    	
    }
    public ArrayList<UserStory> getProductBacklog(Team t) {
    	return null;
    }
    //logs effort to the specified user story
    public void logEffort(EffortLog l, Employee e, UserStory u) {
    	
    }
}
