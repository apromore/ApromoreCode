/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.provider;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Canoniser API used by Apromore, to access the Canoniser Plugins
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface CanoniserProvider {

    /**
     * Convenience method to convert to canonical format with the first Canoniser that is found for given parameters.
     *
     * @param nativeType for example "EPML 2.0" or "YAWL 2.2"
     * @param nativeInput stream of the native format
     * @param annotationFormat list that the canonised Annotations are added to
     * @param canonicalFormat list that the canonised Canonical Processes are added to
     * @throws CanoniserException
     * @throws PluginNotFoundException
     */
    void canonise(String nativeType, InputStream nativeInput, List<AnnotationsType> annotationFormat,
            List<CanonicalProcessType> canonicalFormat) throws CanoniserException, PluginNotFoundException;

    /**
     * Convenience method to convert to canonical format with the first Canoniser that is found for given parameters.
     *
     * @param nativeType for example "EPML 2.0" or "YAWL 2.2"
     * @param name usually the full class name of the Canoniser
     * @param nativeInput stream of the native format
     * @param annotationFormat list that the canonised Annotations are added to
     * @param canonicalFormat list that the canonised Canonical Processes are added to
     * @throws CanoniserException
     * @throws PluginNotFoundException
     */
    void canonise(String nativeType, String name, InputStream nativeInput, List<AnnotationsType> annotationFormat,
            List<CanonicalProcessType> canonicalFormat) throws CanoniserException, PluginNotFoundException;

    /**
     * Convenience method to convert to canonical format with the first Canoniser that is found for given parameters.
     *
     * @param nativeType for example "EPML 2.0" or "YAWL 2.2"
     * @param name usually the full class name of the Canoniser
     * @param version usually the bundle version (X.Y.Z-CLASSIFIER)
     * @param nativeInput stream of the native format
     * @param annotationFormat list that the canonised Annotations are added to
     * @param canonicalFormat list that the canonised Canonical Processes are added to
     * @throws CanoniserException
     * @throws PluginNotFoundException
     */
    void canonise(final String nativeType, String name, String version, final InputStream nativeInput, List<AnnotationsType> annotationFormat,
            List<CanonicalProcessType> canonicalFormat) throws CanoniserException, PluginNotFoundException;

    /**
     * Convenience method to convert to canonical format with the first Canoniser that is found for given parameters.
     *
     * @param nativeType for example "EPML 2.0" or "YAWL 2.2"
     * @param annotationFormat list of Annotations to deCanonise
     * @param canonicalFormat list of Canonical Process Types to deCanonise
     * @param nativeOutput stream of the native format
     * @throws CanoniserException
     * @throws PluginNotFoundException
     */
    void deCanonise(final String nativeType, final AnnotationsType annotationFormat, final CanonicalProcessType canonicalFormat,
            OutputStream nativeOutput) throws CanoniserException, PluginNotFoundException;

    /**
     * Convenience method to convert to canonical format with the first Canoniser that is found for given parameters.
     *
     * @param nativeType for example "EPML 2.0" or "YAWL 2.2"
     * @param name usually the full class name of the Canoniser
     * @param annotationFormat list of Annotations to deCanonise
     * @param canonicalFormat list of Canonical Process Types to deCanonise
     * @param nativeOutput stream of the native format
     * @throws CanoniserException
     * @throws PluginNotFoundException
     */
    void deCanonise(String nativeType, String name, AnnotationsType annotationFormat,
            final CanonicalProcessType canonicalFormat, OutputStream nativeOutput) throws CanoniserException, PluginNotFoundException;

    /**
     * Convenience method to convert to canonical format with the first Canoniser that is found for given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @param version
     *            usually the bundle version (X.Y.Z-CLASSIFIER)
     * @param annotationFormat
     *            list of Annotations to deCanonise
     * @param canonicalFormat
     *            list of Canonical Process Types to deCanonise
     * @param nativeOutput
     *            stream of the native format
     * @throws CanoniserException
     * @throws PluginNotFoundException
     */
    void deCanonise(String nativeType, String name, String version, AnnotationsType annotationFormat,
            final CanonicalProcessType canonicalFormat, OutputStream nativeOutput) throws CanoniserException, PluginNotFoundException;

    /**
     * List all available Canoniser
     *
     * @return Collection of Canoniser
     */
    Collection<Canoniser> listAll();

    /**
     * List all available Canoniser converting the specified native type.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @return Collection of Canoniser
     */
    Collection<Canoniser> listByNativeType(String nativeType);

    /**
     * List all available Canoniser converting the specified native type with the exact name. Please note there could be multiple versions installed,
     * so a Collection will be returned.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @return Collection of Canoniser
     */
    Collection<Canoniser> listByNativeTypeAndName(String nativeType, String name);

    /**
     * Return the first Canoniser that is found with the given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @return Canoniser for given native type
     * @throws PluginNotFoundException
     *             in case there is no Canoniser found
     */
    Canoniser findByNativeType(String nativeType) throws PluginNotFoundException;

    /**
     * Return the first Canoniser that is found with the given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @return Canoniser for given native type and name
     * @throws PluginNotFoundException
     *             in case there is no Canoniser found
     */
    Canoniser findByNativeTypeAndName(String nativeType, String name) throws PluginNotFoundException;

    /**
     * Return the first Canoniser that is found with the given parameters.
     *
     * @param nativeType
     *            for example "EPML 2.0" or "YAWL 2.2"
     * @param name
     *            usually the full class name of the Canoniser
     * @param version
     *            usually the bundle version (X.Y.Z-CLASSIFIER)
     * @return Canoniser for given native type and name and version
     * @throws PluginNotFoundException
     *             in case there is no Canoniser found
     */
    Canoniser findByNativeTypeAndNameAndVersion(String nativeType, String name, String version) throws PluginNotFoundException;

}
