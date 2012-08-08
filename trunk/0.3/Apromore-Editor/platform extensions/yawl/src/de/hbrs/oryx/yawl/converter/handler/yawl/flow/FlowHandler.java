/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl.flow;

import java.util.HashMap;

import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.oryxeditor.server.diagram.label.LabelSettings;
import org.yawlfoundation.yawl.elements.YFlow;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerImpl;
import de.hbrs.oryx.yawl.converter.layout.FlowLayout;
import de.hbrs.oryx.yawl.util.OryxUUID;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Converts the YAWL flows to Oryx edges
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class FlowHandler extends YAWLHandlerImpl {

	private final YFlow flow;

	public FlowHandler(YAWLConversionContext context, YFlow flow) {
		super(context);
		this.flow = flow;
	}

	@Override
	public void convert(String parentId) {
		if (YAWLUtils.isElementVisible(flow.getPriorElement()) && YAWLUtils.isElementVisible(flow.getNextElement())) {
			// Both elements are visible
			// -> draw a edge between them
			String priorElementID = flow.getPriorElement().getID();
			String nextElementID = flow.getNextElement().getID();
			getContext().getNet(parentId).addChildShape(convertFlow(parentId, priorElementID, nextElementID));
		} else if (YAWLUtils.isElementVisible(flow.getPriorElement()) && !YAWLUtils.isElementVisible(flow.getNextElement())) {
			// The next element is invisible (e.g. implicit condition)
			// -> draw a edge to the next element
			String priorElementID = flow.getPriorElement().getID();
			String nextElementID = YAWLUtils.getNextVisibleElement(flow).getID();
			getContext().getNet(parentId).addChildShape(convertFlow(parentId, priorElementID, nextElementID));
		}
	}

	private BasicShape convertFlow(String netId, String priorElementID, String nextElementID) {

		// YAWL flows do not have an ID
		String generatedId = OryxUUID.generate();
		BasicEdge flowShape = new BasicEdge(generatedId, "Flow");

		BasicShape priorShape = getContext().getShape(priorElementID);
		BasicShape nextShape = getContext().getShape(nextElementID);

		// Incomings and Outgoings are automatically configured in Signavio
		flowShape.connectToASource(priorShape);
		flowShape.connectToATarget(nextShape);

		FlowLayout flowLayout = getContext().getFlowLayout(netId, priorElementID, nextElementID);
		flowShape.setBounds(flowLayout.getBounds());
		flowShape.setDockers(flowLayout.getDockers());

		HashMap<String, String> props = new HashMap<String, String>();
		props.put("yawlid", priorElementID + "|-|" + nextElementID);
		props.put("name", flowLayout.getLabel());
		flowShape.setProperties(props);
		//TODO add label settings
		//flowShape.addLabelSetting(newSetting);
		return flowShape;
	}

}
