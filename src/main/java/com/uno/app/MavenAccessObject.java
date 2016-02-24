package com.uno.app;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MavenAccessObject {
	
	private static final String TEMP_DIRECTORY = "jardump";
	
	public ArrayList<PackageElement> getDependencies(String pomFile, PackageElement parent) {
		System.out.println(pomFile);
		ArrayList<PackageElement> list = new ArrayList<PackageElement>();
		try {
			File fXmlFile = new File(pomFile);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("dependency");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				PackageElement element = new PackageElement();

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					element.setArtifactID(eElement.getElementsByTagName("artifactId").item(0).getTextContent());
					element.setGroupID(eElement.getElementsByTagName("groupId").item(0).getTextContent());
					element.setVersion(eElement.getElementsByTagName("version").item(0).getTextContent());

				}
				element.setParent(parent);
				list.add(element);
				System.out.println(element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		parent.setDependencies(list);
		
		downloadJars(list);
		
		return list;
	}

	private void downloadJars(ArrayList<PackageElement> list) {
		for(int i = 0; i < list.size(); i++){
			
			PackageElement element = list.get(i);
			
			String artifactId = element.getArtifactID();
			String groupId = element.getGroupID().replace(".", "/");
			String version = element.getVersion();
			
			String filename = artifactId + "-" + version + ".jar";
						
			URL url;
			try {
				FileUtils fu = new FileUtils();
				url = new URL("https://repo1.maven.org/maven2/" + groupId + "/" + artifactId + "/" + version + "/" + filename);
				System.out.println("Downloading " + filename);
				fu.copyURLToFile(url, new File(TEMP_DIRECTORY + "/" + filename));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
