
/*
 * 
 */

package org.apromore.toolbox.service_manager;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.2.9
 * Thu Jan 27 14:56:19 CET 2011
 * Generated source version: 2.2.9
 * 
 */


@WebServiceClient(name = "ToolboxManagerService", 
                  wsdlLocation = "http://localhost:8080/Apromore-toolbox/services/ToolboxManager?wsdl",
                  targetNamespace = "http://www.apromore.org/toolbox/service_manager") 
public class ToolboxManagerService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://www.apromore.org/toolbox/service_manager", "ToolboxManagerService");
    public final static QName ToolboxManager = new QName("http://www.apromore.org/toolbox/service_manager", "ToolboxManager");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/Apromore-toolbox/services/ToolboxManager?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://localhost:8080/Apromore-toolbox/services/ToolboxManager?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public ToolboxManagerService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ToolboxManagerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ToolboxManagerService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns ToolboxManagerPortType
     */
    @WebEndpoint(name = "ToolboxManager")
    public ToolboxManagerPortType getToolboxManager() {
        return super.getPort(ToolboxManager, ToolboxManagerPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ToolboxManagerPortType
     */
    @WebEndpoint(name = "ToolboxManager")
    public ToolboxManagerPortType getToolboxManager(WebServiceFeature... features) {
        return super.getPort(ToolboxManager, ToolboxManagerPortType.class, features);
    }

}
