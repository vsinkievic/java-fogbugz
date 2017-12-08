package org.paylogic.fogbugz;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that holds data from fogbugz case.
 * Interact with these objects using the FogbugzManager class.
 */
public class FogbugzCase {
    @Getter @Setter private int id;
    @Getter @Setter private String title;
    @Getter @Setter private int openedBy;
    @Getter @Setter private int assignedTo;
    @Getter @Setter private List<String> tags;
    @Getter @Setter private boolean isOpen;
    @Getter @Setter private String milestone;

    private int parentId;
    private int projectId;
    private String projectName;
    private String statusName;
    private BigDecimal hrsOrigEstimate;
    private BigDecimal hrsCurrEstimate;
    private BigDecimal hrsElapsed;
/*
 *     parent Long
    children String
    status String
    hrsOrigEst BigDecimal
    hrsCurrEst BigDecimal
    hrsElapsed BigDecimal

 */
    // Our custom fields. TODO: find nicer way to include custom fields.
    @Getter @Setter private String featureBranch;
    @Getter @Setter private String originalBranch;
    @Getter @Setter private String targetBranch;
    @Getter @Setter private String approvedRevision;
    @Getter @Setter private String ciProject;

    public FogbugzCase(int id, String title, int openedBy, int assignedTo,
                       List<String> tags, boolean isOpen, String featureBranch,
                       String originalBranch, String targetBranch, String approvedRevision, String ciProject,
                       String milestone) {
        this.id = id;
        this.title = title;
        this.openedBy = openedBy;
        this.assignedTo = assignedTo;
        this.tags = tags;
        this.isOpen = isOpen;
        this.featureBranch = featureBranch;
        this.originalBranch = originalBranch;
        this.targetBranch = targetBranch;
        this.milestone = milestone;
        this.approvedRevision = approvedRevision;
        this.ciProject = ciProject;
    }

    public FogbugzCase(int id, String title, int openedBy, int assignedTo,
                       String tags, boolean isOpen, String featureBranch,
                       String originalBranch, String targetBranch, String approvedRevision, String ciProject,
                       String milestone) {
        this(id, title, openedBy, assignedTo, tagsFromCSV(tags), isOpen, featureBranch, originalBranch,
                targetBranch, approvedRevision, ciProject, milestone);
    }

    /**
     * Load tags from String with CSV
     * @param tags A String with tags in CSV format.
     * @return Resulting list, which is also saved.
     */
    public static List<String> tagsFromCSV(String tags) {
        ArrayList<String> list = new ArrayList<String>();
        for (String tag: tags.split(",")) {
            list.add(tag);
        }
        return list;
    }

    /**
     * Create CSV String of tags.
     * @return String with tags, as CSV.
     */
    public String tagsToCSV() {
        return StringUtils.join(this.tags, ",");
    }

    /**
     * Add a tag for this case. Will not add when tag already exists in list.
     * @param tag
     */
    public void addTag(String tag) {
        if (!this.hasTag(tag)) {
            this.tags.add(tag);
        }
    }

    /**
     * Check if tag is in tags list.
     * @param tag
     * @return boolean indicating tag is in tags list or not.
     */
    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * Removes tag from list.
     * @param tag The tag to remove.
     */
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    /**
     * Assign case back to person who opened the case.
     */
    public void assignToOpener() {
        this.assignedTo = this.openedBy;
    }


    @Override
    public boolean equals(Object otherCase) {
        if (!(otherCase instanceof FogbugzCase)) {
            return false;
        }

        FogbugzCase o = (FogbugzCase) otherCase;

        if (
            this.id == o.id &&
            this.title.equals(o.title) &&
            this.openedBy == o.openedBy &&
            this.assignedTo == o.assignedTo &&
            this.tags.size() == o.tags.size() && this.tags.containsAll(o.tags) &&
            this.isOpen == o.isOpen &&
            Objects.equals(this.featureBranch, o.featureBranch) &&
            Objects.equals(this.originalBranch, o.originalBranch) &&
            Objects.equals(this.targetBranch, o.targetBranch) &&
            Objects.equals(this.milestone, o.milestone) &&
            Objects.equals(this.approvedRevision, o.approvedRevision) &&
            this.ciProject.equals(o.ciProject) &&
            this.projectId == o.projectId &&
            this.parentId == o.parentId &&
            Objects.equals(this.projectName, o.projectName) &&
            Objects.equals(this.statusName, o.statusName) &&
            Objects.equals(this.hrsOrigEstimate, o.hrsOrigEstimate) &&
            Objects.equals(this.hrsCurrEstimate, o.hrsCurrEstimate) &&
            Objects.equals(this.hrsElapsed, o.hrsElapsed)
        ) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
    	return String.format("{ case: %d, parentId: %d, projectId: %d, projectName: '%s', title: '%s', isOpen: '%b', statusName: '%s', assignedTo: %d, hrsOrigEst: %s, hrsCurrEst: %s, hrsElapsed: %s }", 
    					this.id, this.parentId, this.projectId, this.projectName, this.title, this.isOpen, this.statusName, this.assignedTo, this.hrsOrigEstimate, this.hrsCurrEstimate, this.hrsElapsed);
    }
    
    public int getProjectId() {
		return projectId;
	}
    
    public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
    
    public int getParentId() {
		return parentId;
	}
    
    public void setParentId(int parentId) {
		this.parentId = parentId;
	}
    
    public String getStatusName() {
		return statusName;
	}
    
    public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
    
    public void setHrsOrigEstimate(BigDecimal hrsOrigEstimate) {
		this.hrsOrigEstimate = hrsOrigEstimate;
	}
    
    public BigDecimal getHrsOrigEstimate() {
		return hrsOrigEstimate;
	}
    
    public void setHrsCurrEstimate(BigDecimal hrsCurrEstimate) {
		this.hrsCurrEstimate = hrsCurrEstimate;
	}
    
    public BigDecimal getHrsCurrEstimate() {
		return hrsCurrEstimate;
	}
    
    public void setHrsElapsed(BigDecimal hrsElapsed) {
		this.hrsElapsed = hrsElapsed;
	}
    
    public BigDecimal getHrsElapsed() {
		return hrsElapsed;
	}
    
    public String getProjectName() {
		return projectName;
	}
    
    public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
