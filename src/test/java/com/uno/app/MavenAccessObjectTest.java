package com.uno.app;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author mkyong
 *
 */
public class MavenAccessObjectTest {

    private static MavenAccessObject mao;

    @BeforeClass
    public  static void oneTimeSetUp() {
        mao = new MavenAccessObject();
    }
    @Before
    public void beforeEach(){
    	File file = new File("output");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
    }
    @After
    public  void afterEveryTearDown(){
    	try {
			FileUtils.deleteDirectory(new File("pomdump"));
			FileUtils.deleteDirectory(new File("output"));
			FileUtils.deleteDirectory(new File("jardump"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    @Test
    public void testregularPom() {
    	PackageElement pe = new PackageElement();
    	String s = "";
    	try {
			s= this.getClass().getResource("/testpom.xml").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	mao.getDependencies(s, pe, 0);
    	File poms = new File("pomdump");
    	File[] files = poms.listFiles();
    	System.out.println(files.length);
    	assertEquals(files[0].getPath(), "pomdump/junit-4.11.pom");
    	assertEquals(files[1].getPath(), "pomdump/commons-io-2.4.pom");
    	assertEquals(files[2].getPath(), "pomdump/hamcrest-core-1.3.pom");
    	assertEquals(files[3].getPath(), "pomdump/junit-4.10.pom");
    	assertEquals(files[4].getPath(), "pomdump/hamcrest-core-1.1.pom");
    	assertEquals(files[5].getPath(), "pomdump/logging-0.0.3.pom");
        
        
    }
    
    @Test
    public void testPomWithResourceAsVersion() {
    	PackageElement pe = new PackageElement();
    	String s = "";
    	try {
			s= this.getClass().getResource("/testpom2.xml").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	mao.getDependencies(s, pe, 0);
    	File poms = new File("pomdump");
    	File[] files = poms.listFiles();
    	System.out.println(files.length);
    	assertEquals(files[0].getPath(), "pomdump/junit-4.11.pom");
    	assertEquals(files[1].getPath(), "pomdump/commons-io-2.4.pom");
    	assertEquals(files[2].getPath(), "pomdump/hamcrest-core-1.3.pom");
    	assertEquals(files[3].getPath(), "pomdump/junit-4.10.pom");
    	assertEquals(files[4].getPath(), "pomdump/hamcrest-core-1.1.pom");
    	assertEquals(files[5].getPath(), "pomdump/logging-0.0.3.pom");
        
        
    }
    
    @Test
    public void testPomWithProjectVersion() {
    	PackageElement pe = new PackageElement();
    	String s = "";
    	try {
			s= this.getClass().getResource("/testpom3.xml").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	mao.getDependencies(s, pe, 0);
    	File poms = new File("pomdump");
    	File[] files = poms.listFiles();
    	System.out.println(files.length);
    	assertEquals(files[0].getPath(), "pomdump/junit-4.11.pom");
    	assertEquals(files[1].getPath(), "pomdump/commons-io-2.4.pom");
    	assertEquals(files[2].getPath(), "pomdump/hamcrest-core-1.3.pom");
    	assertEquals(files[3].getPath(), "pomdump/junit-4.10.pom");
    	assertEquals(files[4].getPath(), "pomdump/hamcrest-core-1.1.pom");
    	assertEquals(files[5].getPath(), "pomdump/logging-0.0.3.pom");
        
        
    }
    
    @Test
    public void testOutputDir() {
    	PackageElement pe = new PackageElement();
    	String s = "";
    	try {
			s= this.getClass().getResource("/testpom3.xml").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	mao.getDependencies(s, pe, 0);
    	File poms = new File("output");
    	File[] files = poms.listFiles();
    	System.out.println(files.length);
    	for(int i = 0; i < files.length; i++){
    		System.out.println(files[i].getPath());
    	}
    	assertEquals(files[0].getPath(), "output/hamcrest-core-1.1.spdx");
    	assertEquals(files[1].getPath(), "output/logging-0.0.3.spdx");
    	assertEquals(files[2].getPath(), "output/hamcrest-core-1.3.spdx");
        
        
    }
}
