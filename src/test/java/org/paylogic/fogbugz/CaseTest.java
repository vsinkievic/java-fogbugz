package org.paylogic.fogbugz;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.List;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.powermock.api.easymock.PowerMock.*;


/**
 * Test class that tests everything to do with cases.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DefaultFogbugzManager.class)
class CaseTest {
    private boolean setUpIsDone = false;
    private DefaultFogbugzManager manager;

    @Before
    public void setUp() {
        if (setUpIsDone) {
            return;
        }

//        manager = new DefaultFogbugzManager("http://localhost/fogbugz/", "asdfasdf12341234",
//                                     "plugin_customfields_at_fogcreek_com_featurexbranchx12",
//                                     "plugin_customfields_at_fogcreek_com_originalxbranchv23",
//                                     "plugin_customfields_at_fogcreek_com_targetxbranchj81", 1, 1);
//        System.out.println(junit.runner.Version.id());
        setUpIsDone = true;
    }

    @Test
    public void testFetchCaseByIdWithCustomFields() throws Exception {
        DefaultFogbugzManager tested = createPartialMock(DefaultFogbugzManager.class, new String[]{"getFogbugzDocument"},
                "http://localhost/fogbugz/", "asdfasdf12341234", "plugin_customfields_at_fogcreek_com_featurexbranchx12",
                "plugin_customfields_at_fogcreek_com_originalxbranchv23", "plugin_customfields_at_fogcreek_com_targetxbranchj81",
                "plugin_customfields_at_fogcreek_com_approvedxrevisiona44", "cixproject", 2, 2);

        FogbugzCase expected = new FogbugzCase(7, "HALLO!", 2, 2, "merged", true,
                                               "maikelwever/repo1#c7", "r1336", "r1336", "1336", "asdf1234", "myproject");

        expectPrivate(tested, "getFogbugzDocument", anyObject()).andReturn(fetchDocumentFromFile("test_case_7.xml"));
        replay(tested);

        FogbugzCase parsed = tested.getCaseById(7);
        verify(tested);

        assert expected.equals(parsed);
    }

    @Test
    public void testSearchForCase() throws Exception {
        DefaultFogbugzManager tested = createPartialMock(DefaultFogbugzManager.class, new String[]{"getFogbugzDocument"},
                "http://localhost/fogbugz/", "asdfasdf12341234", "plugin_customfields_at_fogcreek_com_featurexbranchx12",
                "plugin_customfields_at_fogcreek_com_originalxbranchv23", "plugin_customfields_at_fogcreek_com_targetxbranchj81",
                "plugin_customfields_at_fogcreek_com_approvedxrevisiona44", "cixproject", 2, 2);

        expectPrivate(tested, "getFogbugzDocument", anyObject()).andReturn(fetchDocumentFromFile("test_case_list.xml"));

        replay(tested);

        List<FogbugzCase> cases = tested.searchForCases("foo");

        verify(tested);

        assert cases.size() == 1;
        assert cases.get(0) == new FogbugzCase(1, "Test case name", 1, 1, "", true, null, null, null, null, null, null);

    }

    @Test
    public void testFetchCaseByIdWithoutCustomFields() throws Exception {
        DefaultFogbugzManager tested = createPartialMock(DefaultFogbugzManager.class, new String[]{"getFogbugzDocument"},
                "http://localhost/fogbugz/", "asdfasdf12341234", "", "", "", "", "", 2, 2);

        FogbugzCase expected = new FogbugzCase(7, "HALLO!", 2, 2, "merged", true, "", "", "", "", "1336", "myproject");

        expectPrivate(tested, "getFogbugzDocument", anyObject()).andReturn(fetchDocumentFromFile("test_case_7_no_customfields.xml"));
        replay(tested);

        FogbugzCase parsed = tested.getCaseById(7);
        verify(tested);

        assert expected.equals(parsed);
    }

    @Test
    public void testFetchCaseByIdWithNullCustomFields() throws Exception {
        DefaultFogbugzManager tested = createPartialMock(DefaultFogbugzManager.class, new String[]{"getFogbugzDocument"});

        FogbugzCase expected = new FogbugzCase(7, "HALLO!", 2, 2, "merged", true, "", "", "", "", "1336", "myproject");

        expectPrivate(tested, "getFogbugzDocument", anyObject()).andReturn(fetchDocumentFromFile("test_case_7_no_customfields.xml"));
        replay(tested);

        FogbugzCase parsed = tested.getCaseById(7);
        verify(tested);

        assert expected.equals(parsed);
    }

    @Test(expected=NoSuchCaseException.class)
    public void testFetchNonExistingCase() throws Exception {
        DefaultFogbugzManager tested = createPartialMock(DefaultFogbugzManager.class, new String[]{"getFogbugzDocument"},
                "http://localhost/fogbugz/", "asdfasdf12341234", "plugin_customfields_at_fogcreek_com_featurexbranchx12",
                "plugin_customfields_at_fogcreek_com_originalxbranchv23", "plugin_customfields_at_fogcreek_com_targetxbranchj81",
                "plugin_customfields_at_fogcreek_com_approvedxrevisiona44", "cixproject", 2, 2);

        expectPrivate(tested, "getFogbugzDocument", anyObject()).andReturn(fetchDocumentFromFile("test_case_non_existant.xml"));
        replay(tested);

        FogbugzCase parsed = tested.getCaseById(37);
        verify(tested);
    }

    public void testSavingModifiedCase() throws Exception {

    }

    public void testCaseCreation() throws Exception {

    }

    /**
     * Helper method to get documents from files on disk.
     */
    private Document fetchDocumentFromFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        URL url = this.getClass().getResource("/" + filename);
        File testFile = new File(url.getFile());
        assert testFile.exists();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document doc = builder.parse(testFile);
        return doc;
    }
}