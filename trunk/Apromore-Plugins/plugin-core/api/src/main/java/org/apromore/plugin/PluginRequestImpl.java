package org.apromore.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.RequestParameterType;

/**
 * Default implementation of the {@link PluginResult} interface providing management of {@link RequestParameterType} that are used by the consumer of a
 * Plugin.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class PluginRequestImpl implements PluginRequest {

    /**
     * Map of request properties by their ID
     */
    private Map<String, ParameterType<?>> requestProperties;

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PluginRequest#getRequestProperty(org.apromore.plugin.property.PropertyType)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ParameterType<T> getRequestParameter(final ParameterType<T> pluginProperty) throws PluginPropertyNotFoundException {
        initRequestProperties();
        ParameterType<?> propertyType = requestProperties.get(pluginProperty.getId());
        if (propertyType != null) {
            if (pluginProperty.getValueType().getClass().isInstance(propertyType.getValueType())) {
                return (ParameterType<T>) propertyType;
            } else {
                throw new IllegalArgumentException("Property types do not match " + pluginProperty.getValueType() + " and "
                        + propertyType.getValueType() + " for property with ID " + pluginProperty.getId());
            }
        } else {
            if (pluginProperty.isMandatory()) {
                throw new PluginPropertyNotFoundException("Mandatory property with ID " + pluginProperty.getId() + " not found!");
            } else {
                return pluginProperty;
            }
        }
    }

    /**
     * Add request property to current PluginRequest
     *
     * @param requestProperty
     */
    public void addRequestProperty(final RequestParameterType<?> requestProperty) {
        initRequestProperties();
        requestProperties.put(requestProperty.getId(), requestProperty);
    }

    /**
     * Adds all request properties to current PluginRequest
     *
     * @param requestProperties
     */
    public void addRequestProperty(final Set<RequestParameterType<?>> requestProperties) {
        for (RequestParameterType<?> requestProperty : requestProperties) {
            addRequestProperty(requestProperty);
        }
    }

    private void initRequestProperties() {
        if (requestProperties == null) {
            requestProperties = new HashMap<String, ParameterType<?>>();
        }
    }

}
