/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler;

/**
 * Abstract base class for all Task BasicShape conversions
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public abstract class OryxTaskHandler extends OryxNetElementHandler {

	public OryxTaskHandler(OryxConversionContext context, BasicShape shape) {
		super(context, shape);
	}

	/**
	 * Return the YAWL connector type
	 * 
	 * @param oryxType
	 * @return connector type as specified in YTask
	 */
	protected int convertConnectorType(String oryxType) {

		// NULL means no Decorator/Connector added on import
		if (oryxType == null
				|| oryxType.equalsIgnoreCase(TaskHandler.NONE_CONNECTOR)) {
			// TODO which value should be used?
			return -1;
		}

		// Remove information about position (skip last character!)
		oryxType = oryxType.substring(0, oryxType.length() - 1);

		if (oryxType.equalsIgnoreCase(TaskHandler.AND_CONNECTOR)) {
			return YTask._AND;
		} else if (oryxType.equalsIgnoreCase(TaskHandler.OR_CONNECTOR)) {
			return YTask._OR;
		} else if (oryxType.equalsIgnoreCase(TaskHandler.XOR_CONNECTOR)) {
			return YTask._XOR;
		}
		return -1;
	}

}
