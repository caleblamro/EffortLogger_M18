package entities;

import java.util.ArrayList;

public class Team {
	public Team(int iD, Org org, String name, String d, Employee manager, ArrayList<Employee> employees) {
		ID = iD;
		this.org = org;
		this.manager = manager;
		this.employees = employees;
		this.name = name;
		this.description = d;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public Org getOrg() {
		return org;
	}
	public void setOrg(Org org) {
		this.org = org;
	}
	public Employee getManager() {
		return manager;
	}
	public void setManager(Employee manager) {
		this.manager = manager;
	}
	public ArrayList<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(ArrayList<Employee> employees) {
		this.employees = employees;
	}
	public String getDescription() {
		return description;
	}
	int ID = -1;
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name + " (ID: "+ ID + ")";
	}
	String description;
	Org org;
	Employee manager;
	ArrayList<Employee> employees;
}
