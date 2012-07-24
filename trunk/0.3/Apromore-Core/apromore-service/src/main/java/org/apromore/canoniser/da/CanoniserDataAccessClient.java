package org.apromore.canoniser.da;

import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.dao.DataAccessCanoniserManager;
import org.apromore.exception.ExceptionAnnotation;
import org.apromore.exception.ExceptionCpfUri;
import org.apromore.exception.ExceptionStore;
import org.apromore.exception.ExceptionVersion;
import org.apromore.model.EditSessionType;
import org.apromore.model.GetCpfUriInputMsgType;
import org.apromore.model.GetCpfUriOutputMsgType;
import org.apromore.model.StoreNativeInputMsgType;
import org.apromore.model.StoreNativeOutputMsgType;
import org.apromore.model.StoreVersionInputMsgType;
import org.apromore.model.StoreVersionOutputMsgType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;

public class CanoniserDataAccessClient {

    private DataAccessCanoniserManager manager;

    public void StoreNative(int processId, String version, String nativeType, InputStream process)
            throws IOException, ExceptionStore {
        StoreNativeInputMsgType payload = new StoreNativeInputMsgType();
        payload.setProcessId(processId);
        payload.setVersion(version);
        payload.setNativeType(nativeType);
        DataSource source_native = new ByteArrayDataSource(process, "text/xml");
        payload.setNative(new DataHandler(source_native));
        StoreNativeOutputMsgType res = manager.storeNative(payload);
        if (res.getResult().getCode() == -1) {
            throw new ExceptionStore(res.getResult().getMessage());
        }
    }

    public void StoreVersion(int editSessionCode, EditSessionType editSession,
                             String cpfURI, InputStream native_is, InputStream anf_xml_is,
                             InputStream cpf_xml_is) throws IOException, ExceptionStore, ExceptionVersion {
        StoreVersionInputMsgType payload = new StoreVersionInputMsgType();
        payload.setCpfURI(cpfURI);
        payload.setEditSession(editSession);
        payload.setEditSessionCode(editSessionCode);
        DataSource source_proc = new ByteArrayDataSource(native_is, "text/xml");
        payload.setNative(new DataHandler(source_proc));
        DataSource source_cpf = new ByteArrayDataSource(cpf_xml_is, "text/xml");
        payload.setCpf(new DataHandler(source_cpf));
        DataSource source_anf = new ByteArrayDataSource(anf_xml_is, "text/xml");
        payload.setAnf(new DataHandler(source_anf));
        StoreVersionOutputMsgType res = manager.storeVersion(payload);
        if (res.getResult().getCode() == -1) {
            throw new ExceptionStore(res.getResult().getMessage());
        } else if (res.getResult().getCode() == -3) {
            throw new ExceptionVersion(res.getResult().getMessage());
        }
    }

    public void WriteAnnotation(Integer editSessionCode, String annotationName,
                                Boolean isNew, Integer processId, String version, String cpfUri,
                                String nativeType, InputStream inputStream, InputStream anf_is) throws IOException, ExceptionAnnotation {
        WriteAnnotationInputMsgType payload = new WriteAnnotationInputMsgType();
        payload.setAnnotationName(annotationName);
        payload.setEditSessionCode(editSessionCode);
        payload.setProcessId(processId);
        payload.setVersion(version);
        payload.setIsNew(isNew);
        payload.setNativeType(nativeType);
        payload.setCpfURI(cpfUri);
        DataSource source_anf = new ByteArrayDataSource(anf_is, "text/xml");
        payload.setAnf(new DataHandler(source_anf));
        WriteAnnotationOutputMsgType res = manager.writeAnnotation(payload);
        if (res.getResult().getCode() == -1) {
            throw new ExceptionAnnotation(res.getResult().getMessage());
        }
    }

    public String GetCpfUri(Integer processId, String version) throws ExceptionCpfUri {
        GetCpfUriInputMsgType payload = new GetCpfUriInputMsgType();
        payload.setProcessId(processId);
        payload.setVersion(version);
        GetCpfUriOutputMsgType res = manager.getCpfUri(payload);
        if (res.getResult().getCode() == -1) {
            throw new ExceptionCpfUri(res.getResult().getMessage());
        }
        return res.getCpfURI();
    }


    public void setManager(DataAccessCanoniserManager manager) {
        this.manager = manager;
    }
}
