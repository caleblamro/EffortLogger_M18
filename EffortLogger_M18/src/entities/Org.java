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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public float getAvg_velocity() {
		return avg_velocity;
	}

	public void setAvg_velocity(float avg_velocity) {
		this.avg_velocity = avg_velocity;
	}
	private int ID;
	private String name;
	private String description;
	private String code;
	private float avg_velocity;
	
	public Org(String name, String description, String code) {
		
	}
	
	public Org(int iD, String name, String code, String description, float avg_velocity) {
		ID = iD;
		this.name = name;
		this.code = code;
		this.avg_velocity = avg_velocity;
		this.description = description;
	}
	public void initialize(DatabaseConnection db) {
		db.createOrg(this);
	}
}
