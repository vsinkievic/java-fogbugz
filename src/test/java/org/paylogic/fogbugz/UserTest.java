/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paylogic.fogbugz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author dirk
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DefaultFogbugzManager.class)
public class UserTest {

    @Test
    public void testGetUserName() throws Exception {
        DefaultFogbugzManager tested = createPartialMock(DefaultFogbugzManager.class, new String[]{"getFogbugzDocument"},
                "http://localhost/fogbugz/", "asdfasdf12341234", "plugin_customfields_at_fogcreek_com_featurexbranchx12",
                "plugin_customfields_at_fogcreek_com_originalxbranchv23", "plugin_customfields_at_fogcreek_com_targetxbranchj81",
                "plugin_customfields_at_fogcreek_com_approvedxrevisiona44", "cixproject", 2, 2);

        expectPrivate(tested, "getFogbugzDocument", anyObject()).andReturn(fetchDocumentFromFile("test_user.xml"));

        replay(tested);

        FogbugzUser parsed = tested.getFogbugzUser(1);

        verify(tested);

        assert parsed.equals(new FogbugzUser(1, "First Last"));
    }

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
