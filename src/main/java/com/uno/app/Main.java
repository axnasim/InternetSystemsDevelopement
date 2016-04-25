package com.uno.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;

import org.apache.commons.io.FileUtils;

public class Main {
	public static void main(String[] args) {

		if (args.length < 2) {
			if (args.length == 1) {
				checkDatabase(args[0]);
				System.exit(0);
			}
			// throw new RuntimeException("Needs a pom file to read");
			System.out.println("\tUsage:\n\tCommand: java -jar /location/of/pom.xml [jarname].jar [options]");
			System.out.println("\tCommand: java -jar [jarname].jar");
			System.out.println("\tOptions:\n\t\t-p - print dependency tree");
			System.exit(0);
		}

		// check if dosocs2 is installed
		Runtime rt = Runtime.getRuntime();
		String[] commands = { "dosocs2" };
		Process proc = null;
		try {
			proc = rt.exec(commands);
		} catch (IOException e1) {
			System.out.println("Dosocs is not installed");
			System.exit(0);
		}
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String s = null;
		boolean found = false;
		try {
			while((s = stdError.readLine()) != null){
				if(s.contains("Usage:")){
					found = true;
				}
			}
			
		} catch (IOException e2) {
			System.out.println("Dosocs is not installed");
			System.exit(0);
		}
		if (!found) {
			System.out.println("Dosocs is not installed");
			System.exit(0);
		}

		MavenAccessObject mao = new MavenAccessObject();

		PackageElement parent = new PackageElement();
		parent.setJarLocation(args[1]);
		mao.analyzeRootJar(parent);
		System.out.println(parent.getChecksum());

		File file = new File("output");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		ArrayList<PackageElement> list = mao.getDependencies(args[0], parent, 0);

		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			String userHome = System.getProperty("user.home");
			conn = DriverManager.getConnection("jdbc:sqlite:" + userHome + "/.config/dosocs2/dosocs2.sqlite3");
		} catch (Exception e) {
			System.out.println(e);
		}

		saveDependencies(list, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		if (args.length > 2 && args[2].equals("-p")) {
			System.out.println("\n--DEPENDENCY TREE--\n");
			printTree(parent);
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

	private static void printTree(PackageElement parent) {
		System.out.println("Project");
		ArrayList<PackageElement> list = parent.getDependencies();
		printTree(list, 1);
	}

	private static void printTree(ArrayList<PackageElement> list, int depth) {
		// Print each item, then recursively print their children
		for (PackageElement pkg : list) {
			for (int i = 0; i < depth; i++) {
				System.out.print("...");
			}
			System.out.println(pkg.getArtifactID() + "-" + pkg.getVersion());
			printTree(pkg.getDependencies(), depth + 1);
		}
	}

	private static void saveDependencies(ArrayList<PackageElement> list, Connection conn) {
		for (PackageElement pkg : list) {
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
			} catch (SQLException e) {
				System.out.println(e);
			}
			String sql = "INSERT INTO RELATIONSHIPS VALUES ("
					+ "((SELECT MAX(RELATIONSHIP_ID) FROM RELATIONSHIPS) + 1),"
					+ "(SELECT IDENTIFIER_ID FROM IDENTIFIERS WHERE PACKAGE_ID = (SELECT PACKAGE_ID FROM PACKAGES WHERE SHA1='"
					+ pkg.getParent().getChecksum() + "')),"
					+ "(SELECT IDENTIFIER_ID FROM IDENTIFIERS WHERE PACKAGE_ID = (SELECT PACKAGE_ID FROM PACKAGES WHERE SHA1='"
					+ pkg.getChecksum() + "') )," + "29,'')";
			try {
				stmt.executeUpdate(sql);
				stmt.close();
			} catch (SQLException e) {
				// System.out.println(e);
			}
			saveDependencies(pkg.getDependencies(), conn);
		}
	}

	private static void checkDatabase(String jar) {
		System.out.println("Searching database for dependency relationships...");

		File jarFile = new File(jar);
		String jarSha = sha1(jarFile);

		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			String userHome = System.getProperty("user.home");
			conn = DriverManager.getConnection("jdbc:sqlite:" + userHome + "/.config/dosocs2/dosocs2.sqlite3");
		} catch (Exception e) {
			System.out.println(e);
		}

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			System.out.println(e);
		}
		String sql = "SELECT IDENTIFIER_ID FROM IDENTIFIERS WHERE PACKAGE_ID = (SELECT PACKAGE_ID FROM PACKAGES WHERE SHA1 = '"
				+ jarSha + "')";
		String sql2 = "SELECT NAME FROM PACKAGES WHERE PACKAGE_ID IN (SELECT PACKAGE_ID FROM IDENTIFIERS WHERE IDENTIFIER_ID IN (SELECT RIGHT_IDENTIFIER_ID FROM RELATIONSHIPS WHERE RELATIONSHIP_TYPE_ID = 29 AND LEFT_IDENTIFIER_ID = ("
				+ sql + ")))";
		try {
			ResultSet rs = stmt.executeQuery(sql2);
			while (rs.next()) {
				System.out.println(rs.getString("name"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	private static String sha1(File file) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}

		String result = "";

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			final byte[] buffer = new byte[1024];
			for (int read = 0; (read = is.read(buffer)) != -1;) {
				messageDigest.update(buffer, 0, read);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		// Convert the byte to hex format
		try {
			Formatter formatter = new Formatter();
			for (final byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			result = formatter.toString();
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}
}
