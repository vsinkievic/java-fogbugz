package org.paylogic.fogbugz;

import lombok.Getter;

/**
 * Represents a Fogbugz Milestone (FixFor)
 */
public class FogbugzMilestone {
    @Getter private int id;
    @Getter private String name;
    @Getter private boolean isDeleted;
    @Getter private boolean isReallyDeleted;

    public FogbugzMilestone(int id, String name, boolean isDeleted, boolean isReallyDeleted) {
        this.id = id;
        this.name = name;
        this.isDeleted = isDeleted;
        this.isReallyDeleted = isReallyDeleted;
    }
}
