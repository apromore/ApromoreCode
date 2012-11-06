package org.apromore.graph.canonical;

/**
 * Class for CPF Object.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CanonicalObject extends NonFlowNode implements IObject, Cloneable {

    private String objectId;
    private String originalId;
    private String type;
    private boolean optional = false;
    private boolean consumed = false;
    private boolean configurable = false;

    private ObjectTypeEnum objType;


    /**
     * @return a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CanonicalObject() { }

    /**
     * @param newName of this
     * @return a new instance of this class where parent is set to the given one and label is set to <code>null</code>.
     */
    public CanonicalObject(String newName) {
        super(newName);
    }


    @Override
    public CanonicalObject clone() {
        CanonicalObject clone = null;
        clone = (CanonicalObject) super.clone();
        if (this.getId() != null) {
            clone.setId(this.getId());
        }
        if (this.objectId != null) {
            clone.setObjectId(this.objectId);
        }
        if (this.originalId != null) {
            clone.setOriginalId(this.originalId);
        }
        clone.setName(this.getName());
        clone.setType(this.type);
        clone.setOptional(this.optional);
        clone.setConsumed(this.consumed);
        clone.setConfigurable(this.configurable);

        clone.setObjectType(this.objType);
        clone.setAttributes(this.attributes);

        return clone;
    }


    /**
     * Return this {@link org.apromore.graph.canonical.IObject} ObjectId.
     * @return the ObjectId of the Resource
     */
    @Override
    public String getObjectId() {
        return objectId;
    }

    /**
     * Set if this {@link org.apromore.graph.canonical.IObject} ObjectId.
     * @param newObjectId the objectId
     */
    @Override
    public void setObjectId(String newObjectId) {
        objectId = newObjectId;
    }

    @Override
    public String getOriginalId() {
        return originalId;
    }

    @Override
    public void setOriginalId(String newOriginalId) {
        originalId = newOriginalId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(final String newType) {
        type = newType;
    }

    @Override
    public boolean getOptional() {
        return optional;
    }

    @Override
    public void setOptional(final boolean isOptional) {
        optional = isOptional;
    }

    @Override
    public boolean getConsumed() {
        return consumed;
    }

    @Override
    public void setConsumed(boolean isConsumed) {
        consumed = isConsumed;
    }

    @Override
    public boolean isConfigurable() {
        return configurable;
    }

    @Override
    public void setConfigurable(boolean newConfigurable) {
        configurable = newConfigurable;
    }


    @Override
    public ObjectTypeEnum getObjectType() {
        return objType;
    }

    @Override
    public void setObjectType(final ObjectTypeEnum newType) {
        objType = newType;
    }

}
