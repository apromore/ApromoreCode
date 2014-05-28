package org.apromore.portal.dialogController;

// Java 2 Standard packages
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Applet;
import org.zkoss.zul.Window;

// Local packages
import com.processconfiguration.ConfigurationMapping;
import com.processconfiguration.cmap.CMAP;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TDocumentation;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * Invoke the Configuration Mapper UI to configure a process model version.
 */
public class CmapController extends BaseController {

    private static final String NATIVE_TYPE = "BPMN 2.0";
    private static final long serialVersionUID = 1L;
    private MainController mainC;
    private Window cmapW;

    /** The list of process versions to configure. */
    private Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions;

    /**
     * Sole constructor.
     *
     * @param mainC
     * @param selectedProcessVersions
     * @throws ConfigureException if <var>selectedProcessVersions</var> include a process which doesn't have
     *   a valid cmap and qml associated with it
     */
    public CmapController(MainController mainC, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions)
        throws ConfigureException {
        this.mainC = mainC;

        this.cmapW = (Window) Executions.createComponents("macros/cmap.zul", null, null);

        URL cmapURL = null;
        URL qmlURL  = null;

        // Look up JAXB context
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                              org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                              org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                              org.omg.spec.dd._20100524.di.ObjectFactory.class,
                                              com.processconfiguration.ObjectFactory.class,
                                              com.signavio.ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create JAXB context", e);
        }
        assert context != null;

        // Iterate through all selected models
        for (ProcessSummaryType process: selectedProcessVersions.keySet()) {
            for (VersionSummaryType version: selectedProcessVersions.get(process)) {

                String modelName = process.getName();

                // Look for the cmap URL in the C-BPMN document
                ExportFormatResultType exportFormatResult;
                try {
                    exportFormatResult = getService().exportFormat(
                        process.getId(),             // process ID
                        null,                        // process name
                        version.getName(),           // branch
                        version.getVersionNumber(),  // version number,
                        NATIVE_TYPE,                 // nativeType,
                        null,                        // annotation name,
                        false,                       // with annotations?
                        null,                        // owner
                        Collections.EMPTY_SET        // canoniser properties
                    );
                } catch (Exception e) {
                    System.err.println("Unable to export BPMN model: " + e.getMessage());
                    e.printStackTrace();
                    throw new ConfigureException("Unable to access " + modelName, e);
                }

                TDefinitions bpmn;
                try {
                    bpmn = ((JAXBElement<TDefinitions>) context.createUnmarshaller().unmarshal(new StreamSource(exportFormatResult.getNative().getInputStream()))).getValue();
                } catch (IOException e) {
                    throw new ConfigureException("Unable to read " + modelName, e);
                } catch (JAXBException e) {
                    throw new ConfigureException("Unable to parse " + modelName, e);
                }
                assert bpmn != null;

                String cmapURLString = null;
                for (JAXBElement<? extends TRootElement> root: bpmn.getRootElement()) {
                    TExtensionElements extensionElements = root.getValue().getExtensionElements();
                    if (extensionElements != null) {
                        for (Object object: extensionElements.getAny()) {
                            if (object instanceof ConfigurationMapping) {
                                cmapURLString = ((ConfigurationMapping) object).getHref();
                            }
                        }
                    }
                }

                // Is there a cmap link at all?
                if (cmapURLString == null || cmapURLString.trim().isEmpty()) {
                    throw new ConfigureException("The model " + modelName +
                        " lacks a link to a configuration mapping.");
                }

                // Parse the cmap link into cmapURL
                try {
                    cmapURL = new URL(cmapURLString);
                } catch (MalformedURLException e) {
                    throw new ConfigureException("The model " + modelName +
                        " has a malformed link to its configuration mapping: \"" + cmapURLString + "\"", e);
                }
                assert cmapURL != null;

                // Download and parse the cmap document
                CMAP cmap;
                try {
                    HttpURLConnection c = (HttpURLConnection) cmapURL.openConnection();
                    c.setRequestMethod("GET");
                    c.addRequestProperty("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");  // Base64 encoded "admin:password"
                    c.connect();
                    System.err.println("Reponse code: " + c.getResponseCode());
                    System.err.println("Reponse message: " + c.getResponseMessage());
                    System.err.println("Content type: " + c.getContentType());
                    cmap = (CMAP) JAXBContext.newInstance(com.processconfiguration.cmap.ObjectFactory.class)
                                                  .createUnmarshaller()
                                                  .unmarshal(new StreamSource(c.getInputStream()));
                } catch (IOException e) {
                    throw new ConfigureException("Unable to read the configuration mapping from " + cmapURL, e);
                } catch (JAXBException e) {
                    throw new ConfigureException("Unable to parse the configuration mapping from " + cmapURL, e);
                }
                assert cmap != null;

                // Parse the qml link into qmlURL
                System.err.println("QML field from Cmap: " + cmap.getQml());
                try {
                    qmlURL = new URL(cmapURL, cmap.getQml());
                } catch (MalformedURLException e) {
                    throw new ConfigureException("The cmap file " + cmapURLString +
                        " which " + modelName + " is linked to has an invalid questionnaire link: \"" +
                        cmap.getQml() + "\"", e);
                }
                System.err.println("QML URL from Cmap: " + qmlURL);

                // Set the applet parameters
                Applet configureA = (Applet) this.cmapW.getFellow("cmapper");
                configureA.setParam("apromore_model", process.getId() + " " + version.getName() + " " + version.getVersionNumber());
                configureA.setParam("qml_url", qmlURL.toString());
                configureA.setParam("cmap_url", cmapURL.toString());

                System.err.println("New params=" + configureA.getParams());

                this.cmapW.doModal();

                System.err.println("Entered Cmapper applet mode");
            }
        }
    }
}
