package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical XOr Join Node.
 *
 * @author Cameron James
 */
public class XOrJoin extends Join implements IXOrJoin {

    /**
     * Empty constructor.
     */
    public XOrJoin() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public XOrJoin(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public XOrJoin(String label, String desc) {
        super(label, desc);
    }

}
