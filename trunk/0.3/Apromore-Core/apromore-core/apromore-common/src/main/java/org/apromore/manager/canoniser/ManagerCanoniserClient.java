package org.apromore.manager.canoniser;

import org.apromore.canoniser.service.CanoniserManager;
import org.apromore.exception.ExceptionCanoniseVersion;
import org.apromore.exception.ExceptionDeCanonise;
import org.apromore.exception.ExceptionGenerateAnnotation;
import org.apromore.exception.ExceptionImport;
import org.apromore.exception.ExceptionVersion;
import org.apromore.model.AnnotationsType;
import org.apromore.model.CanoniseProcessInputMsgType;
import org.apromore.model.CanoniseProcessOutputMsgType;
import org.apromore.model.CanoniseVersionInputMsgType;
import org.apromore.model.CanoniseVersionOutputMsgType;
import org.apromore.model.DeCanoniseProcessInputMsgType;
import org.apromore.model.DeCanoniseProcessOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.GenerateAnnotationInputMsgType;
import org.apromore.model.GenerateAnnotationOutputMsgType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ManagerCanoniserClient {

    @Autowired
	private CanoniserManager manager;


	/**
     *
	 */
	public ProcessSummaryType CanoniseProcess(String username, String processName, String cpfURI,
			String versionName, String nativeType, InputStream cpf, String domain, String documentation, 
			String created, String lastupdate, Boolean addFakeEvents) 
	            throws IOException, ExceptionImport {
		CanoniseProcessInputMsgType payload = new CanoniseProcessInputMsgType();
		EditSessionType editSession = new EditSessionType();
		editSession.setUsername(username);
		editSession.setNativeType(nativeType);
		editSession.setProcessName(processName);
		editSession.setVersionName(versionName);
		editSession.setDomain(domain);
		editSession.setCreationDate(created);
		editSession.setLastUpdate(lastupdate);
		payload.setEditSession(editSession);
		payload.setCpfUri(cpfURI);
		payload.setAddFakeEvents(addFakeEvents);
		DataSource source = new ByteArrayDataSource(cpf, "text/xml"); 
		payload.setProcessDescription(new DataHandler(source));

        CanoniseProcessOutputMsgType res = manager.canoniseProcess(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionImport (res.getResult().getMessage());
		} else {
			ProcessSummaryType processP = new ProcessSummaryType();
			processP.setDomain(res.getProcessSummary().getDomain());
			processP.setId(res.getProcessSummary().getId());
			processP.setLastVersion(res.getProcessSummary().getLastVersion());
			processP.setName(res.getProcessSummary().getName());
			processP.setOriginalNativeType(res.getProcessSummary().getOriginalNativeType());
			processP.setRanking(res.getProcessSummary().getRanking());
			processP.getVersionSummaries().clear();
			processP.setOwner(res.getProcessSummary().getOwner());
			Iterator it = res.getProcessSummary().getVersionSummaries().iterator();
			// normally, only one... consider many for future needs
			while (it.hasNext()) {
				VersionSummaryType first_versionP = new VersionSummaryType();
				VersionSummaryType versionC = (VersionSummaryType) it.next();
				first_versionP.setCreationDate(versionC.getCreationDate());
				first_versionP.setLastUpdate(versionC.getLastUpdate());
				first_versionP.setName(versionC.getName());
				first_versionP.setRanking(versionC.getRanking());
				first_versionP.setCreationDate(versionC.getCreationDate());
				first_versionP.setLastUpdate(versionC.getLastUpdate());
				for (int i=0; i<versionC.getAnnotations().size();i++) {
					AnnotationsType annotationsC = new AnnotationsType();
					annotationsC.getAnnotationName().addAll(versionC.getAnnotations().get(i).getAnnotationName());
					annotationsC.setNativeType(versionC.getAnnotations().get(i).getNativeType());
					first_versionP.getAnnotations().add(annotationsC);
				}
				processP.getVersionSummaries().add(first_versionP);
			}
			return processP;

		}
	}

	public InputStream DeCanonise(int processId, String version, String nativeType, InputStream cpf_is, InputStream anf_is)
	        throws IOException, ExceptionDeCanonise {
		DeCanoniseProcessInputMsgType payload = new DeCanoniseProcessInputMsgType();
		DataSource source_cpf = new ByteArrayDataSource(cpf_is, "text/xml");
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setNativeType(nativeType);
		payload.setCpf(new DataHandler(source_cpf));
		if (anf_is != null) {
			// given annotation must be used
			DataSource source_anf = new ByteArrayDataSource(anf_is, "text/xml");
			payload.setAnf(new DataHandler(source_anf));
		}
		DeCanoniseProcessOutputMsgType res = manager.deCanoniseProcess(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionDeCanonise (res.getResult().getMessage());
		} else {
			DataHandler handler = res.getNativeDescription();
			InputStream is = handler.getInputStream();
			return is;
		}
	}

	public void CanoniseVersion(Integer editSessionCode, EditSessionType editSession, String cpfURI,
			InputStream native_is) throws IOException, ExceptionCanoniseVersion, ExceptionVersion {
		CanoniseVersionInputMsgType payload = new CanoniseVersionInputMsgType();
		DataSource source = new ByteArrayDataSource(native_is, "text/xml");
		payload.setEditSessionCode(editSessionCode);
		payload.setNative(new DataHandler(source));
		payload.setEditSession(editSession);
		payload.setCpfUri(cpfURI);
		// send request to canoniser
		CanoniseVersionOutputMsgType res = manager.canoniseVersion(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionCanoniseVersion (res.getResult().getMessage());
		} else if (res.getResult().getCode() == -3) {
			throw new ExceptionVersion(res.getResult().getMessage());
		}
	}

	public void GenerateAnnotation(String annotName, Integer editSessionCode,
			Boolean isNew, Integer processId, String version, String nat_type, 
			InputStream native_is) throws IOException, ExceptionGenerateAnnotation {
		GenerateAnnotationInputMsgType payload = new GenerateAnnotationInputMsgType();
		DataSource source = new ByteArrayDataSource(native_is, "text/xml");
		payload.setEditSessionCode(editSessionCode);
		payload.setNative(new DataHandler(source));
		payload.setIsNew(isNew);
		payload.setAnnotationName(annotName);
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setNativeType(nat_type);
		// send request to canoniser
		GenerateAnnotationOutputMsgType res = manager.generateAnnotation(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionGenerateAnnotation (res.getResult().getMessage());
		}
	}



    public void setManager(CanoniserManager manager) {
        this.manager = manager;
    }
}
