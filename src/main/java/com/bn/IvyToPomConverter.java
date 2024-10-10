package com.bn;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;
import org.apache.ivy.util.MessageLogger;

public class IvyToPomConverter {

    public static void main(String[] args) throws Exception {
        // Path to ivy.xml and version.properties
        String ivyXmlPath = "path/to/ivy.xml";
        String versionPropertiesPath = "path/to/version.properties";

        // Load Ivy settings
        IvySettings ivySettings = new IvySettings();

        // Resolve Ivy dependencies
        Ivy ivy = Ivy.newInstance(ivySettings);
        ResolveOptions resolveOptions = new ResolveOptions().setConfs(new String[] { "default" });
        ResolveReport resolveReport = ivy.resolve(new File(ivyXmlPath).toURI().toURL(), resolveOptions);

        // Initialize properties from version.properties
        Properties versionProps = new Properties();
        try (FileInputStream versionPropsStream = new FileInputStream(versionPropertiesPath)) {
            versionProps.load(versionPropsStream);
        }

        // Process Ivy module descriptor
        ModuleDescriptor md = resolveReport.getModuleDescriptor();
        ModuleRevisionId moduleRevisionId = md.getModuleRevisionId();
        DependencyDescriptor[] dependencies = md.getDependencies();

        // Generate POM XML
        StringBuilder pomXmlBuilder = new StringBuilder();
        pomXmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        pomXmlBuilder.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n");
        pomXmlBuilder.append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        pomXmlBuilder.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
        pomXmlBuilder.append("    <modelVersion>4.0.0</modelVersion>\n");
        pomXmlBuilder.append("    <groupId>").append(moduleRevisionId.getOrganisation()).append("</groupId>\n");
        pomXmlBuilder.append("    <artifactId>").append(moduleRevisionId.getName()).append("</artifactId>\n");
        pomXmlBuilder.append("    <version>").append(moduleRevisionId.getRevision()).append("</version>\n");

        // Add dependencies section
        pomXmlBuilder.append("    <dependencies>\n");
        for (DependencyDescriptor dependency : dependencies) {
            ModuleRevisionId depRevisionId = dependency.getDependencyRevisionId();
            pomXmlBuilder.append("        <dependency>\n");
            pomXmlBuilder.append("            <groupId>").append(depRevisionId.getOrganisation()).append("</groupId>\n");
            pomXmlBuilder.append("            <artifactId>").append(depRevisionId.getName()).append("</artifactId>\n");
            pomXmlBuilder.append("            <version>").append(versionProps.getProperty(depRevisionId.getName())).append("</version>\n");
            // Add other details like scope, exclusions etc. if needed
            pomXmlBuilder.append("        </dependency>\n");
        }
        pomXmlBuilder.append("    </dependencies>\n");

        // Close project tag
        pomXmlBuilder.append("</project>");

        // Output generated POM XML
        System.out.println(pomXmlBuilder.toString());
    }
}
