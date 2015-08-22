package org.paylogic.fogbugz;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
        String csv = "";
        for (String tag: this.tags) {
            csv += tag + ",";
        }
        if (csv.length() > 0) {
            // Remove trailing comma
            csv = csv.substring(0, csv.length()-1);
        }
        return csv;
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
            this.featureBranch.equals(o.featureBranch) &&
            this.originalBranch.equals(o.originalBranch) &&
            this.targetBranch.equals(o.targetBranch) &&
            this.milestone.equals(o.milestone) &&
            this.approvedRevision.equals(o.approvedRevision) &&
            this.ciProject.equals(o.ciProject)
        ) {
            return true;
        } else {
            return false;
        }
    }
}
