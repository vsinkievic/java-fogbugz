package org.paylogic.fogbugz;

import lombok.Getter;

import java.util.Date;

public class FogbugzEvent implements Comparable<FogbugzEvent> {

    @Getter private final int id;
    @Getter private final int caseId;
    @Getter private final String verb;
    @Getter private final int person;
    @Getter private final int personAssignedTo;
    @Getter private final Date dateTimeStamp;
    @Getter private final String description;
    @Getter private final String sPerson;

    public FogbugzEvent(int id, int caseId, String verb, int person, int personAssignedTo, Date dateTimeStamp, String description, String sPerson) {
        this.id = id;
        this.caseId = caseId;
        this.verb = verb;
        this.person = person;
        this.personAssignedTo = personAssignedTo;
        this.dateTimeStamp = dateTimeStamp;
        this.description = description;
        this.sPerson = sPerson;
    }

    public int compareTo(FogbugzEvent o) {
        return this.dateTimeStamp.compareTo(o.getDateTimeStamp());
    }

    public String toString() {
        return this.description;
    }

    public String formatNicely() {
        return "Event " + Integer.toString(this.id) + " for case " + Integer.toString(this.caseId) + ":\n" +
                "Verb:\t" + this.verb + "\n" +
                "Person:\t" + Integer.toString(this.person) + "\n" +
                "AssignedTo:\t" + Integer.toString(this.personAssignedTo) + "\n" +
                "Description:\t" + this.description + "\n" +
                "sPerson:\t" + this.sPerson + "\n";
    }
}

