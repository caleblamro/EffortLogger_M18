package entities;

import java.util.Date;

public class Sprint {
	private int ID = -1;
	private String name;
	private String description;
	private Date start_date;
	private Date est_end_date;
	private Date actual_end_date;
	private int story_points;
	
	public Sprint(int iD, String name, String description, Date start_date, Date est_end_date, int story_points) {
		super();
		ID = iD;
		this.name = name;
		this.description = description;
		this.start_date = start_date;
		this.est_end_date = est_end_date;
		this.story_points = story_points;
	}
	public int getID() {
		return ID;
	}
	public void setID(int i) {
		ID = i;
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
	public Date getStartDate() {
		return start_date;
	}
	public void setStartDate(Date start_date) {
		this.start_date = start_date;
	}
	public Date getActualEndDate() {
		return actual_end_date;
	}
	public void setActualEndDate(Date actual_end_date) {
		this.actual_end_date = actual_end_date;
	}
	public Date getEstEndDate() {
		return est_end_date;
	}
	public void setEstEndDate(Date est_end_date) {
		this.est_end_date = est_end_date;
	}
	public int getStoryPoints() {
		return story_points;
	}
	public void setStoryPoints(int story_points) {
		this.story_points = story_points;
	}
}
