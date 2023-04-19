package entities;

import database.DatabaseConnection;

public class Org {
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getAvg_velocity() {
		return avg_velocity;
	}

	public void setAvg_velocity(float avg_velocity) {
		this.avg_velocity = avg_velocity;
	}
	private int ID = -1;
	private String name;
	private String description;
	private float avg_velocity;
	
	public Org(String name, String description, String code) {
		
	}
	
	public Org(int iD, String name, String description, float avg_velocity) {
		ID = iD;
		this.name = name;
		this.description = description;
		this.avg_velocity = avg_velocity;
	}
	public Org(int id, String name, String description) {
		ID = id;
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		return "\tID: " + getID() + "\n\tNAME: " + getName() + "\n\tDESCRIPTION: " + getDescription();
	}
	public void initialize(DatabaseConnection db) {
	}
}
