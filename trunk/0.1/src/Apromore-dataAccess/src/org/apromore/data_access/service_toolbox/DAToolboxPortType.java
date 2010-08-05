package org.apromore.data_access.service_toolbox;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.9
 * Wed Aug 04 18:23:53 EST 2010
 * Generated source version: 2.2.9
 * 
 */
 
@WebService(targetNamespace = "http://www.apromore.org/data_access/service_toolbox", name = "DAToolboxPortType")
@XmlSeeAlso({org.apromore.data_access.model_toolbox.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface DAToolboxPortType {

    @WebResult(name = "ReadCanonicalsOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_toolbox", partName = "payload")
    @WebMethod(operationName = "ReadCanonicals")
    public org.apromore.data_access.model_toolbox.ReadCanonicalsOutputMsgType readCanonicals(
        @WebParam(partName = "payload", name = "ReadCanonicalsInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_toolbox")
        org.apromore.data_access.model_toolbox.ReadCanonicalsInputMsgType payload
    );

    @WebResult(name = "StoreCpfOutputMsg", targetNamespace = "http://www.apromore.org/data_access/model_toolbox", partName = "payload")
    @WebMethod(operationName = "StoreCpf")
    public org.apromore.data_access.model_toolbox.StoreCpfOutputMsgType storeCpf(
        @WebParam(partName = "payload", name = "StoreCpfInputMsg", targetNamespace = "http://www.apromore.org/data_access/model_toolbox")
        org.apromore.data_access.model_toolbox.StoreCpfInputMsgType payload
    );
}
