package entities;

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
	public boolean isIs_manager() {
		return is_manager;
	}
	public void setIs_manager(boolean is_manager) {
		this.is_manager = is_manager;
	}
	private int ID;
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
