package org.paylogic.fogbugz;

public class FogbugzProject {
	private int id;
	private String name;
	private boolean isDeleted = false;
	
	public FogbugzProject(int id, String name, boolean isDeleted) {
		this.id = id;
		this.name = name;
		this.isDeleted = isDeleted;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	@Override
	public String toString() {
		return String.format("{ id: %d, name: '%s', isDeleted: %b }", this.id, this.name, this.isDeleted);
	}
	
}
