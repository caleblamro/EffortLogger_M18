package entities;

public class Manager extends Employee {

	public Manager(int id, String name, String username) {
		super(id, name, username);
		setIs_manager(true);
	}

}
