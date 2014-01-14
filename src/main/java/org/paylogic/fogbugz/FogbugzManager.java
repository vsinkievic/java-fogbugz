package org.paylogic.fogbugz;

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * Manager for FogbugzCase objects. Use this to retrieve, save and create cases.
 */
@Log
public class FogbugzManager {

    private String url;
    private String token;
    @Getter private String featureBranchFieldname;
    @Getter private String originalBranchFieldname;
    @Getter private String targetBranchFieldname;
    @Getter private String approvedRevisionFieldname;
    @Getter private int mergekeeperUserId;
    @Getter private int gatekeeperUserId;

    /**
     * Constructor of FogbugzManager.
     */
    public FogbugzManager(String url, String token, @Nullable String featureBranchFieldname,
                          @Nullable String originalBranchFieldname, @Nullable String targetBranchFieldname,
                          @Nullable String approvedRevisionFieldname, int mergekeeperUserId, int gatekeeperUserId) {

        this.url = url;
        this.token = token;
        this.mergekeeperUserId = mergekeeperUserId;
        this.gatekeeperUserId = gatekeeperUserId;

        // If user does not want custom fields ignore them.
        if (featureBranchFieldname != null) {
            this.featureBranchFieldname = featureBranchFieldname;
        } else {
            this.featureBranchFieldname = "";
        }
        if (originalBranchFieldname != null) {
            this.originalBranchFieldname = originalBranchFieldname;
        } else {
            this.originalBranchFieldname = "";
        }
        if (targetBranchFieldname != null) {
            this.targetBranchFieldname = targetBranchFieldname;
        } else {
            this.targetBranchFieldname = "";
        }
        if (approvedRevisionFieldname != null) {
            this.approvedRevisionFieldname = approvedRevisionFieldname;
        } else {
            this.approvedRevisionFieldname = "";
        }
    }

    /**
     * Helper method to create basic URL with authentication token in it.
     * @return String with basic URL
     */
    private String getFogbugzUrl() {
        return this.url + "api.asp?token=" + this.token;
    }

    /**
     * Helper method to create API url from Map, with proper encoding.
     * @param params Map with parameters to encode.
     * @return String which represents API URL.
     */
    private String mapToFogbugzUrl(Map<String, String> params) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for (String key: params.keySet()) {
            String value = params.get(key);
            if (!value.isEmpty()) {
                paramList.add(new BasicNameValuePair(key, value));
            }
        }
        String output = this.getFogbugzUrl() + "&" + URLEncodedUtils.format(paramList, '&', "UTF-8");
        FogbugzManager.log.info("Generated URL to send to Fogbugz: " + output);
        return output;
    }

    /**
     * Fetches the XML from the Fogbugz API and returns a Document object
     * with the response XML in it, so we can use that.
     */
    private Document getFogbugzDocument(Map<String, String> parameters) throws IOException, ParserConfigurationException, SAXException {
        URL uri = new URL(this.mapToFogbugzUrl(parameters));
        HttpURLConnection con = (HttpURLConnection) uri.openConnection();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(con.getInputStream());
    }

    /**
     * Retrieves a case using the Fogbugz API by caseId.
     * @param id the id of the case to fetch.
     * @return FogbugzCase if all is well, else null.
     */
    public FogbugzCase getCaseById(int id) throws InvalidResponseException, NoSuchCaseException {
        HashMap params = new HashMap();  // Hashmap defaults to <String, String>
        params.put("cmd", "search");
        params.put("q", Integer.toString(id));
        params.put("cols", "ixBug,tags,fOpen,sTitle,sFixFor,ixPersonOpenedBy,ixPersonAssignedTo" + // No trailing comma
                                  this.getCustomFieldsCSV());

        Document doc = null;
        try {
            doc = this.getFogbugzDocument(params);
        } catch (Exception e) {
            throw new InvalidResponseException(e.getMessage());
        }

        // Check for case count in cases tag, so we know wheter to parse the response ever further or not.
        int caseCount = 0;
        try {
            Node casesContainer = doc.getElementsByTagName("cases").item(0);
            caseCount = Integer.parseInt(casesContainer.getAttributes().getNamedItem("count").getTextContent());
        } catch (NumberFormatException e) {
            log.log(Level.INFO, "No valid number in case count XML response.", e);
        }
        if (caseCount < 1) {
            throw new NoSuchCaseException("Fogbugz did not return a case for case id " + Integer.toString(id));
        }

        // Collect tags, and put them in list so we can work with them in a nice way.
        List<String> tags = new ArrayList();
        NodeList tagNodeList = doc.getElementsByTagName("tag");
        if (tagNodeList != null && tagNodeList.getLength() != 0) {
            for (int i = 0; i< tagNodeList.getLength(); i++) {
                tags.add(tagNodeList.item(i).getTextContent());
            }
        }

        // Construct case object from retrieved data.
        return new FogbugzCase(
                id,
                doc.getElementsByTagName("sTitle").item(0).getTextContent(),
                Integer.parseInt(doc.getElementsByTagName("ixPersonOpenedBy").item(0).getTextContent()),
                Integer.parseInt(doc.getElementsByTagName("ixPersonAssignedTo").item(0).getTextContent()),
                tags,
                Boolean.valueOf(doc.getElementsByTagName("fOpen").item(0).getTextContent()),

                // The following four field are only to be set if the user wants these custom fields.
                // Else we put empty string in there, rest of code understands that.
                (this.featureBranchFieldname != null && !this.featureBranchFieldname.isEmpty()) ?
                        doc.getElementsByTagName(this.featureBranchFieldname).item(0).getTextContent() : "",
                (this.originalBranchFieldname != null && !this.originalBranchFieldname.isEmpty()) ?
                    doc.getElementsByTagName(this.originalBranchFieldname).item(0).getTextContent() : "",
                (this.targetBranchFieldname != null && !this.targetBranchFieldname.isEmpty()) ?
                    doc.getElementsByTagName(this.targetBranchFieldname).item(0).getTextContent() : "",
                (this.approvedRevisionFieldname != null && !this.approvedRevisionFieldname.isEmpty()) ?
                        doc.getElementsByTagName(this.approvedRevisionFieldname).item(0).getTextContent() : "",

                doc.getElementsByTagName("sFixFor").item(0).getTextContent()
        );
    }

    /**
     * Retrieves all events for a certain case.
     * @param id Case id to fetch events from
     * @return list of FogbugzEvents
     */
    public List<FogbugzEvent> getEventsForCase(int id) {
        try {
            HashMap params = new HashMap();  // Hashmap defaults to <String, String>
            params.put("cmd", "search");
            params.put("q", Integer.toString(id));
            params.put("cols", "events");

            Document doc = this.getFogbugzDocument(params);

            List<FogbugzEvent> eventList = new ArrayList<FogbugzEvent>();
            NodeList eventsNodeList = doc.getElementsByTagName("event");
            if (eventsNodeList != null && eventsNodeList.getLength() != 0) {
                for (int i = 0; i< eventsNodeList.getLength(); i++) {
                    Element currentNode = (Element) eventsNodeList.item(i);
                    // Construct event object from retrieved data.
                    eventList.add(new FogbugzEvent(
                            Integer.parseInt(currentNode.getElementsByTagName("ixBugEvent").item(0).getTextContent()), // eventid
                            id, // caseid
                            currentNode.getElementsByTagName("sVerb").item(0).getTextContent(), // verb
                            Integer.parseInt(currentNode.getElementsByTagName("ixPerson").item(0).getTextContent()), // person
                            Integer.parseInt(currentNode.getElementsByTagName("ixPersonAssignedTo").item(0).getTextContent()), // personAssignedTo
                            DatatypeConverter.parseDateTime(currentNode.getElementsByTagName("dt").item(0).getTextContent()).getTime(), // dateTime
                            currentNode.getElementsByTagName("evtDescription").item(0).getTextContent(), // evtDescription
                            currentNode.getElementsByTagName("sPerson").item(0).getTextContent() // sPerson
                    ));
                }
            }

            return eventList;

        } catch (Exception e) {
            FogbugzManager.log.log(Level.SEVERE, "Exception while fetching case " + Integer.toString(id), e);
        }
        return null;
    }

    /**
     * Loop through all FogbugzEvent for given case id, and return last (in time) with assignment to gatekeepers.
     * @param caseId
     * @return Last event with gatekeeper assignment or null.
     */
    public FogbugzEvent getLastAssignedToGatekeepersEvent(int caseId) {
        List<FogbugzEvent> eventList = this.getEventsForCase(caseId);
        Collections.sort(eventList);
        Collections.reverse(eventList);

        for (FogbugzEvent ev: eventList) {
            if (ev.getPersonAssignedTo() == this.gatekeeperUserId) {
                return ev;
            }
        }

        return null;
    }

    /**
     * Saves a case to fogbugz using its API.
     * Supports creating new cases, by setting caseId to 0 on case object.
     * @param fbCase The case to save.
     * @param comment A message to pass for this edit.
     * @return boolean, true if all is well, else false.
     */
    public boolean saveCase(FogbugzCase fbCase, String comment) {
        try {
            HashMap params = new HashMap();
            // If id = 0, create new case.
            if (fbCase.getId() == 0) {
                params.put("cmd", "new");
                params.put("sTitle", fbCase.getTitle());
            } else {
                params.put("cmd", "edit");
                params.put("ixBug", Integer.toString(fbCase.getId()));
            }
            params.put("ixPersonAssignedTo", Integer.toString(fbCase.getAssignedTo()));
            params.put("ixPersonOpenedBy", Integer.toString(fbCase.getOpenedBy()));
            params.put("sTags", fbCase.tagsToCSV());
            if (this.featureBranchFieldname != null && !this.featureBranchFieldname.isEmpty()) {
                params.put(this.featureBranchFieldname, fbCase.getFeatureBranch());
            }
            if (this.originalBranchFieldname != null && !this.originalBranchFieldname.isEmpty()) {
                params.put(this.originalBranchFieldname, fbCase.getOriginalBranch());
            }
            if (this.targetBranchFieldname != null && !this.targetBranchFieldname.isEmpty()) {
                params.put(this.targetBranchFieldname, fbCase.getTargetBranch());
            }
            if (this.approvedRevisionFieldname != null && !this.approvedRevisionFieldname.isEmpty()) {
                params.put(this.approvedRevisionFieldname, fbCase.getApprovedRevision());
            }

            params.put("sFixFor", fbCase.getMilestone());
            params.put("sEvent", comment);

            Document doc = this.getFogbugzDocument(params);
            FogbugzManager.log.info("Fogbugz response got when saving case: " + doc.toString());
            // If we got this far, all is probably well.
            // TODO: parse XML that gets returned to check status furreal.
            return true;

        } catch (Exception e) {
            FogbugzManager.log.log(Level.SEVERE, "Exception while creating/saving case " + Integer.toString(fbCase.getId()), e);
        }
        return false;
    }

    /**
     * Additional save method that does not propagate a comment.
     * @param fbCase The case to save.
     * @return boolean, true if all is well, else false.
     */
    public boolean saveCase(FogbugzCase fbCase) {
        return this.saveCase(fbCase, "");
    }

    /**
     * Assign case to mergekeepers user id. Note: does not save case.
     * @param fbCase the case to set assignedTo on.
     * @return modified case.
     */
    public FogbugzCase assignToMergekeepers(FogbugzCase fbCase) {
        fbCase.setAssignedTo(this.mergekeeperUserId);
        return fbCase;
    }

    /**
     * Assign the given case to gatekeepers (uses GatekeeperUserID from global settings)
     * @param fbCase The case to edit.
     * @return modified case.
     */
    public FogbugzCase assignToGatekeepers(FogbugzCase fbCase) {
        fbCase.setAssignedTo(this.gatekeeperUserId);
        return fbCase;
    }

    /**
     * Returns a list of custom field names, comma seperated. Starts with a comma.
     */
    private String getCustomFieldsCSV() {
        String toReturn = "";
        if (this.featureBranchFieldname != null && !this.featureBranchFieldname.isEmpty()) {
            toReturn += "," + this.featureBranchFieldname;
        }
        if (this.originalBranchFieldname != null && !this.originalBranchFieldname.isEmpty()) {
            toReturn += "," + this.originalBranchFieldname;
        }
        if (this.targetBranchFieldname != null && !this.targetBranchFieldname.isEmpty()) {
            toReturn += "," + this.targetBranchFieldname;
        }
        if (this.approvedRevisionFieldname != null && !this.approvedRevisionFieldname.isEmpty()) {
            toReturn += "," + this.approvedRevisionFieldname;
        }
        return toReturn;
    }

    /**
     * Retrieves all milestones and returns them in a nice List of FogbugzMilestone objects.
     * @return list of FogbugzMilestones
     */
    public List<FogbugzMilestone> getMilestones() {
        try {
            HashMap params = new HashMap();  // Hashmap defaults to <String, String>
            params.put("cmd", "listFixFors");

            Document doc = this.getFogbugzDocument(params);

            List<FogbugzMilestone> milestoneList = new ArrayList<FogbugzMilestone>();
            NodeList milestonesNodeList = doc.getElementsByTagName("fixfor");
            if (milestonesNodeList != null && milestonesNodeList.getLength() != 0) {
                for (int i = 0; i< milestonesNodeList.getLength(); i++) {
                    Element currentNode = (Element) milestonesNodeList.item(i);
                    // Construct event object from retrieved data.
                    milestoneList.add(new FogbugzMilestone(
                            Integer.parseInt(currentNode.getElementsByTagName("ixFixFor").item(0).getTextContent()),
                            currentNode.getElementsByTagName("sFixFor").item(0).getTextContent(),
                            Boolean.valueOf(currentNode.getElementsByTagName("fDeleted").item(0).getTextContent()),
                            Boolean.valueOf(currentNode.getElementsByTagName("fReallyDeleted").item(0).getTextContent())
                    ));
                }
            }

            return milestoneList;

        } catch (Exception e) {
            FogbugzManager.log.log(Level.SEVERE, "Exception while fetching milestones", e);
        }
        return new ArrayList<FogbugzMilestone>();
    }

    /**
     * Creates new Milestone in Fogbugz. Please leave id of milestone object empty.
     * Only creates global milestones.
     * @param milestone to edit/create
     */
    public boolean createMilestone(FogbugzMilestone milestone) {
        try {
            HashMap params = new HashMap();
            // If id = 0, create new case.
            if (milestone.getId() != 0) {
                throw new Exception("Editing existing milestones is not supported, please set the id to 0.");
            }

            params.put("cmd", "newFixFor");
            params.put("ixProject", "-1");
            params.put("fAssignable", "1");  // 1 means true somehow...
            params.put("sFixFor", milestone.getName());

            Document doc = this.getFogbugzDocument(params);

            FogbugzManager.log.info("Fogbugz response got when saving milestone: " + doc.toString());
            // If we got this far, all is probably well.
            // TODO: parse XML that gets returned to check status furreal.
            return true;

        } catch (Exception e) {
            FogbugzManager.log.log(Level.SEVERE, "Exception while creating milestone " + milestone.getName(), e);
        }
        return false;
    }

    public boolean createMilestoneIfNotExists(String milestoneName) {
        List<FogbugzMilestone> milestones = this.getMilestones();
        for (FogbugzMilestone milestone : milestones) {
            if (milestone.getName().equals(milestoneName)) {
                // Milestone already exists, no need to create.
                FogbugzManager.log.info("Milestone " + milestoneName + " already exists, not creating.");
                return false;
            }
        }

        FogbugzManager.log.info("Creating milestone " + milestoneName + ".");
        FogbugzMilestone newMilestone = new FogbugzMilestone(0, milestoneName, false, false);
        return this.createMilestone(newMilestone);
    }
}
