
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.toolbox.da;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.9
 * Wed Aug 04 15:40:56 EST 2010
 * Generated source version: 2.2.9
 * 
 */

@javax.jws.WebService(
                      serviceName = "DAToolboxService",
                      portName = "DAToolbox",
                      targetNamespace = "http://www.apromore.org/data_access/service_toolbox",
                      wsdlLocation = "http://localhost:8080/Apromore-dataAccess/services/DAToolbox?wsdl",
                      endpointInterface = "org.apromore.toolbox.da.DAToolboxPortType")
                      
public class DAToolboxPortTypeImpl implements DAToolboxPortType {

    private static final Logger LOG = Logger.getLogger(DAToolboxPortTypeImpl.class.getName());

    /* (non-Javadoc)
     * @see org.apromore.toolbox.da.DAToolboxPortType#readCanonicals(org.apromore.toolbox.model_da.ReadCanonicalsInputMsgType  payload )*
     */
    public org.apromore.toolbox.model_da.ReadCanonicalsOutputMsgType readCanonicals(org.apromore.toolbox.model_da.ReadCanonicalsInputMsgType payload) { 
        LOG.info("Executing operation readCanonicals");
        System.out.println(payload);
        try {
            org.apromore.toolbox.model_da.ReadCanonicalsOutputMsgType _return = new org.apromore.toolbox.model_da.ReadCanonicalsOutputMsgType();
            org.apromore.toolbox.model_da.ResultType _returnResult = new org.apromore.toolbox.model_da.ResultType();
            _returnResult.setMessage("Message2061185988");
            _returnResult.setCode(Integer.valueOf(-1915245739));
            _return.setResult(_returnResult);
            org.apromore.toolbox.model_da.CanonicalsType _returnCanonicals = new org.apromore.toolbox.model_da.CanonicalsType();
            _returnCanonicals.setProcessId(-1785811802);
            _returnCanonicals.setVersionName("VersionName1124077886");
            java.util.List<javax.activation.DataHandler> _returnCanonicalsCpf = new java.util.ArrayList<javax.activation.DataHandler>();
            javax.activation.DataHandler _returnCanonicalsCpfVal1 = null;
            _returnCanonicalsCpf.add(_returnCanonicalsCpfVal1);
            _returnCanonicals.getCpf().addAll(_returnCanonicalsCpf);
            _return.setCanonicals(_returnCanonicals);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.toolbox.da.DAToolboxPortType#storeCpf(org.apromore.toolbox.model_da.StoreCpfInputMsgType  payload )*
     */
    public org.apromore.toolbox.model_da.StoreCpfOutputMsgType storeCpf(org.apromore.toolbox.model_da.StoreCpfInputMsgType payload) { 
        LOG.info("Executing operation storeCpf");
        System.out.println(payload);
        try {
            org.apromore.toolbox.model_da.StoreCpfOutputMsgType _return = new org.apromore.toolbox.model_da.StoreCpfOutputMsgType();
            org.apromore.toolbox.model_da.ResultType _returnResult = new org.apromore.toolbox.model_da.ResultType();
            _returnResult.setMessage("Message357966839");
            _returnResult.setCode(Integer.valueOf(-268995212));
            _return.setResult(_returnResult);
            org.apromore.toolbox.model_da.ProcessSummaryType _returnProcessSummary = new org.apromore.toolbox.model_da.ProcessSummaryType();
            java.util.List<org.apromore.toolbox.model_da.VersionSummaryType> _returnProcessSummaryVersionSummaries = new java.util.ArrayList<org.apromore.toolbox.model_da.VersionSummaryType>();
            org.apromore.toolbox.model_da.VersionSummaryType _returnProcessSummaryVersionSummariesVal1 = new org.apromore.toolbox.model_da.VersionSummaryType();
            java.util.List<java.lang.String> _returnProcessSummaryVersionSummariesVal1Annotations = new java.util.ArrayList<java.lang.String>();
            _returnProcessSummaryVersionSummariesVal1.getAnnotations().addAll(_returnProcessSummaryVersionSummariesVal1Annotations);
            _returnProcessSummaryVersionSummariesVal1.setRanking("Ranking626289882");
            _returnProcessSummaryVersionSummariesVal1.setName("Name-1965299731");
            _returnProcessSummaryVersionSummariesVal1.setDocumentation("Documentation-1845382004");
            _returnProcessSummaryVersionSummariesVal1.setLastUpdate("LastUpdate-1979314062");
            _returnProcessSummaryVersionSummariesVal1.setCreationDate("CreationDate-1664055032");
            _returnProcessSummaryVersionSummaries.add(_returnProcessSummaryVersionSummariesVal1);
            _returnProcessSummary.getVersionSummaries().addAll(_returnProcessSummaryVersionSummaries);
            _returnProcessSummary.setOriginalNativeType("OriginalNativeType-1292732582");
            _returnProcessSummary.setName("Name-1492373591");
            _returnProcessSummary.setId(Integer.valueOf(910423975));
            _returnProcessSummary.setDomain("Domain-1606919868");
            _returnProcessSummary.setRanking("Ranking-652915383");
            _returnProcessSummary.setLastVersion("LastVersion-1526263671");
            _returnProcessSummary.setOwner("Owner1412919506");
            _return.setProcessSummary(_returnProcessSummary);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
