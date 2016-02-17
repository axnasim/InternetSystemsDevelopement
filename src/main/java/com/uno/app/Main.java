package com.uno.app;

import java.util.ArrayList;

public class Main{
	public static void main(String [] args){
		
		if(args.length != 1){
			throw new RuntimeException("Needs a pom file to read");
		}
		MavenAccessObject mao = new MavenAccessObject();
		
		PackageElement parent = new PackageElement();
		
		ArrayList<PackageElement> list = mao.getDependencies(args[0], parent);
	}
	
	
}
