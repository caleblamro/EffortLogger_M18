package database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import application.Main;
import entities.Employee;
import entities.Defect;
import entities.EffortLog;
import entities.Org;
import entities.Project;
import entities.Team;
import entities.UserStory;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.NotManagerException;
import exceptions.OrgExistsException;
import exceptions.OrgNotFoundException;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;

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
			int units = result.getInt("days_per_story_point");
			list.add(new Org(id, name, description, avg_velocity, units));
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
			int units = org_info.getInt("days_per_story_point");
    		employee_orgs.add(new Org(id, name, description, (float) 0,units));
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
     * @throws UserNotFoundException 
     */
    public Team createTeam(String team_name, String description, Employee manager, Org current_org, ArrayList<Employee> employees) throws InvalidInputException, SQLException, UserNotFoundException {
    	if(team_name == null || team_name.isBlank() || team_name.isEmpty() || current_org == null || manager == null || employees == null || employees.size() == 0 || !manager.is_manager()) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement create_user = connection.prepareStatement("INSERT INTO teams (name, manager, org_id, description) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    	create_user.setString(1, team_name);
    	create_user.setInt(2, manager.getID());
    	create_user.setInt(3, current_org.getID());
    	create_user.setString(4, description);
    	int rows_affected = create_user.executeUpdate();
    	if(rows_affected != 1) {
    		throw new SQLException();
    	}
    	ResultSet generated_keys = create_user.getGeneratedKeys();
    	if(!generated_keys.next()) {
    		throw new SQLException();
    	}
    	int id = generated_keys.getInt(1);
    	for(Employee e : employees) {
    		PreparedStatement add_to_team = connection.prepareStatement("INSERT INTO team_employees (team_id, employee_id) VALUES (?,?)");
    		int emp_id = e.getID();
    		if(emp_id == -1) throw new UserNotFoundException();
    		add_to_team.setInt(1, id);
    		add_to_team.setInt(2, emp_id);
    		int ra = add_to_team.executeUpdate();
    		if(ra != 1) throw new SQLException();
    	}
    	return new Team(id, current_org, team_name, description, manager, employees);
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
			list.add(new Employee(id, name, username, is_manager));
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
    	int is_manager_i = result.getInt("is_manager");
    	boolean is_manager = is_manager_i == 1 ? true : false;
    	return new Employee(id, name, user_name, is_manager);
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
    	create_user.setInt(4, (is_manager ? 1 : 0));
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
    	return new Employee(id, name, username, is_manager);
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
    public Org createOrg(String name, String description, String code, int days_per_point) throws InvalidInputException, SQLException, OrgExistsException {
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
    	PreparedStatement create_org = connection.prepareStatement("INSERT INTO orgs (name, description, code, days_per_story_point) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    	create_org.setString(1, name);
    	create_org.setString(2, description);
    	create_org.setString(3, hashed_code);
    	create_org.setInt(4, days_per_point);
    	int rows_affected = create_org.executeUpdate();
    	int id = -1;
    	if(rows_affected == 1) {
    		ResultSet generated_keys = create_org.getGeneratedKeys();
    		if(!generated_keys.next()) throw new SQLException();
    		id = generated_keys.getInt(1);
    		return new Org(id, name, description, -1, days_per_point);
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
     * Creates a new user story given the user inputs
     * @param name - inputed name for the user story
     * @param description - description for user story
     * @param start_date - est start date for the user story
     * @param est_end_date - est end date for the user story
     * @param e - the manager who owns this user story
     * @param p - the project that this user story is assigned to
     * @param story_points - number of story points estimated
     * @param t - the team that will handle the user story
     * @return - new userstory object with generated fields
     * @throws InvalidInputException
     * @throws SQLException
     */
    public UserStory createUserStory(String name, String description, Date start_date, Date est_end_date, Employee e, Project p, int story_points, Team t) throws InvalidInputException, SQLException {
    	if(name == null || name.isBlank() || name.isEmpty() || description == null || description.isEmpty() || description.isBlank() || start_date == null || est_end_date == null || e == null || p == null || t == null || p.getID() == -1 || t.getID() == -1) {
    		throw new InvalidInputException();
    	}
    	//insert new project
    	PreparedStatement s = connection.prepareStatement("INSERT INTO user_stories (name, description, start_date, est_end_date, owner_id, project_id, story_points, assigned_to_sprint, org_id, team_id) VALUES (?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
    	s.setString(1, name);
    	s.setString(2, description);
    	s.setDate(3, start_date);
    	s.setDate(4, est_end_date);
    	s.setInt(5, e.getID());
    	s.setInt(6, p.getID());
    	s.setInt(7, story_points);
    	s.setBoolean(8, false);
    	int org_id = Main.getCurrentOrg().getID();
    	if(org_id == -1) {
    		throw new SQLException();
    	}
    	s.setInt(9, org_id);
    	s.setInt(10, t.getID());
    	int rows_affected = s.executeUpdate();
    	if(rows_affected != 1) {
    		throw new SQLException();
    	}
		ResultSet generated_keys = s.getGeneratedKeys();
    	if(!generated_keys.next()) throw new SQLException();
    	int generated_user_story_id = generated_keys.getInt(1);
    	return new UserStory(p.getID(), false, name, description, start_date, est_end_date, story_points, org_id, generated_user_story_id, t.getID());
    }

    public ArrayList<Project> getProjects(Org o) throws InvalidInputException, SQLException {
    	if(o == null || o.getID() == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM projects WHERE org_id = ?");
    	s.setInt(1, o.getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<Project> result = new ArrayList<>();
    	while(r.next()) {
    		int id = r.getInt("project_id");
    		String name = r.getString("name");
    		String description = r.getString("description");
    		Date start = r.getDate("start_date");
    		Date end = r.getDate("est_end_date");
    		Date actual_end = r.getDate("actual_end_date");
    		int points = r.getInt("story_points");
    		Project p = new Project(id, name, description, start, end, actual_end, points);
    		result.add(p);
    	}
    	return result;
    }
    
    public Employee getEmployee(int employee_id) throws InvalidInputException, SQLException, UserNotFoundException {
    	if(employee_id == -1) throw new InvalidInputException();
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM employees WHERE id = ?");
    	s.setInt(1, employee_id);
    	ResultSet r = s.executeQuery();
    	if(!r.next()) {
    		throw new UserNotFoundException();
    	}
		String name = r.getString("name");
		String username = r.getString("user_name");
    	int is_manager_i = r.getInt("is_manager");
    	boolean is_manager = is_manager_i == 1 ? true : false;
    	return new Employee(employee_id, name, username, is_manager);
		
    }
    
    public Org getOrg(int org_id) throws InvalidInputException, SQLException, UserNotFoundException {
    	if(org_id == -1) throw new InvalidInputException();
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM orgs WHERE id = ?");
    	s.setInt(1, org_id);
    	ResultSet r = s.executeQuery();
    	if(!r.next()) {
    		throw new UserNotFoundException();
    	}
		String name = r.getString("name");
		String description = r.getString("description");
		int id = r.getInt("id");
		return new Org(id, name, description);
		
    }
    
    public ArrayList<Employee> getTeamEmployees(int team_id) throws InvalidInputException, SQLException, UserNotFoundException {
    	if(team_id == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT employee_id FROM team_employees WHERE team_id = ?");
    	s.setInt(1, team_id);
    	ResultSet r = s.executeQuery();
    	ArrayList<Employee> emps = new ArrayList<>();
    	while(r.next()) {
    		int emp_id = r.getInt("employee_id");
    		emps.add(getEmployee(emp_id));
    	}
    	return emps;
    }

	public ArrayList<Team> getTeams(Org o) throws InvalidInputException, SQLException, UserNotFoundException {
		if(o == null || o.getID() == -1) {
			throw new InvalidInputException();
		}
		PreparedStatement s = connection.prepareStatement("SELECT * FROM teams WHERE org_id = ?");
		s.setInt(1, o.getID());
		ResultSet rows = s.executeQuery();
		ArrayList<Team> t = new ArrayList<>();
		while(rows.next()) {
			String name = rows.getString("name");
			String description = rows.getString("description");
			int id = rows.getInt("id");
			int manager_id = rows.getInt("manager");
			ArrayList<Employee> emps = getTeamEmployees(id);
			Employee manager = getEmployee(manager_id);
			t.add(new Team(id, o, name, description, manager, emps));
		}
		return t;
	}
	
	public ArrayList<Team> getTeams(Employee e) throws InvalidInputException, SQLException, UserNotFoundException, NotManagerException {
		Org o = Main.getCurrentOrg();
		if(e == null || e.getID() == -1 || o == null || o.getID() == -1) {
			throw new InvalidInputException();
		}
		if(!e.is_manager()) {
			throw new NotManagerException();
		}
		PreparedStatement s = connection.prepareStatement("SELECT * FROM teams WHERE manager = ?");
		s.setInt(1, e.getID());
		ResultSet rows = s.executeQuery();
		ArrayList<Team> t = new ArrayList<>();
		while(rows.next()) {
			String name = rows.getString("name");
			String description = rows.getString("description");
			int id = rows.getInt("id");
			ArrayList<Employee> emps = getTeamEmployees(id);
			Team ta =new Team(id, o, name, description, e, emps);
			t.add(ta);
		}
		return t;
	}
	public ArrayList<Team> getEmployeeTeams() throws InvalidInputException, SQLException, UserNotFoundException {
		if(Main.getCurrentUser() == null || Main.getCurrentUser().getID() == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT (team_id) FROM team_employees WHERE employee_id = ?");
    	s.setInt(1, Main.getCurrentUser().getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<Integer> team_ids = new ArrayList<>();
    	while(r.next()) {
    		int team_id = r.getInt(1);
    		team_ids.add(team_id);
    	}
    	ArrayList<Team> result = new ArrayList<>();
    	for (int i = 0; i < team_ids.size(); i++) {
        	PreparedStatement statement = connection.prepareStatement("SELECT * FROM teams WHERE id = ?");
    	    statement.setInt(1, team_ids.get(i));
    	    ResultSet rows = statement.executeQuery();
    	    if(!rows.next()) throw new SQLException();
			String name = rows.getString("name");
			String description = rows.getString("description");
			int id = rows.getInt("id");
			Team ta =new Team(id, Main.getCurrentOrg(), name, description, null, null);
    		result.add(ta);
    	}
    	return result;
	}
    
    public Project createProject(String name, String description, Date start_date, Date est_end_date, int est_story_points) throws InvalidInputException, SQLException {
    	if(name == null || name.isBlank() || name.isEmpty() || start_date == null || est_end_date == null || description == null) { 
    		throw new InvalidInputException();
    	}
    	PreparedStatement p = connection.prepareStatement("INSERT INTO projects (name, start_date, est_end_date, story_points, org_id, description) VALUES (?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
    	p.setString(1, name);
    	p.setDate(2, start_date);
    	p.setDate(3, est_end_date);
    	p.setInt(4, est_story_points);
    	int org_id = Main.getCurrentOrg().getID();
    	if(org_id == -1) {
    		throw new SQLException();
    	}
    	p.setInt(5, org_id);
    	p.setString(6, description);
    	int rows_affected = p.executeUpdate();
    	if(rows_affected != 1) {
    		throw new SQLException();
    	}
    	ResultSet generated_keys = p.getGeneratedKeys();
    	if(!generated_keys.next()) {
    		throw new SQLException();
    	}
    	int id = generated_keys.getInt(1);
    	return new Project(id, name, description, start_date, est_end_date, null, est_story_points);
    }
    
    public ArrayList<UserStory> getUserStories(Org o) throws InvalidInputException, SQLException {
    	if(o == null || o.getID() == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM user_stories WHERE org_id = ?");
    	s.setInt(1, o.getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<UserStory> result = new ArrayList<>();
    	while(r.next()) {
    		int id = r.getInt("id");
    		int assigned_to_sprint = r.getInt("assigned_to_sprint");
    		String name = r.getString("name");
    		String description = r.getString("description");
    		Date start_date = r.getDate("start_date");
    		Date est_end_date = r.getDate("est_end_date");
    		int story_points = r.getInt("story_points");
    		int project_id = r.getInt("project_id");
    		int org_id = r.getInt("org_id");
    		int team_id = r.getInt("team_id");
        	result.add(new UserStory(project_id, assigned_to_sprint == 0 ? false : true, name, description, start_date, est_end_date, story_points, org_id, id, team_id));
    	}
    	return result;
     }
    public Team getTeam(int id) throws InvalidInputException, SQLException, UserNotFoundException {
    	if(id == -1 ) throw new InvalidInputException();
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM teams WHERE id = ?");
    	s.setInt(1, id);
    	ResultSet r = s.executeQuery();
    	if(!r.next()) throw new SQLException();
		String name = r.getString("name");
		String description = r.getString("description");
		int manager_id = r.getInt("manager");
		int org_id = r.getInt("org_id");
		ArrayList<Employee> emps = getTeamEmployees(id);
		Employee manager = getEmployee(manager_id);
		return new Team(id, getOrg(org_id), name, description, manager, emps);
    	
    }
    public ArrayList<UserStory> getUserStories(Employee e) throws InvalidInputException, SQLException {
    	if(e == null || e.getID() == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM user_stories WHERE owner_id = ?");
    	s.setInt(1, e.getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<UserStory> result = new ArrayList<>();
    	while(r.next()) {
    		int id = r.getInt("id");
    		String name = r.getString("name");
    		String description = r.getString("description");
    		Date start_date = r.getDate("start_date");
    		Date est_end_date = r.getDate("est_end_date");
    		Date actual_end_date = r.getDate("actual_end_date");
    		int story_points = r.getInt("story_points");
    		int project_id = r.getInt("project_id");
    		int org_id = r.getInt("org_id");
    		int team_id = r.getInt("team_id");
        	result.add(new UserStory(id, project_id, false, name, description, start_date, actual_end_date, est_end_date, story_points, org_id, team_id));
    	}
    	return result;
    }
    public ArrayList<UserStory> getActiveUserStories(Employee e) throws InvalidInputException, SQLException {
    	if(e == null || e.getID() == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM user_stories WHERE owner_id = ? AND actual_end_date IS NULL");
    	s.setInt(1, e.getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<UserStory> result = new ArrayList<>();
    	while(r.next()) {
    		int id = r.getInt("id");
    		String name = r.getString("name");
    		String description = r.getString("description");
    		Date start_date = r.getDate("start_date");
    		Date est_end_date = r.getDate("est_end_date");
    		Date actual_end_date = r.getDate("actual_end_date");
    		int story_points = r.getInt("story_points");
    		int project_id = r.getInt("project_id");
    		int org_id = r.getInt("org_id");
    		int team_id = r.getInt("team_id");
        	result.add(new UserStory(id, project_id, false, name, description, start_date, actual_end_date, est_end_date, story_points, org_id, team_id));
    	}
    	return result;
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
    public void completeProjects(ArrayList<Project> ps) throws InvalidInputException, SQLException {
    	if(ps == null) throw new InvalidInputException();
    	for(Project p : ps) {
        	PreparedStatement s = connection.prepareStatement("DELETE FROM projects WHERE project_id = ?");
        	if(p.getID() == -1) throw new InvalidInputException();
        	s.setInt(1, p.getID());
        	int ra = s.executeUpdate();
        	if(ra != 1) throw new SQLException();
    	}
     }
    public void addEmployeeToTeam(Employee e, Team t) {
    	
    }
    public void completeUserStory(UserStory u) {
    	
    }
    public ArrayList<UserStory> getProductBacklog(Team t) {
    	//get all the user stories that haven't been assigned to a sprint
    	return null;
    }
    //logs effort to the specified user story
    public void logEffort(EffortLog l, Employee e, UserStory u) {
    	
    }
    public ArrayList<Defect> getActiveDefects() throws InvalidInputException, SQLException{
    	if(Main.getCurrentOrg() == null || Main.getCurrentOrg().getID() == -1 ) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement s = connection.prepareStatement("SELECT (project_id) FROM projects WHERE org_id = ?");
    	s.setInt(1, Main.getCurrentOrg().getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<Integer> project_ids = new ArrayList<>();
    	while(r.next()) {
    		int project_id = r.getInt(1);
    		project_ids.add(project_id);
    	}
    	String sql = "SELECT * FROM defects WHERE project_id IN (";
    	for (int i = 0; i < project_ids.size(); i++) {
    	    if (i > 0 && i != project_ids.size() - 1) {
    	        sql += ", ";
    	    }
    	    sql += "?";
    	}
    	sql += ") AND is_fixed = 0";

    	// Prepare a PreparedStatement object with the SQL query
    	PreparedStatement statement = connection.prepareStatement(sql);

    	// Set the values of the project ids in the IN clause
    	for (int i = 0; i < project_ids.size(); i++) {
    	    statement.setInt(i + 1, project_ids.get(i));
    	}
    	ArrayList<Defect> result = new ArrayList<>();
    	ResultSet r1 = statement.executeQuery();
    	while (r1.next()) {
    		int id = r1.getInt("id");
    		String d = r1.getString("description");
    		int pid = r1.getInt("project_id");
    		int eid = r1.getInt("effort_log_id");
    		result.add(new Defect(id, d, false, pid, eid));
    	}
    	return result;
    }
    public Defect createDefect(String description, Project p, UserStory e) throws InvalidInputException, SQLException {
    	if(description == null || description.isEmpty() || description.isBlank() || p == null || e == null || e.getID() == -1) {
    		throw new InvalidInputException();
    	}
    	PreparedStatement st = connection.prepareStatement("INSERT INTO defects (description, project_id, user_story_id) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
    	st.setString(1, description);
    	st.setInt(2, p.getID());
    	st.setInt(3, e.getID());
    	int ra = st.executeUpdate();
    	if(ra != 1) throw new SQLException();
    	ResultSet r = st.getGeneratedKeys();
    	if(!r.next()) throw new SQLException();
    	int id = r.getInt(1);
    	return new Defect(id, description, false, p.getID(), e.getID());
    }
    public Project getProject(int id) throws SQLException {
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM projects WHERE project_id = ?");
    	s.setInt(1, id);
    	ResultSet r = s.executeQuery();
    	if(!r.next()) throw new SQLException();
    	String n = r.getString("name");
    	String d = r.getString("description");
    	Date sd = r.getDate("start_date");
    	Date eed = r.getDate("est_end_date");
    	Date ed = r.getDate("actual_end_date");
    	int sp = r.getInt("story_points");
    	int oid = r.getInt("org_id");
    	return new Project(id, n, d, sd, eed, ed, sp, oid);
    }
    public boolean completeUserStories(ArrayList<UserStory> us) throws SQLException {
    	if(us == null) return false;
    	Date d = new Date(System.currentTimeMillis());
    	for(UserStory u : us) {
    		if(u.getID() == -1) continue;
    		PreparedStatement s = connection.prepareStatement("UPDATE user_stories SET actual_end_date = ? WHERE id = ?");
    		s.setDate(1, d);
    		s.setInt(2, u.getID());
    		int ra = s.executeUpdate();
    		if(ra != 1) throw new SQLException();
    	}
    	return true;
    }
    public ArrayList<UserStory> getTeamUserStories() throws InvalidInputException, SQLException{
    	if(Main.getCurrentTeam() == null || Main.getCurrentTeam().getID() == -1) throw new InvalidInputException();
    	PreparedStatement s = connection.prepareStatement("SELECT * FROM user_stories WHERE team_id = ? AND actual_end_date IS NULL");
    	s.setInt(1, Main.getCurrentTeam().getID());
    	ResultSet r = s.executeQuery();
    	ArrayList<UserStory> result = new ArrayList<>();
    	while(r.next()) {
    		int id = r.getInt("id");
    		String name = r.getString("name");
    		String description = r.getString("description");
    		Date start_date = r.getDate("start_date");
    		Date est_end_date = r.getDate("est_end_date");
    		Date actual_end_date = r.getDate("actual_end_date");
    		int story_points = r.getInt("story_points");
    		int project_id = r.getInt("project_id");
    		int org_id = r.getInt("org_id");
    		int team_id = r.getInt("team_id");
        	result.add(new UserStory(id, project_id, false, name, description, start_date, actual_end_date, est_end_date, story_points, org_id, team_id));
    	}
    	return result;
    }
}
