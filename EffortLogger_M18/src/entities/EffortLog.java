package entities;

import java.sql.Date;

public class EffortLog {
	private int ID = -1;
	private int employee_id;
	private String description;
	private int time_worked_sec;
	private Date date;
	public EffortLog(int iD, int employee_id, String description, int time_worked_sec, Date date) {
		super();
		ID = iD;
		this.employee_id = employee_id;
		this.description = description;
		this.time_worked_sec = time_worked_sec;
		this.date = date;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getEmployeeId() {
		return employee_id;
	}
	public void setEmployeeId(int employee_id) {
		this.employee_id = employee_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTimeWorked() {
		return time_worked_sec;
	}
	public void setTimeWorked(int time_worked_sec) {
		this.time_worked_sec = time_worked_sec;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
