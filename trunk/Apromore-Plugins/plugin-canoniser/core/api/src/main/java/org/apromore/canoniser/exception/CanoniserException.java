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
package org.apromore.canoniser.exception;

import org.apromore.plugin.exception.PluginException;

/**
 * Thrown if a Canoniser can not proceed with the conversion.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public class CanoniserException extends PluginException {

	private static final long serialVersionUID = 4207728954089608127L;

	public CanoniserException() {
		super();
	}

	public CanoniserException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CanoniserException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CanoniserException(final String message) {
		super(message);
	}

	public CanoniserException(final Throwable cause) {
		super(cause);
	}

}
