/**
 *  Copyright 2012, Felix Mannhardt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.property;

/**
 * Property of a Plugin
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PropertyType {

	/**
	 * @return
	 */
	String getName();

	/**
	 * @return
	 */
	Class<?> getValueType();

	/**
	 * @return
	 */
	Boolean isMandatory();

	/**
	 * @return
	 */
	String getDescription();

	/**
	 * @return
	 */
	Object getValue();

	/**
	 * @param value
	 */
	void setValue(Object value);

	/**
	 * @return
	 */
	boolean hasValue();
}
