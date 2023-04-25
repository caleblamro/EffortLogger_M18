package entities;

/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class Employee {
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		this.ID = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean is_manager() {
		return is_manager;
	}
	protected void setIs_manager(boolean is_manager) {
		this.is_manager = is_manager;
	}
	@Override
	public String toString() {
		return "\tID: " + getID() + "\n\tNAME: " + getName() + "\n\tIS_MANAGER: " + is_manager();
	}
	
	private int ID = -1;
	private String name;
	private String username;
	private boolean is_manager;
	public Employee(int id, String name, String username, boolean is_manager) {
		this.ID = id;
		this.name = name;
		this.username = username;
		this.is_manager = is_manager;
	}
}
