package org.apromore.manager.da;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.7
 * Wed Apr 21 15:29:07 EST 2010
 * Generated source version: 2.2.7
 * 
 */
 
@WebService(targetNamespace = "http://www.apromore.org/data_access/service_manager", name = "DAManagerPortType")
@XmlSeeAlso({org.apromore.manager.model_manager.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface DAManagerPortType {

    @WebResult(name = "WriteUserOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager", partName = "payload")
    @WebMethod(operationName = "WriteUser")
    public org.apromore.manager.model_manager.WriteUserOutputMsgType writeUser(
        @WebParam(partName = "payload", name = "WriteUserInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager")
        org.apromore.manager.model_manager.WriteUserInputMsgType payload
    );

    @WebResult(name = "ReadDomainsOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager", partName = "payload")
    @WebMethod(operationName = "ReadDomains")
    public org.apromore.manager.model_manager.ReadDomainsOutputMsgType readDomains(
        @WebParam(partName = "payload", name = "ReadDomainsInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager")
        org.apromore.manager.model_manager.ReadDomainsInputMsgType payload
    );

    @WebResult(name = "ReadFormatsOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager", partName = "payload")
    @WebMethod(operationName = "ReadFormats")
    public org.apromore.manager.model_manager.ReadFormatsOutputMsgType readFormats(
        @WebParam(partName = "payload", name = "ReadFormatsInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager")
        org.apromore.manager.model_manager.ReadFormatsInputMsgType payload
    );

    @WebResult(name = "ReadUserOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager", partName = "payload")
    @WebMethod(operationName = "ReadUser")
    public org.apromore.manager.model_manager.ReadUserOutputMsgType readUser(
        @WebParam(partName = "payload", name = "ReadUserInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager")
        org.apromore.manager.model_manager.ReadUserInputMsgType payload
    );

    @WebResult(name = "ReadProcessSummariesOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager", partName = "payload")
    @WebMethod(operationName = "ReadProcessSummaries")
    public org.apromore.manager.model_manager.ReadProcessSummariesOutputMsgType readProcessSummaries(
        @WebParam(partName = "payload", name = "ReadProcessSummariesInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_manager")
        org.apromore.manager.model_manager.ReadProcessSummariesInputMsgType payload
    );
}
