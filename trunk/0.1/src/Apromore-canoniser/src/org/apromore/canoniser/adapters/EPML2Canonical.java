package org.apromore.canoniser.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.FillType;
import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

import de.epml.TEpcElement;
import de.epml.TExtensibleElements;
import de.epml.TypeAND;
import de.epml.TypeArc;
import de.epml.TypeAttrTypes;
import de.epml.TypeCFunction;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeEvent;
import de.epml.TypeFlow;
import de.epml.TypeFont;
import de.epml.TypeFunction;
import de.epml.TypeLine;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypeOR;
import de.epml.TypeObject;
import de.epml.TypeRole;
import de.epml.TypeXOR;

public class EPML2Canonical{
	Map<BigInteger, BigInteger> id_map = new HashMap<BigInteger, BigInteger>();
	List<BigInteger> flow_source_id_list = new LinkedList<BigInteger>();
	List<TypeAND> and_list = new LinkedList<TypeAND>();
	List<TypeOR> or_list = new LinkedList<TypeOR>();
	List<TypeXOR> xor_list = new LinkedList<TypeXOR>();
	List<TypeRole> role_list = new LinkedList<TypeRole>();
	List<TypeObject> object_list = new LinkedList<TypeObject>();
	private CanonicalProcessType cproc = new CanonicalProcessType();
	private AnnotationsType annotations = new AnnotationsType();
	private JAXBContext jaxbContext = null;
	private JAXBContext jaxbContext2 = null;
	private Unmarshaller unmarshaller = null;
	private Marshaller marshaller = null;
	private TypeEPML epml = null;
	private long ids = 1;
	FileInputStream fis;
	//
	
	public CanonicalProcessType getCPF()
	{
		return cproc;
	}
	
	public AnnotationsType getANF()
	{
		return annotations;
	}
	
	public EPML2Canonical(TypeEPML epml) throws JAXBException {
	
		for (TExtensibleElements epc: epml.getDirectory().get(0).getEpcOrDirectory()) {
			NetType net = new NetType();
			translateEpc(net,(TypeEPC)epc);
			net.setId(BigInteger.valueOf(ids++));
			cproc.getNet().add(net);
		}
	}
	
	private void translateEpc(NetType net, TypeEPC epc)
	{
		for (Object obj: epc.getEventOrFunctionOrRole()) {
			//System.out.println(obj.getClass());
			if (obj instanceof TypeEvent) {
				translateEvent(net, (TypeEvent) obj);
				addNodeAnnotations(obj);
			} else if (obj instanceof TypeFunction) {
				translateFunction(net, (TypeFunction) obj);
				addNodeAnnotations(obj);
			} else if(obj instanceof TypeAND || obj instanceof TypeOR || obj instanceof TypeXOR)
			{
				translateGateway(net, obj);
				addNodeAnnotations(obj);
			}
			else if(obj instanceof TypeRole)
			{
				role_list.add((TypeRole) obj);
			}
			else if(obj instanceof TypeObject)
			{
				object_list.add((TypeObject) obj);
			}
		}
		
		translateRoles();
		translateObjects();
		
		for (Object obj: epc.getEventOrFunctionOrRole()) {
			if (obj instanceof TypeArc) {
				translateArc(net, (TypeArc) obj);
				addEdgeAnnotation((TypeArc)obj);
			}
		}
		
		//process the gateway lists
		int counter;
		for(TypeAND and: and_list)
		{
			counter = 0;
			BigInteger n = and.getId();
			for(BigInteger s: flow_source_id_list)
				if(n.equals(s))
					counter++;
			if(counter == 1)
				//TODO
				//the and is joint
			{
				ANDJoinType andJ = new ANDJoinType();
				andJ.setId(and.getId());
				andJ.setName(and.getName());
				net.getNode().add(andJ);
			}
			else
				//TODO
				//the and is split, create it
			{
				ANDSplitType andS = new ANDSplitType();
				andS.setId(and.getId());
				andS.setName(and.getName());
				net.getNode().add(andS);
			}
		}
		
		/// make the same for or 
		for(TypeOR or: or_list)
		{
			counter = 0;
			BigInteger n = or.getId();
			for(BigInteger s: flow_source_id_list)
				if(n.equals(s))
					counter++;
			if(counter == 1)
				//TODO
				//the or is joint
			{
				ORJoinType orJ = new ORJoinType();
				orJ.setId(or.getId());
				orJ.setName(or.getName());
				net.getNode().add(orJ);
			}
			else
				//TODO
				//or is split, create it then remove the events after
			{
				ORSplitType orS = new ORSplitType();
				orS.setId(or.getId());
				orS.setName(or.getName());
				net.getNode().add(orS);
				processUnrequiredEvents(net,or.getId()); // after creating the split node ,, delete the event
			}
		}
		
		// make the same for xor
		for(TypeXOR xor: xor_list)
		{
			counter = 0;
			BigInteger n = xor.getId();
			for(BigInteger s: flow_source_id_list)
				if(n.equals(s))
					counter++;
			if(counter == 1)
				//TODO
				// xor is joint
			{
				XORJoinType xorJ = new XORJoinType();
				xorJ.setId(xor.getId());
				xorJ.setName(xor.getName());
				net.getNode().add(xorJ);
			}
			else
				//TODO
				//xor is split, create it
			{
				XORSplitType xorS = new XORSplitType();
				xorS.setId(xor.getId());
				xorS.setName(xor.getName());
				net.getNode().add(xorS);
				processUnrequiredEvents(net,xor.getId()); // after creating the split node ,, delete the event
			}
		}
		
		// find the edge after the split
		// and remove the event
		//TODO

		
	}

	private void addEdgeAnnotation(TypeArc arc) {
		
		LineType line = new LineType();
		GraphicsType graph = new GraphicsType();
		FontType font = new FontType();
		
		if(arc.getGraphics() != null)
		{
			graph.setCpfId(arc.getId());
			if(arc.getGraphics().get(0) != null)
			{
				if(arc.getGraphics().get(0).getFont() != null)
				{
					font.setColor(arc.getGraphics().get(0).getFont().getColor());
					font.setDecoration(arc.getGraphics().get(0).getFont().getDecoration());
					font.setFamily(arc.getGraphics().get(0).getFont().getFamily());
					font.setHorizontalAlign(arc.getGraphics().get(0).getFont().getHorizontalAlign());
					font.setRotation(arc.getGraphics().get(0).getFont().getRotation());
					font.setSize(arc.getGraphics().get(0).getFont().getSize());
					font.setStyle(arc.getGraphics().get(0).getFont().getStyle());
					font.setVerticalAlign(arc.getGraphics().get(0).getFont().getVerticalAlign());
					font.setWeight(arc.getGraphics().get(0).getFont().getWeight());
					graph.setFont(font);
				}
				if(arc.getGraphics().get(0).getLine() != null)
				{
					line.setColor(arc.getGraphics().get(0).getLine().getColor());
					line.setShape(arc.getGraphics().get(0).getLine().getShape());
					line.setStyle(arc.getGraphics().get(0).getLine().getStyle());
					line.setWidth(arc.getGraphics().get(0).getLine().getWidth());
					graph.setLine(line);
				}
				
				for(TypeMove2 mov2: arc.getGraphics().get(0).getPosition())
				{
					PositionType pos = new PositionType();
					pos.setX(mov2.getX());
					pos.setY(mov2.getY());
					graph.getPosition().add(pos);
				}
			}
			annotations.getAnnotation().add(graph);
		}
	}

	private void addNodeAnnotations(Object obj) {
		AnnotationType annotation = new AnnotationType();
		GraphicsType graphT = new GraphicsType();
		LineType line = new LineType();
		FillType fill = new FillType();
		PositionType pos = new PositionType();
		SizeType size = new SizeType();
		FontType font = new FontType();
		BigInteger cpfId = null;
		
		// 
		
		TEpcElement element = (TEpcElement)obj;
		cpfId = id_map.get(element.getId());
		
		if(element.getGraphics().getFill() != null) {
			fill.setColor(element.getGraphics().getFill().getColor());
			fill.setGradientColor(element.getGraphics().getFill().getGradientColor());
			fill.setGradientRotation(element.getGraphics().getFill().getGradientRotation());
			fill.setImage(element.getGraphics().getFill().getImage());
			graphT.setFill(fill);

		} 
		
		if(element.getGraphics().getPosition() != null) {
			size.setHeight(element.getGraphics().getPosition().getHeight());
			size.setWidth(element.getGraphics().getPosition().getWidth());
			graphT.setSize(size);
			
			pos.setX(element.getGraphics().getPosition().getX());
			pos.setY(element.getGraphics().getPosition().getY());
			graphT.getPosition().add(pos);
		} 
		
		if(element.getGraphics().getLine() != null) {
			line.setColor(element.getGraphics().getLine().getColor());
			line.setShape(element.getGraphics().getLine().getShape());
			line.setStyle(element.getGraphics().getLine().getStyle());
			line.setWidth(element.getGraphics().getLine().getWidth());
			graphT.setLine(line);
		} 
		
		if(element.getGraphics().getFont() != null) {
			font.setColor(element.getGraphics().getFont().getColor());
			font.setDecoration(element.getGraphics().getFont().getDecoration());
			font.setFamily(element.getGraphics().getFont().getFamily());
			font.setHorizontalAlign(element.getGraphics().getFont().getHorizontalAlign());
			font.setRotation(element.getGraphics().getFont().getRotation());
			font.setSize(element.getGraphics().getFont().getSize());
			font.setStyle(element.getGraphics().getFont().getStyle());
			font.setVerticalAlign(element.getGraphics().getFont().getVerticalAlign());
			font.setWeight(element.getGraphics().getFont().getWeight());
			graphT.setFont(font);
		}

		graphT.setCpfId(cpfId);
		annotations.getAnnotation().add(graphT);
	}

	// should be in the end
	
	private void processUnrequiredEvents(NetType net, BigInteger id)
	{
		BigInteger event_id;
		for(EdgeType edge: net.getEdge())
			if(edge.getSourceId().equals(id))
			{
				event_id = edge.getTargetId();
				for(EdgeType edge2: net.getEdge())
					if(edge2.getSourceId().equals(edge.getTargetId()))
					{
						edge.setTargetId(edge2.getTargetId());
						net.getEdge().remove(edge2);
					}
				
				// delete the unrequired event and set its name as a condition for the edge
				for(NodeType node: net.getNode())
					if(node.getId().equals(event_id))
					{
						edge.setCondition(node.getName());
						net.getNode().remove(node);
					}
			}
						
	}
	
	private void translateEvent(NetType net, TypeEvent event)
	{
		EventType node = new EventType();
		id_map.put(event.getId(), BigInteger.valueOf(ids));
		node.setId(BigInteger.valueOf(ids++));
		node.setName(event.getName());
		net.getNode().add(node);	
	}
	
	private void translateFunction(NetType net, TypeFunction func)
	{
		TaskType task = new TaskType();
		id_map.put(func.getId(), BigInteger.valueOf(ids));
		task.setId(BigInteger.valueOf(ids++));
		task.setName(func.getName());
		net.getNode().add(task);	
	}
	
	private void translateArc(NetType net, TypeArc arc)
	{
		if(arc.getFlow() != null) // if it is null, that's mean the arc is relation , not processed yet
		{
			EdgeType edge = new EdgeType();
			id_map.put(arc.getId(), BigInteger.valueOf(ids));
			edge.setId(BigInteger.valueOf(ids++));	
			edge.setSourceId(id_map.get(arc.getFlow().getSource()));
			edge.setTargetId(id_map.get(arc.getFlow().getTarget()));
			net.getEdge().add(edge);
			flow_source_id_list.add(edge.getSourceId());
		}
	}
	
	private void translateGateway(NetType net, Object object)
	{
		if (object instanceof TypeAND) {
			id_map.put(((TypeAND) object).getId(), BigInteger.valueOf(ids));
			((TypeAND) object).setId(BigInteger.valueOf(ids++));
			and_list.add((TypeAND) object);
		} else if (object instanceof TypeOR) {
			id_map.put(((TypeOR) object).getId(), BigInteger.valueOf(ids));
			((TypeOR) object).setId(BigInteger.valueOf(ids++));
			or_list.add((TypeOR) object);
		} else if (object instanceof TypeXOR) {
			id_map.put(((TypeXOR) object).getId(), BigInteger.valueOf(ids));
			((TypeXOR) object).setId(BigInteger.valueOf(ids++));
			xor_list.add((TypeXOR) object);
		}
	}
	
	private void translateObjects() {
		for(TypeObject obj: object_list)
		{
			ObjectType object = new ObjectType();
			id_map.put(obj.getId(), BigInteger.valueOf(ids));
			object.setId(BigInteger.valueOf(ids++));
			object.setName(obj.getName());
			object.setConfigurable(obj.isFinal());
			addNodeAnnotations(obj);
			cproc.getObject().add(object);
		}
	}

	private void translateRoles() {
		for(TypeRole role: role_list)
		{
			ResourceTypeType obj = new ResourceTypeType();
			id_map.put(role.getId(), BigInteger.valueOf(ids));
			obj.setId(BigInteger.valueOf(ids++));
			obj.setName(role.getName());
			addNodeAnnotations(role);
			cproc.getResourceType().add(obj);
		}
	}
	
}

