package org.apromore.toolbox.similaritySearch.graph;

import java.math.BigInteger;
import java.util.HashSet;

public class VertexResourceRef {

	private boolean optional;
	private BigInteger resourceID;
	private String qualifier;
	private HashSet<String> models = new HashSet<String>();
	
	public VertexResourceRef(boolean optional, BigInteger resourceID,
			String qualifier, HashSet<String> models) {
		super();
		this.optional = optional;
		this.resourceID = resourceID;
		this.qualifier = qualifier;
		this.models = models;
	}
	
	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public BigInteger getresourceID() {
		return resourceID;
	}

	public void setresourceID(BigInteger resourceID) {
		this.resourceID = resourceID;
	}
	
	public HashSet<String> getModels() {
		return models;
	}

	public void addModels(HashSet<String> models) {
		this.models.addAll(models);
	}

	public void addModel(String model) {
		models.add(model);
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getQualifier() {
		return qualifier;
	}
}
