package com.uno.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

	private static final String POM_DIRECTORY = "pomdump";

	public ArrayList<PackageElement> getDependencies(String pomFile, PackageElement parent, int level) {
		if (level == 3) {
			return new ArrayList<PackageElement>();
		}
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
					String ver = "";
					if (eElement.getElementsByTagName("version") != null
							&& eElement.getElementsByTagName("version").item(0) != null) {
						ver = eElement.getElementsByTagName("version").item(0).getTextContent();
						if (!ver.contains("{")) {
							element.setVersion(eElement.getElementsByTagName("version").item(0).getTextContent());
							element.setParent(parent);
							list.add(element);
						} else {
							String prop = ver.replaceFirst("\\$\\{", "");
							prop = prop.replaceFirst("\\}", "");
							NodeList property = doc.getElementsByTagName(prop);
							String value = null;
							if (property != null && property.item(0) != null) {
								value = property.item(0).getTextContent();
								ver = ver.replaceFirst("\\$\\{" + prop + "\\}", value);
								element.setVersion(ver);
								element.setParent(parent);
								list.add(element);
							} else {
								System.out.println(ver);
								if (ver.equals("${project.version}")) {
									NodeList mlist = doc.getElementsByTagName("project");
									Node mnode = mlist.item(0);
									if (mnode.getNodeType() == Node.ELEMENT_NODE) {
										Element nEl = (Element) mnode;
										if (nEl.getElementsByTagName("version") != null
												&& nEl.getElementsByTagName("version").item(0) != null) {
											element.setVersion(
													nEl.getElementsByTagName("version").item(0).getTextContent());
											element.setParent(parent);
											list.add(element);
										} else {
											mlist = doc.getElementsByTagName("parent");
											mnode = mlist.item(0);
											System.out.println("got parent");
											if (mnode.getNodeType() == Node.ELEMENT_NODE) {
												nEl = (Element) mnode;
												System.out.println(nEl.getElementsByTagName("version").item(0));
												if (nEl.getElementsByTagName("version") != null
														&& nEl.getElementsByTagName("version").item(0) != null) {
													element.setVersion(nEl.getElementsByTagName("version").item(0)
															.getTextContent());
													element.setParent(parent);
													list.add(element);
												}
											}
										}
									}

								}

							}

						}

					}

				}

			}
		}  catch (

		Exception e)

		{
			System.out.println("Cant find pom");
		}
		parent.setDependencies(list);

		downloadJars(list);

		analyzeJars(list);

		recurse(list, level);

		return list;
	}

	private void analyzeJars(ArrayList<PackageElement> list) {
		for (int i = 0; i < list.size(); i++) {
			PackageElement element = list.get(i);

			try {
				Runtime rt = Runtime.getRuntime();
				// Process pr = rt.exec("cmd /c dir");
				Process pr = rt.exec("dosocs2 oneshot " + element.getJarLocation());

				BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

				String line = null;
				String total = "";
				while ((line = input.readLine()) != null) {
					total += line + "\n";
				}
				element.setDosocsOutput(total);
				int exitVal = pr.waitFor();

				PrintWriter out = new PrintWriter(
						"output/" + element.getArtifactID() + "-" + element.getVersion() + ".spdx");
				FileOutputStream fop = null;
				File file;
				// String content = "This is the text content";

				try {

					file = new File("output/" + element.getArtifactID() + "-" + element.getVersion() + ".spdx");
					fop = new FileOutputStream(file);

					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}

					// get the content in bytes
					byte[] contentInBytes = total.getBytes();

					fop.write(contentInBytes);
					fop.flush();
					fop.close();

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fop != null) {
							fop.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}

	}

	private void recurse(ArrayList<PackageElement> list, int level) {
		for (int i = 0; i < list.size(); i++) {
			getDependencies(list.get(i).getPomLocation(), list.get(i), level + 1);
		}

	}

	private void downloadJars(ArrayList<PackageElement> list) {
		for (int i = 0; i < list.size(); i++) {

			PackageElement element = list.get(i);

			String artifactId = element.getArtifactID();
			String groupId = element.getGroupID().replace(".", "/");
			String version = element.getVersion();

			String filename = artifactId + "-" + version + ".jar";
			String pomname = artifactId + "-" + version + ".pom";
			element.setJarLocation(TEMP_DIRECTORY + "/" + filename);
			element.setPomLocation(POM_DIRECTORY + "/" + pomname);
			boolean skip = false;
			URL url;
			try {
				FileUtils fu = new FileUtils();
				url = new URL("https://repo1.maven.org/maven2/" + groupId + "/" + artifactId + "/" + version + "/"
						+ filename);
				System.out.println("Downloading " + filename);
				fu.copyURLToFile(url, new File(TEMP_DIRECTORY + "/" + filename), 4000, 4000);
			} catch (FileNotFoundException error) {
				System.out.println("File not found in Maven Central");
			} catch (IOException e) {
				try {
					FileUtils fu = new FileUtils();
					url = new URL("https://repo1.maven.org/maven2/" + groupId + "/" + artifactId + "/" + version + "/"
							+ filename);
					System.out.println("Downloading " + filename);
					fu.copyURLToFile(url, new File(TEMP_DIRECTORY + "/" + filename), 4000, 4000);
				} catch (FileNotFoundException error) {
					System.out.println("File not found in Maven Central");
					list.remove(i);
					skip = true;
				} catch (IOException error) {
					System.out.println("Timed out");
					list.remove(i);
					skip = true;
				}
			}
			if (!skip) {
				try {
					FileUtils fu = new FileUtils();
					url = new URL("https://repo1.maven.org/maven2/" + groupId + "/" + artifactId + "/" + version + "/"
							+ pomname);
					System.out.println("Downloading " + pomname);
					fu.copyURLToFile(url, new File(POM_DIRECTORY + "/" + pomname), 2000, 2000);
				} catch (FileNotFoundException error) {
					System.out.println("File not found in Maven Central");
				} catch (IOException e) {
					try {
						FileUtils fu = new FileUtils();
						url = new URL("https://repo1.maven.org/maven2/" + groupId + "/" + artifactId + "/" + version
								+ "/" + pomname);
						System.out.println("Downloading " + pomname);
						fu.copyURLToFile(url, new File(POM_DIRECTORY + "/" + pomname), 2000, 2000);
					} catch (FileNotFoundException error) {
						System.out.println("File not found in Maven Central");
						list.remove(i);
					} catch (IOException error) {
						System.out.println("Timed out");
						list.remove(i);
					}
				}

			}
		}

	}
}
