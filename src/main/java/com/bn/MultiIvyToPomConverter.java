package com.bn;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;

import com.bn.service.IvyFileWalker;

public class MultiIvyToPomConverter {

    private static Properties appConfig;

	public static void main(String[] args) throws Exception {
    	
    	// load config
    	if(args!=null && args[0]!=null) {
    		String[] keyValue = args[0].split("=");
    		if(keyValue[0]=="config") {
    			
    		}
    	}
    	MultiIvyToPomConverter ivyToMvnConvert = new MultiIvyToPomConverter();
    	ivyToMvnConvert.loadConfig();
    	
        // Path to ivysettings.xml and version.properties
        String ivySettingsPath = appConfig.getProperty("ivy.settings");
        String versionPropertiesPath = appConfig.getProperty("ivy.properties");
        
        IvyFileWalker walker = new IvyFileWalker(Paths.get(appConfig.getProperty("ivy.project.root")));
        List<Path> ivyFilePaths = walker.getIvyFiles();

        // Load Ivy settings from ivysettings.xml
        IvySettings ivySettings = new IvySettings();
        ivySettings.load(new File(ivySettingsPath));
        Ivy ivy = Ivy.newInstance(ivySettings);

        // Load version properties
        Properties versionProps = new Properties();
        try (FileInputStream versionPropsStream = new FileInputStream(versionPropertiesPath)) {
            versionProps.load(versionPropsStream);
        }

        // Set to hold unique dependencies across subprojects
        Set<DependencyDescriptor> allDependencies = new HashSet<>();

        // Resolve each subproject's Ivy file and collect dependencies
        for (Path ivyFilePath : ivyFilePaths) {
            System.out.println("Processing: " + ivyFilePath);
            ResolveOptions resolveOptions = new ResolveOptions().setConfs(new String[]{"default"});
            ResolveReport resolveReport = ivy.resolve(ivyFilePath.toFile().toURI().toURL(), resolveOptions);
            ModuleDescriptor md = resolveReport.getModuleDescriptor();
            for (DependencyDescriptor dependency : md.getDependencies()) {
                allDependencies.add(dependency);
            }
        }

        // Generate POM XML
        StringBuilder pomXmlBuilder = new StringBuilder();
        pomXmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        pomXmlBuilder.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n");
        pomXmlBuilder.append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        pomXmlBuilder.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
        pomXmlBuilder.append("    <modelVersion>4.0.0</modelVersion>\n");
        pomXmlBuilder.append("    <groupId>your.group.id</groupId>\n"); // Update this as needed
        pomXmlBuilder.append("    <artifactId>your-artifact-id</artifactId>\n"); // Update this as needed
        pomXmlBuilder.append("    <version>1.0-SNAPSHOT</version>\n");

        // Add dependencies section
        pomXmlBuilder.append("    <dependencies>\n");
        for (DependencyDescriptor dependency : allDependencies) {
            ModuleRevisionId depRevisionId = dependency.getDependencyRevisionId();
            pomXmlBuilder.append("        <dependency>\n");
            pomXmlBuilder.append("            <groupId>").append(depRevisionId.getOrganisation()).append("</groupId>\n");
            pomXmlBuilder.append("            <artifactId>").append(depRevisionId.getName()).append("</artifactId>\n");
            pomXmlBuilder.append("            <version>").append(versionProps.getProperty(depRevisionId.getName(), depRevisionId.getRevision())).append("</version>\n");
            pomXmlBuilder.append("        </dependency>\n");
        }
        pomXmlBuilder.append("    </dependencies>\n");

        // Close project tag
        pomXmlBuilder.append("</project>");

        // Output generated POM XML
        System.out.println(pomXmlBuilder.toString());
    }

	private void loadConfig() {
		appConfig = new Properties();
		// Use the class loader to get the resource from the classpath
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				System.out.println("Sorry, unable to find config.properties");
				return;
			}
			// Load the properties from the input stream
			appConfig.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
