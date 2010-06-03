
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.portal.service_oryx;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_oryx.CloseSessionInputMsgType;
import org.apromore.portal.model_oryx.CloseSessionOutputMsgType;
import org.apromore.portal.model_oryx.ReadNativeInputMsgType;
import org.apromore.portal.model_oryx.ReadNativeOutputMsgType;
import org.apromore.portal.model_oryx.ResultType;
import org.apromore.portal.model_oryx.WriteNewProcessInputMsgType;
import org.apromore.portal.model_oryx.WriteNewProcessOutputMsgType;
import org.apromore.portal.model_oryx.WriteProcessInputMsgType;
import org.apromore.portal.model_oryx.WriteProcessOutputMsgType;

/**
 * This class was generated by Apache CXF 2.2.7
 * Thu Jun 03 09:26:30 EST 2010
 * Generated source version: 2.2.7
 * 
 */

@javax.jws.WebService(
                      serviceName = "PortalOryxService",
                      portName = "PortalOryx",
                      targetNamespace = "http://www.apromore.org/portal/service_oryx",
                      wsdlLocation = "http://localhost:8080/Apromore-portal/services/PortalOryx?wsdl",
                      endpointInterface = "org.apromore.portal.service_oryx.PortalOryxPortType")

		public class PortalOryxPortTypeImpl implements PortalOryxPortType {

	private static final Logger LOG = Logger.getLogger(PortalOryxPortTypeImpl.class.getName());


	public CloseSessionOutputMsgType closeSession(CloseSessionInputMsgType payload) { 
        LOG.info("Executing operation closeSession");
        System.out.println(payload);
        CloseSessionOutputMsgType res = new CloseSessionOutputMsgType();

		ResultType result = new ResultType();
		res.setResult(result);
		int code = payload.getCode();
        try {
			// delete edit session
			RequestToManager request = new RequestToManager();
			request.DeleteEditionSession(code);
			result.setCode(0);
			result.setMessage("");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
    }

	/* (non-Javadoc)
	 * @see org.apromore.portal.service_oryx.PortalOryxPortType#writeProcess(org.apromore.portal.model_oryx.WriteProcessInputMsgType  payload )*
	 */
	public WriteProcessOutputMsgType writeProcess(WriteProcessInputMsgType payload) { 
		LOG.info("Executing operation writeProcess");
		System.out.println(payload);
		WriteProcessOutputMsgType res = new WriteProcessOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int code = payload.getEditSessionCode();
		String new_versionName = payload.getVersionName();
		try {
			DataHandler handler = payload.getNative();
			InputStream native_is = handler.getInputStream();
			RequestToManager request = new RequestToManager();
			// request details associated with edit session
			EditSessionType editSession = request.ReadEditSession(code);
			String username = editSession.getUsername();
			int processId = editSession.getProcessId();
			String nativeType = editSession.getNativeType();
			String domain = editSession.getDomain();
			String versionName = editSession.getVersionName();
			// update process: create new version (new_versionName) derived from versionName
			request.UpdateProcess (username, nativeType, processId, versionName, new_versionName, native_is, domain);
			result.setCode(0);
			result.setMessage("");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.portal.service_oryx.PortalOryxPortType#writeNewProcess(org.apromore.portal.model_oryx.WriteNewProcessInputMsgType  payload )*
	 */
	public WriteNewProcessOutputMsgType writeNewProcess(WriteNewProcessInputMsgType payload) { 
		LOG.info("Executing operation writeNewProcess");
		System.out.println(payload);
		WriteNewProcessOutputMsgType res = new WriteNewProcessOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int code = payload.getEditSessionCode();
		
		try {
			DataHandler handler = payload.getNative();
			InputStream native_is = handler.getInputStream();
			RequestToManager request = new RequestToManager();
			// request details associated with edit session
			EditSessionType editSession = request.ReadEditSession(code);
			String username = editSession.getUsername();
			String nativeType = editSession.getNativeType();
			String domain = editSession.getDomain();
			String new_processName = payload.getProcessName();
			String new_versionName = payload.getVersionName();
			// import native for corresponding process version
			request.ImportModel(username, nativeType, new_processName, new_versionName, native_is, domain);
			// delete edit session
			request.DeleteEditionSession(code);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.portal.service_oryx.PortalOryxPortType#readNative(org.apromore.portal.model_oryx.ReadNativeInputMsgType  payload )*
	 */
	public ReadNativeOutputMsgType readNative(ReadNativeInputMsgType payload) { 
		LOG.info("Executing operation readNative");
		System.out.println(payload);
		ReadNativeOutputMsgType res = new ReadNativeOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int code = payload.getEditSessionCode();
		
		try {
			// request the edit session identified by code
			RequestToManager request = new RequestToManager();
			EditSessionType editSession = request.ReadEditSession(code);
			int processId = editSession.getProcessId();
			String version = editSession.getVersionName();
			String nativeType = editSession.getNativeType();
			String processName = editSession.getProcessName();
			// request native 
			InputStream native_is = 
				request.ExportNative(processId, version, nativeType);
			DataSource sourceNative = new ByteArrayDataSource(native_is, "text/xml"); 
			res.setNative(new DataHandler(sourceNative));	
			
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

}
