package org.apromore.cpf;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Utilities for working with the CPF (Validation/Parsing)
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public class CPFSchema {

    public static final String CPF_CONTEXT = "org.apromore.cpf";

    /**
     * URI used for XPath
     */
    public static final String EXPRESSION_LANGUAGE_XPATH = "http://www.w3.org/1999/xpath/";

    /**
     * URI used for XQuery
     */
    public static final String EXPRESSION_LANGUAGE_XQUERY = "http://www.w3.org/2005/xpath-functions/";

    /**
     * URI used for MVEL
     */
    public static final String EXPRESSION_LANGUAGE_MVEL = "http://www.mvel.org/2.0";

    /**
     * URI used for Resource Runtime Expressions
     */
    public static final String EXPRESSION_LANGUAGE_APROMORE_RESOURCE_RUNTIME = "http://www.apromore.org/expressions/resources/runtime";


    private static final String CPF_SCHEMA_LOCATION = "/xsd/cpf_1.0.xsd";


    /**
     * Schema of CPF
     *
     * @return
     * @throws SAXException
     */
    public static Schema getCPFSchema() throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(CPFSchema.class.getResource(CPF_SCHEMA_LOCATION));
        return schema;
    }

    /**
     * Validator for CPF
     *
     * @return
     * @throws SAXException
     */
    public static Validator getCPFValidator() throws SAXException {
        return getCPFSchema().newValidator();
    }

    /**
     * Marshal the Canonical Process Format into the provided OutputStream.
     *
     * @param canonicalFormat
     * @param cpf
     * @throws JAXBException
     * @throws PropertyException
     * @throws SAXException
     */
    public static void marshalCanoncialFormat(final OutputStream canonicalFormat, final CanonicalProcessType cpf, final boolean isValidating) throws JAXBException, PropertyException, SAXException {
        final JAXBContext context = JAXBContext.newInstance(CPF_CONTEXT);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (isValidating) {
            marshaller.setSchema(getCPFSchema());
        }
        final JAXBElement<CanonicalProcessType> cproc_cpf = new ObjectFactory().createCanonicalProcess(cpf);
        marshaller.marshal(cproc_cpf, canonicalFormat);
    }


    /**
     * Unmarshal the Canonical Process Format from the provided InputStream.
     *
     * @param canonicalFormat
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    public static JAXBElement<CanonicalProcessType> unmarshalCanonicalFormat(final InputStream canonicalFormat, final boolean isValidating) throws JAXBException, SAXException {
        final JAXBContext jc = JAXBContext.newInstance(CPF_CONTEXT);
        final Unmarshaller u = jc.createUnmarshaller();
        if (isValidating) {
            u.setSchema(getCPFSchema());
        }
        return (JAXBElement<CanonicalProcessType>) u.unmarshal(canonicalFormat);
    }

    /**
     * Creates a valid output expression taking the target Object and the actual expression in an arbitrary language.
     *
     * @param netObject the result of the expression will be assigned to
     * @param expression a String with an arbitrary expression
     * @return a new expression in the form of 'netObjectName = expression'
     */
    public static String createOuputExpression(final String netObjectName, final String expression) {
        return netObjectName + " = " + expression;
    }

    /**
     * Creates a valid input expression taking the target variable (Task Object) name and the actual expression in an arbitrary language.
     *
     * @param taskObjectName name of the internal variable that is mapped to
     * @param expression a String with an arbitrary expression
     * @return a new expression in the form of 'taskObjectName = expression'
     */
    public static String createInputExpression(final String taskObjectName, final String expression) {
        return taskObjectName + " = " + expression;
    }

    /**
     * Gets the name of the Task Object from an input expression.
     *
     * @param expression a valid input expression
     * @return object name
     */
    public static String getTaskObjectName(final String expression) {
        String[] splittedExpr = expression.split(" = ");
        if (splittedExpr.length > 1) {
            return splittedExpr[0];
        } else {
            throw new IllegalArgumentException("Invalid expression as input");
        }
    }

    /**
     * Gets the name of the Net Object from an output expression.
     *
     * @param expression a valid output expression
     * @return object name
     */
    public static String getNetObjectName(final String expression) {
        // For now the same
        return getTaskObjectName(expression);
    }

    /**
     * Returns the canonical XPath expression that references the given cpfObject.
     *
     * <pre>
     * cpf:getObjectValue(%s)
     * </pre>
     *
     * @param objectName that is referenced
     * @return canonical XPath expression
     */
    public static String createGetObjectValueExpr(final String objectName) {
        return String.format("cpf:getObjectValue('%s')", objectName);
    }

    /**
     * Returns the canonical XPath expression that references the given internal Object of a Task.
     *
     * <pre>
     * cpf:getTaskObjectValue(%s)
     * </pre>
     *
     * @param taskObjectName that is referenced
     * @return canonical XPath expression
     */
    public static String createGetTaskObjectValueExpr(final String taskObjectName) {
        return String.format("cpf:getTaskObjectValue('%s')", taskObjectName);
    }

}
