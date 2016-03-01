package com.uno.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class Main{
	public static void main(String [] args) {
		
		if(args.length != 1){
			throw new RuntimeException("Needs a pom file to read");
		}
		MavenAccessObject mao = new MavenAccessObject();
		
		PackageElement parent = new PackageElement();
		File file = new File("output");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		ArrayList<PackageElement> list = mao.getDependencies(args[0], parent);
		
		try {
			FileUtils.deleteDirectory(new File("pomdump"));
			FileUtils.deleteDirectory(new File("jardump"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
