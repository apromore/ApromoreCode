
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.canoniser.da;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.7
 * Mon May 24 14:52:13 EST 2010
 * Generated source version: 2.2.7
 * 
 */

@javax.jws.WebService(
                      serviceName = "DACanoniserService",
                      portName = "DACanoniser",
                      targetNamespace = "http://www.apromore.org/data_access/service_canoniser",
                      wsdlLocation = "http://localhost:8080/Apromore-dataAccess/services/DACanoniser?wsdl",
                      endpointInterface = "org.apromore.canoniser.da.DACanoniserPortType")
                      
public class DACanoniserPortTypeImpl implements DACanoniserPortType {

    private static final Logger LOG = Logger.getLogger(DACanoniserPortTypeImpl.class.getName());

    /* (non-Javadoc)
     * @see org.apromore.canoniser.da.DACanoniserPortType#storeNativeCpf(org.apromore.canoniser.model_da.StoreNativeCpfInputMsgType  payload )*
     */
    public org.apromore.canoniser.model_da.StoreNativeCpfOutputMsgType storeNativeCpf(org.apromore.canoniser.model_da.StoreNativeCpfInputMsgType payload) { 
        LOG.info("Executing operation storeNativeCpf");
        System.out.println(payload);
        try {
            org.apromore.canoniser.model_da.StoreNativeCpfOutputMsgType _return = new org.apromore.canoniser.model_da.StoreNativeCpfOutputMsgType();
            org.apromore.canoniser.model_da.ResultType _returnResult = new org.apromore.canoniser.model_da.ResultType();
            _returnResult.setMessage("Message1928006626");
            _returnResult.setCode(Integer.valueOf(1768666972));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public org.apromore.canoniser.model_da.StoreVersionOutputMsgType storeVersion(org.apromore.canoniser.model_da.StoreVersionInputMsgType payload) { 
        LOG.info("Executing operation storeVersion");
        System.out.println(payload);
        try {
            org.apromore.canoniser.model_da.StoreVersionOutputMsgType _return = new org.apromore.canoniser.model_da.StoreVersionOutputMsgType();
            org.apromore.canoniser.model_da.ResultType _returnResult = new org.apromore.canoniser.model_da.ResultType();
            _returnResult.setMessage("Message52553338");
            _returnResult.setCode(Integer.valueOf(-688066109));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

	/* (non-Javadoc)
     * @see org.apromore.canoniser.da.DACanoniserPortType#storeNative(org.apromore.canoniser.model_da.StoreNativeInputMsgType  payload )*
     */
    public org.apromore.canoniser.model_da.StoreNativeOutputMsgType storeNative(org.apromore.canoniser.model_da.StoreNativeInputMsgType payload) { 
        LOG.info("Executing operation storeNative");
        System.out.println(payload);
        try {
            org.apromore.canoniser.model_da.StoreNativeOutputMsgType _return = new org.apromore.canoniser.model_da.StoreNativeOutputMsgType();
            org.apromore.canoniser.model_da.ResultType _returnResult = new org.apromore.canoniser.model_da.ResultType();
            _returnResult.setMessage("Message642859751");
            _returnResult.setCode(Integer.valueOf(985267482));
            _return.setResult(_returnResult);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
