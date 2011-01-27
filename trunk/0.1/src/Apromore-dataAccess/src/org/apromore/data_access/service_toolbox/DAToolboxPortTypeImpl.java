
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.data_access.service_toolbox;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.activation.DataHandler;

import org.apromore.data_access.dao.ProcessDao;
import org.apromore.data_access.model_toolbox.CanonicalType;
import org.apromore.data_access.model_toolbox.CanonicalsType;
import org.apromore.data_access.model_toolbox.ProcessSummariesType;
import org.apromore.data_access.model_toolbox.ProcessVersionType;
import org.apromore.data_access.model_toolbox.ReadCanonicalsInputMsgType;
import org.apromore.data_access.model_toolbox.ReadCanonicalsOutputMsgType;
import org.apromore.data_access.model_toolbox.ReadProcessSummariesInputMsgType;
import org.apromore.data_access.model_toolbox.ReadProcessSummariesOutputMsgType;
import org.apromore.data_access.model_toolbox.ResultType;
import org.apromore.data_access.model_toolbox.StoreCpfInputMsgType;
import org.apromore.data_access.model_toolbox.StoreCpfOutputMsgType;

/**
 * This class was generated by Apache CXF 2.2.9
 * Thu Jan 27 13:54:22 CET 2011
 * Generated source version: 2.2.9
 * 
 */

@javax.jws.WebService(
                      serviceName = "DAToolboxService",
                      portName = "DAToolbox",
                      targetNamespace = "http://www.apromore.org/data_access/service_toolbox",
                      wsdlLocation = "http://localhost:8080/Apromore-dataAccess/services/DAToolbox?wsdl",
                      endpointInterface = "org.apromore.data_access.service_toolbox.DAToolboxPortType")
                      
public class DAToolboxPortTypeImpl implements DAToolboxPortType {

    private static final Logger LOG = Logger.getLogger(DAToolboxPortTypeImpl.class.getName());

	public ReadProcessSummariesOutputMsgType readProcessSummaries(ReadProcessSummariesInputMsgType payload) { 
        LOG.info("Executing operation readProcessSummaries");
        System.out.println(payload);
        ResultType result = new ResultType();
        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        res.setResult(result);
        try {        
        	List<Integer> processIds = new ArrayList<Integer>();
        	List<String> versionNames = new ArrayList<String>();
        	for (ProcessVersionType pv: payload.getProcessVersions().getProcessVersion()) {
        		processIds.add(pv.getProcessId());
        		versionNames.add(pv.getVersionName());
        	}
        	ProcessSummariesType processes = ProcessDao.getInstance().getProcessSummaries(processIds, versionNames);
        	res.setProcessSummaries(processes);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
        	result.setCode(-1);
        	result.setMessage(ex.getMessage());
        }
        return res;
    }

	/* (non-Javadoc)
     * @see org.apromore.data_access.service_toolbox.DAToolboxPortType#readCanonicals(org.apromore.data_access.model_toolbox.ReadCanonicalsInputMsgType  payload )*
     */
    public ReadCanonicalsOutputMsgType readCanonicals(ReadCanonicalsInputMsgType payload) { 
        LOG.info("Executing operation readCanonicals");
        ReadCanonicalsOutputMsgType res = new ReadCanonicalsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
        	List<ProcessVersionType> ids = payload.getProcessVersion();
        	List<CanonicalType> allCanonicals = ProcessDao.getInstance().getCanonicals(ids);
            CanonicalsType canonicals = new CanonicalsType();
            canonicals.getCanonical().addAll(allCanonicals);
            res.setCanonicals(canonicals);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
        	result.setCode(-1);
        	result.setMessage(ex.getMessage());
        }
        return res;
    }

    /* (non-Javadoc)
     * @see org.apromore.data_access.service_toolbox.DAToolboxPortType#storeCpf(org.apromore.data_access.model_toolbox.StoreCpfInputMsgType  payload )*
     */
    public StoreCpfOutputMsgType storeCpf(StoreCpfInputMsgType payload) { 
        LOG.info("Executing operation storeCpf");
        StoreCpfOutputMsgType res = new StoreCpfOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        String processName = payload.getProcessName();
        String versionName = payload.getVersion();
        String domain = payload.getDomain();
        String username = payload.getUsername();
        DataHandler handler = payload.getCpf();
        try {
            InputStream cpf_is = handler.getInputStream();
            org.apromore.data_access.model_toolbox.ProcessSummaryType process = 
            	ProcessDao.getInstance().storeCpf(processName, versionName, domain, username, cpf_is, newCpfURI());
            res.setProcessSummary(process);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
        	result.setCode(-1);
        	result.setMessage(ex.getMessage());
        }
        return res;
    }
    /**
	 * Generate a cpf uri for version of processId
	 * @param processId
	 * @param version
	 * @return
	 */
	private String newCpfURI() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
		Date date = new Date();
		String time = dateFormat.format(date);
		return time;
	}
}
