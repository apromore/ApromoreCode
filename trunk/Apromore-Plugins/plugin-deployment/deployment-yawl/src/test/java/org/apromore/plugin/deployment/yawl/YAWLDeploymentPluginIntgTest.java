package org.apromore.plugin.deployment.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.impl.PluginRequestImpl;
import org.apromore.plugin.property.RequestParameterType;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Integration test using a real YAWL Engine if available. It will be searched for on localhost:8080!
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLDeploymentPluginIntgTest {

    private YAWLDeploymentPlugin deploymentPlugin;

    @Before
    public void setUp() throws Exception {
        deploymentPlugin = new YAWLDeploymentPlugin();
    }

    @Test
    public void testDeployProcessCanonicalProcessType() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/WPC1Sequence.yawl.cpf"))) {
                CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
                PluginRequestImpl request = new PluginRequestImpl();
                request.addRequestProperty(new RequestParameterType<String>("yawlEngineUrl", "http://localhost:8080/yawl/ia"));
                request.addRequestProperty(new RequestParameterType<String>("yawlEngineUsername", "admin"));
                request.addRequestProperty(new RequestParameterType<String>("yawlEnginePassword", "YAWL"));
                PluginResult result = deploymentPlugin.deployProcess(cpf, request);
                assertTrue(result.getPluginMessage().size() == 1 || result.getPluginMessage().size() == 2);
                if (result.getPluginMessage().size() == 2) {
                    assertEquals("Failure deploying process WP1Sequence", result.getPluginMessage().get(0).getMessage());
                    assertEquals("Error: There is a specification with an identical id to [UID: WP1Sequence- Version: 0.1] already loaded into the engine.", result.getPluginMessage().get(1).getMessage());
                } else {
                    assertTrue("Process Simple Make Trip Process successfully deployed.".equals(result.getPluginMessage().get(0).getMessage())
                            || "Error: There is a specification with an identical id to [UID: WP1Sequence- Version: 0.1] already loaded into the engine.".equals(result.getPluginMessage().get(0).getMessage()));
                }
            }
        }
    }

    private boolean checkYAWLServerAvailable() {
        try {
            return Request.Get("http://localhost:8080/yawl/ia").useExpectContinue()
                    .addHeader("Accept-Charset", "UTF-8")
                    .version(HttpVersion.HTTP_1_1)
                    .execute().returnContent().asString().startsWith("<response><failure><reason>");
        } catch (IOException e) {
            return false;
        }
    }


}