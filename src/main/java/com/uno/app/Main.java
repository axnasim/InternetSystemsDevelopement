package com.uno.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class Main{
	public static void main(String [] args) {
		
		if(args.length < 1){
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
		
		if (args.length > 1 && args[1].equals("-p")) {
			System.out.println("\n--DEPENDENCY TREE--\n");
			printTree(list, 0);
			System.out.println();
		}
		
		try {
			FileUtils.deleteDirectory(new File("pomdump"));
			FileUtils.deleteDirectory(new File("jardump"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void printTree(ArrayList<PackageElement> list, int depth) {
		//Print each item, then recursively print their children
		for (PackageElement pkg : list) {
			for (int i = 0; i < depth; i++) {
				System.out.print("...");
			}
			System.out.println(pkg.getArtifactID() + "-" + pkg.getVersion());
			printTree(pkg.getDependencies(), depth + 1);
		}
	}
}
