package org.paylogic.fogbugz;

import java.time.ZonedDateTime;

public class FogbugzTimeinterval {
	private int id;
	private int caseId;
	private int personId;
	private boolean isDeleted = false;
	private ZonedDateTime from;
	private ZonedDateTime till;
	
	public FogbugzTimeinterval(int id, int caseId, int personId, boolean isDeleted, ZonedDateTime from, ZonedDateTime till) {
		this.id = id;
		this.caseId = caseId;
		this.personId = personId;
		this.isDeleted = isDeleted;
		this.from = from;
		this.till = till;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCaseId() {
		return caseId;
	}
	
	public int getPersonId() {
		return personId;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public ZonedDateTime getFrom() {
		return from;
	}
	
	public ZonedDateTime getTill() {
		return till;
	}
	
	@Override
	public String toString() {
		return String.format("{ id: %d, caseId: %d, personId: %d, isDeleted: %b, from: '%s', till: '%s' }",  this.id, this.caseId, this.personId, this.isDeleted, this.from, this.till);
	}
}
