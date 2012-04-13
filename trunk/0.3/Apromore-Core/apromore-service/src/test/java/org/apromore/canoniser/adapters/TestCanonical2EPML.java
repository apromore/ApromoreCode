package org.apromore.canoniser.adapters;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2EPML;
import org.apromore.exception.ExceptionStore;
import org.apromore.cpf.CanonicalProcessType;

import de.epml.TypeEPML;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCanonical2EPML {

    public TestCanonical2EPML() {}

    @Test
    public void testSomething() {
        assertTrue(true);
    }

	/**
	 * @param args
	 */
	public void main(String[] args) {
		//File cpf_file = new File("/home/fauvet/models/epml_models/SAP_1.cpf");
		//File anf_file = new File("/home/fauvet/models/epml_models/SAP_1.anf");
		File cpf_file = new File("work_package/04_Example-Workflow.cpf");
		File anf_file = new File("work_package/04_Example-Workflow.anf");		
		try {
			JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
			CanonicalProcessType cpf = rootElement.getValue();
			
			jc = JAXBContext.newInstance("org.apromore.anf");
			u = jc.createUnmarshaller();
			JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_file);
			AnnotationsType anf = anfRootElement.getValue();

			//Canonical2EPML canonical2epml_1 = new Canonical2EPML(cpf,true);
			
			jc = JAXBContext.newInstance("de.epml");
			
			//Marshaller m2 = jc.createMarshaller();
			//m2.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			//JAXBElement<TypeEPML> cprocRootElem2 = 
				//new de.epml.ObjectFactory().createEpml(canonical2epml_1.getEPML());
			//m2.marshal(cprocRootElem2, new File("XPDL_models/_111test.epml"));
			
			Canonical2EPML canonical2epml_2 = new Canonical2EPML (cpf, anf, true);
			
			Marshaller m1 = jc.createMarshaller();
			m1.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<TypeEPML> cprocRootElem1 = 
				new de.epml.ObjectFactory().createEpml(canonical2epml_2.getEPML());
			m1.marshal(cprocRootElem1, new File("work_package/04_Example-Workflow.epml"));
			
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
