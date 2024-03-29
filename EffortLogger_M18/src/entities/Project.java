package entities;

import java.util.Date;

/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class Project {
	public Project(int iD, String name, String description, Date start_date, int story_points, int org_id,
			Date end_date) {
		super();
		ID = iD;
		this.name = name;
		this.description = description;
		this.start_date = start_date;
		this.story_points = story_points;
		this.org_id = org_id;
		this.end_date = end_date;
	}
	private int ID = -1;
	private String name;
	private String description;
	private Date start_date;
	private int story_points;
	private int org_id = -1;
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
	public Date getEndDate() {
		return end_date;
	}
	public void setEndDate(Date end_date) {
		this.end_date = end_date;
	}
	public Project(String name, String description, Date start_date, Date end_date, int s) {
		super();
		this.name = name;
		this.description = description;
		this.start_date = start_date;
		this.end_date = end_date;
		story_points = s;
	}
	public Project(int i, String name, String description, Date start_date, Date end_date, Date a, int s) {
		ID = i;
		this.name = name;
		this.description = description;
		this.start_date = start_date;
		this.end_date = end_date;
		actual_end_date = a;
		story_points = s;
	}
	public Project(int id2, String n, String d, Date sd, Date eed, Date ed, int sp, int oid) {
		ID = id2;
		name = n;
		description = d;
		sd = start_date;
		end_date = eed;
		actual_end_date = ed;
		story_points = sp;
		org_id = oid;
	}
	public String toString() {
		return "NAME: " + name + "\nSTART: " + start_date + "\nEND: " + end_date + "\nACTUAL: " + actual_end_date;
	}
	private Date actual_end_date;
	private Date end_date;
}
