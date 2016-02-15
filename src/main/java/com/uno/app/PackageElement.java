package com.uno.app;

import java.util.ArrayList;

public class PackageElement {
	
	private PackageElement parent = new PackageElement();
	
	private ArrayList<PackageElement> dependencies = new ArrayList<PackageElement>();
	
	private ArrayList<Licence> licences = new ArrayList<Licence>();
}
