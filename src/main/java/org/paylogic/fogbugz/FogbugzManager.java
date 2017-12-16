package org.paylogic.fogbugz;

import java.time.LocalDate;
import java.util.List;

/**
 * Manager for FogbugzCase objects. Use this to retrieve, save and create cases.
 */
public interface FogbugzManager {


    /**
     * Retrieves a case using the Fogbugz API by caseId.
     * @param id the id of the case to fetch.
     * @return FogbugzCase if all is well, else null.
     */
    public FogbugzCase getCaseById(int id) throws InvalidResponseException, NoSuchCaseException;

    /**
     * Retrieves cases using the Fogbugz API by a query
     * @param query fogbugz search query
     * @return List of cases
     */
    public List<FogbugzCase> searchForCases(String query) throws InvalidResponseException, NoSuchCaseException;

    public List<FogbugzProject> getFogbugzProjects() throws InvalidResponseException;

    /**
     * Retrieves all events for a certain case.
     * @param id Case id to fetch events from
     * @return list of FogbugzEvents
     */
    public List<FogbugzEvent> getEventsForCase(int id);

    /**
     * Loop through all FogbugzEvent for given case id, and return last (in time) with assignment to gatekeepers.
     * @param caseId
     * @return Last event with gatekeeper assignment or null.
     */
    public FogbugzEvent getLastAssignedToGatekeepersEvent(int caseId);

    /**
     * Loop through all FogbugzEvent for given case id, and return last (in time) with assignment to given user.
     * @param caseId
     * @param userId
     * @return Last event with user assignment or null.
     */
    public FogbugzEvent getLastAssignedTo(int caseId, int userId);

    /**
     * Saves a case to fogbugz using its API.
     * Supports creating new cases, by setting caseId to 0 on case object.
     * @param fbCase The case to save.
     * @param comment A message to pass for this edit.
     * @return boolean, true if all is well, else false.
     */
    public boolean saveCase(FogbugzCase fbCase, String comment);

    /**
     * Additional save method that does not propagate a comment.
     * @param fbCase The case to save.
     * @return boolean, true if all is well, else false.
     */
    public boolean saveCase(FogbugzCase fbCase);

    /**
     * Assign case to mergekeepers user id. Note: does not save case.
     * @param fbCase the case to set assignedTo on.
     * @return modified case.
     */
    public FogbugzCase assignToMergekeepers(FogbugzCase fbCase);

    /**
     * Assign the given case to gatekeepers (uses GatekeeperUserID from global settings)
     * @param fbCase The case to edit.
     * @return modified case.
     */
    public FogbugzCase assignToGatekeepers(FogbugzCase fbCase);

    public FogbugzUser getFogbugzUser(int ix);
    
    public List<FogbugzUser> getFogbugzUsers();

    /**
     * Retrieves all milestones and returns them in a nice List of FogbugzMilestone objects.
     * @return list of FogbugzMilestones
     */
    public List<FogbugzMilestone> getMilestones();

    /**
     * Creates new Milestone in Fogbugz. Please leave id of milestone object empty.
     * Only creates global milestones.
     * @param milestone to edit/create
     */
    public boolean createMilestone(FogbugzMilestone milestone);

    public boolean createMilestoneIfNotExists(String milestoneName);
    
    public List<FogbugzTimeinterval> getTimeintervals(int caseId) throws InvalidResponseException;

    public List<FogbugzTimeinterval> getTimeintervals(LocalDate from, LocalDate till) throws InvalidResponseException; 
    
    public List<FogbugzTimeinterval> getTimeintervals(int userId, LocalDate from, LocalDate till) throws InvalidResponseException;
    
}
