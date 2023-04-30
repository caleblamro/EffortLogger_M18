package entities;

public class Defect {
	private int ID = -1;
	private String description;
	private boolean is_fixed;
	private int project_id;
	private int effort_log_id;
	public Defect(int iD, String description, boolean is_fixed, int project_id, int el) {
		super();
		ID = iD;
		this.description = description;
		this.is_fixed = is_fixed;
		this.project_id = project_id;
		effort_log_id = el;
	}
	public String getDescription() {
		return description;
	}
	public int getID() {
		return ID;
	}
	public boolean isFixed() {
		return is_fixed;
	}
	public int getProjectId() {
		return project_id;
	}
	public int getEffortLogID() {
		return effort_log_id;
	}
	
}
