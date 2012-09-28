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
package org.apromore.canoniser.provider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProviderHelper;

/**
 * Providing the default CanoniserProvider implementation
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class CanoniserProviderImpl implements CanoniserProvider {

    private List<Canoniser> internalCanoniserList;

    protected List<Canoniser> getInternalCanoniserList() {
        return internalCanoniserList;
    }

    protected void setInternalCanoniserList(final List<Canoniser> canoniserList) {
        this.internalCanoniserList = canoniserList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#listAll()
     */
    @Override
    public final Collection<Canoniser> listAll() {
        return Collections.unmodifiableCollection(getInternalCanoniserList());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#listByNativeType(java.lang.String)
     */
    @Override
    public final Collection<Canoniser> listByNativeType(final String nativeType) {
        return Collections.unmodifiableCollection(findAllCanoniser(nativeType, null, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#listByNativeTypeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Collection<Canoniser> listByNativeTypeAndName(final String nativeType, final String name) {
        return Collections.unmodifiableCollection(findAllCanoniser(nativeType, name, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeType(java.lang.String)
     */
    @Override
    public final Canoniser findByNativeType(final String nativeType) throws PluginNotFoundException {
        return findByNativeTypeAndNameAndVersion(nativeType, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeTypeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Canoniser findByNativeTypeAndName(final String nativeType, final String name) throws PluginNotFoundException {
        return findByNativeTypeAndNameAndVersion(nativeType, name, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public final Canoniser findByNativeTypeAndNameAndVersion(final String nativeType, final String name, final String version)
            throws PluginNotFoundException {
        final List<Canoniser> resultList = findAllCanoniser(nativeType, name, version);
        if (!resultList.isEmpty()) {
            // Just return the first one
            return resultList.get(0);
        }
        throw new PluginNotFoundException("Could not find canoniser with name: " + ((name != null) ? name : "null") + " version: "
                + ((version != null) ? version : "null") + " nativeType: " + ((nativeType != null) ? nativeType : "null"));
    }

    /**
     * Returns a List of Canonisers with matching parameters.
     *
     * @param nativeType can be NULL
     * @param name can be NULL
     * @param version can be NULL
     * @return List of Canonisers or empty List
     */
    private List<Canoniser> findAllCanoniser(final String nativeType, final String name, final String version) {

        final List<Canoniser> cList = new ArrayList<Canoniser>();

        for (final Canoniser c : getInternalCanoniserList()) {
            if (PluginProviderHelper.compareNullable(nativeType, c.getNativeType()) && PluginProviderHelper.compareNullable(name, c.getName()) && PluginProviderHelper.compareNullable(version, c.getVersion())) {
                cList.add(c);
            }
        }
        return cList;
    }

}
