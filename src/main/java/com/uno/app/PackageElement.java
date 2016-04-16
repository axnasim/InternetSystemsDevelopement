package com.uno.app;

import java.util.ArrayList;

public class PackageElement {
	
	private String groupID = "";
	
	private String artifactID = "";
	
	private String version = "";
	
	private String jarLocation = "";
	
	private String pomLocation = "";
	
	private String dosocsOutput = "";
	
	private String checksum = "";
	
	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public String getDosocsOutput() {
		return dosocsOutput;
	}

	public void setDosocsOutput(String dosocsOutput) {
		this.dosocsOutput = dosocsOutput;
	}

	public String getJarLocation() {
		return jarLocation;
	}

	public void setJarLocation(String jarLocation) {
		this.jarLocation = jarLocation;
	}

	public String getPomLocation() {
		return pomLocation;
	}

	public void setPomLocation(String pomLocation) {
		this.pomLocation = pomLocation;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getArtifactID() {
		return artifactID;
	}

	public void setArtifactID(String artifactID) {
		this.artifactID = artifactID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public PackageElement getParent() {
		return parent;
	}

	public void setParent(PackageElement parent) {
		this.parent = parent;
	}

	public ArrayList<PackageElement> getDependencies() {
		return dependencies;
	}

	public void setDependencies(ArrayList<PackageElement> dependencies) {
		this.dependencies = dependencies;
	}



	private PackageElement parent = null;
	
	private ArrayList<PackageElement> dependencies = new ArrayList<PackageElement>();
	
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("groupId: ").append(this.groupID).append(" artifactId: ").append(this.artifactID)
		.append(" version: ").append(this.version).append("\n");
		return s.toString();
	}
}
