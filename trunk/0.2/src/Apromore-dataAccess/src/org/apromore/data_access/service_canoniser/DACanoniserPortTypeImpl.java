
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.data_access.service_canoniser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.activation.DataHandler;

import org.apromore.data_access.dao.ProcessDao;
import org.apromore.data_access.exception.ExceptionAnntotationName;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.exception.ExceptionStoreVersion;
import org.apromore.data_access.exception.ExceptionSyncNPF;
import org.apromore.data_access.model_canoniser.GetCpfUriOutputMsgType;
import org.apromore.data_access.model_canoniser.ProcessSummaryType;
import org.apromore.data_access.model_canoniser.ResultType;
import org.apromore.data_access.model_canoniser.StoreNativeCpfInputMsgType;
import org.apromore.data_access.model_canoniser.StoreNativeCpfOutputMsgType;
import org.apromore.data_access.model_canoniser.StoreNativeInputMsgType;
import org.apromore.data_access.model_canoniser.StoreNativeOutputMsgType;
import org.apromore.data_access.model_canoniser.StoreVersionInputMsgType;
import org.apromore.data_access.model_canoniser.StoreVersionOutputMsgType;
import org.apromore.data_access.model_canoniser.WriteAnnotationInputMsgType;
import org.apromore.data_access.model_canoniser.WriteAnnotationOutputMsgType;

/**
 * This class was generated by Apache CXF 2.2.9
 * Tue May 24 18:54:42 CEST 2011
 * Generated source version: 2.2.9
 * 
 */

@javax.jws.WebService(
                      serviceName = "DACanoniserService",
                      portName = "DACanoniser",
                      targetNamespace = "http://www.apromore.org/data_access/service_canoniser",
                      wsdlLocation = "http://localhost:8080/Apromore-dataAccess/services/DACanoniser?wsdl",
                      endpointInterface = "org.apromore.data_access.service_canoniser.DACanoniserPortType")

		public class DACanoniserPortTypeImpl implements DACanoniserPortType {

	private static final Logger LOG = Logger.getLogger(DACanoniserPortTypeImpl.class.getName());



	public org.apromore.data_access.model_canoniser.GetCpfUriOutputMsgType 
	getCpfUri(org.apromore.data_access.model_canoniser.GetCpfUriInputMsgType payload) { 
        LOG.info("Executing operation getCpfUri");
        System.out.println(payload);
        GetCpfUriOutputMsgType res = new GetCpfUriOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
        try {
        	String version = payload.getVersion();
        	Integer processId = payload.getProcessId();
        	String cpf_uri = ProcessDao.getInstance().getCpfUri (processId, version);
        	res.setCpfURI(cpf_uri);
        	result.setCode(0);
			result.setMessage("");
        } catch (Exception ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
        }
        return res;
    }



	public WriteAnnotationOutputMsgType writeAnnotation(WriteAnnotationInputMsgType payload) { 
		LOG.info("Executing operation writeAnnotation");
		System.out.println(payload);
		WriteAnnotationOutputMsgType res = new WriteAnnotationOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			Integer editSessionCode = payload.getEditSessionCode();
			String annotationName = payload.getAnnotationName();
			Boolean isNew = payload.isIsNew();
			DataHandler handler = payload.getAnf();
			InputStream anf_is = handler.getInputStream();
			Integer processId = payload.getProcessId();
			String version = payload.getVersion();
			String nat_type = payload.getNativeType();
			String cpf_uri = payload.getCpfURI();
			ProcessDao.getInstance().storeAnnotation(annotationName, processId, 
					version, cpf_uri, nat_type, anf_is, isNew);
			result.setCode(0);
			result.setMessage("");
		} catch (IOException e) {
			result.setCode(-1);
			result.setMessage(e.getMessage());
		} catch (SQLException e) {
			result.setCode(-1);
			result.setMessage(e.getMessage());
		} catch (ExceptionDao e) {
			result.setCode(-1);
			result.setMessage(e.getMessage());
		} catch (ExceptionAnntotationName e) {
			result.setCode(-3);
			result.setMessage(e.getMessage());
		}
		return res;
	}


	public StoreVersionOutputMsgType storeVersion(StoreVersionInputMsgType payload) { 
		LOG.info("Executing operation storeVersion");
		System.out.println(payload);
		StoreVersionOutputMsgType res = new StoreVersionOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int processId = payload.getEditSession().getProcessId();
		String nativeType = payload.getEditSession().getNativeType();
		int editSessionCode = payload.getEditSessionCode();
		String cpf_uri = payload.getCpfURI();
		try {
			DataHandler handlernat = payload.getNative();
			InputStream native_is = handlernat.getInputStream();

			DataHandler handlercpf = payload.getCpf();
			InputStream cpf_is = handlercpf.getInputStream();

			DataHandler handleranf = payload.getAnf();
			InputStream anf_is = handleranf.getInputStream();
			ProcessDao.getInstance().storeVersion 
			(editSessionCode, processId, cpf_uri, nativeType, native_is, cpf_is, anf_is);
			result.setCode(0);
			result.setMessage("");
		} catch (ExceptionDao ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (SQLException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (IOException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (ExceptionStoreVersion ex) {
			result.setCode(-3);
			result.setMessage(ex.getMessage());
		} catch (ExceptionSyncNPF ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}


	public StoreNativeOutputMsgType storeNative(StoreNativeInputMsgType payload) { 
		LOG.info("Executing operation storeNative");
		System.out.println(payload);
		StoreNativeOutputMsgType res = new StoreNativeOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int processId = payload.getProcessId();
		String version = payload.getVersion();
		String nativeType = payload.getNativeType();

		try {
			DataHandler handler = payload.getNative();
			InputStream native_xml = handler.getInputStream();
			ProcessDao.getInstance().storeNative(nativeType, processId, version, native_xml);
			result.setCode(0);
			result.setMessage("");
		} catch (ExceptionDao ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (SQLException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (IOException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.data_access.service_canoniser.DACanoniserPortType#storeNativeCpf(org.apromore.data_access.model_canoniser.StoreNativeCpfInputMsgType  payload )*
	 */
	public StoreNativeCpfOutputMsgType storeNativeCpf(StoreNativeCpfInputMsgType payload) { 
		LOG.info("Executing operation storeNativeCpf");
		System.out.println(payload);

		StoreNativeCpfOutputMsgType res = new StoreNativeCpfOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		String username = payload.getEditSession().getUsername();
		String nativeType = payload.getEditSession().getNativeType();
		String processName = payload.getEditSession().getProcessName();
		String domain = payload.getEditSession().getDomain();
		String version = payload.getEditSession().getVersionName();
		String created = payload.getEditSession().getCreationDate();
		String lastupdate = payload.getEditSession().getLastUpdate();
		String cpf_uri = payload.getCpfURI();
		try {
			DataHandler handler = payload.getNative();
			InputStream process_xml = handler.getInputStream();
			DataHandler handlercpf = payload.getCpf();
			InputStream cpf_xml = handlercpf.getInputStream();
			DataHandler handleranf = payload.getAnf();
			InputStream anf_xml = handleranf.getInputStream();

			ProcessSummaryType process = ProcessDao.getInstance().storeNativeCpf(
					username, processName, cpf_uri, domain, 
					nativeType, version, created, lastupdate,
					process_xml, cpf_xml, anf_xml);
			res.setProcessSummary(process);
			result.setCode(0);
			result.setMessage("");
		} catch (ExceptionDao ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (SQLException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (IOException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

}
