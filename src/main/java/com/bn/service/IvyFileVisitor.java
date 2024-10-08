package com.bn.service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

// Custom FileVisitor class
public class IvyFileVisitor implements FileVisitor<Path> {

	private List<Path> ivyFiles;

	public IvyFileVisitor(List<Path> ivyFiles) {
		// TODO Auto-generated constructor stub
		this.ivyFiles = ivyFiles;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		// We don't need to do anything before visiting a directory
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		// Check if the file name is ivy.xml and if it is inside a folder named "ivy"
		if (file.getFileName().toString().equals("ivy.xml") && file.getParent().getFileName().toString().equals("ivy")) {
			// Trigger an action (you can replace this with your desired action)
			System.out.println("Found ivy.xml in an ivy folder: " + file.toAbsolutePath());

			// You can trigger additional action here, like processing the ivy.xml file
			processIvyFile(file);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		// Handle error (optional)
		System.err.println("Failed to visit file: " + file + ", " + exc.getMessage());
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		// We don't need to do anything after visiting a directory
		return FileVisitResult.CONTINUE;
	}

	// Custom action to process ivy.xml file
	private void processIvyFile(Path ivyFilePath) {
		// Placeholder for processing the ivy.xml file
		System.out.println("Processing ivy.xml file: " + ivyFilePath);
		// You can call a service to transform the Ivy file into POM or any other action
		// here
		ivyFiles.add(ivyFilePath);
	}
}
