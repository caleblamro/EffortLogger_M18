package entities;

import java.util.ArrayList;

public class Team {
	public Team(int iD, Org org, String name, Manager manager, ArrayList<Employee> employees) {
		ID = iD;
		this.org = org;
		this.manager = manager;
		this.employees = employees;
		this.name = name;
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
	public Manager getManager() {
		return manager;
	}
	public void setManager(Manager manager) {
		this.manager = manager;
	}
	public ArrayList<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(ArrayList<Employee> employees) {
		this.employees = employees;
	}
	int ID = -1;
	String name;
	Org org;
	Manager manager;
	ArrayList<Employee> employees;
}
