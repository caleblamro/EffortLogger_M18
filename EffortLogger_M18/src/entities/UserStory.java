package entities;

import java.util.Date;

/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class UserStory {
	int ID = -1;
	int project_id = -1;
	boolean assigned_to_sprint = false;
	String name;
	String description;
	Date start_date;
	Date actual_end_date;
	Date est_end_date = null;
	int story_points;
	public UserStory(int project_id, boolean assigned_to_sprint, String name, String description, Date start_date,
			Date est_end_date, int story_points) {
		this.project_id = project_id;
		this.assigned_to_sprint = assigned_to_sprint;
		this.name = name;
		this.description = description;
		this.start_date = start_date;
		this.est_end_date = est_end_date;
		this.story_points = story_points;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getProjectId() {
		return project_id;
	}
	public void setProjectId(int project_id) {
		this.project_id = project_id;
	}
	public boolean isAssignedToSprint() {
		return assigned_to_sprint;
	}
	public void setAssignedToSprint(boolean assigned_to_sprint) {
		this.assigned_to_sprint = assigned_to_sprint;
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