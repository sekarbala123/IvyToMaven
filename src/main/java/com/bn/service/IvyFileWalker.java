package com.bn.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class IvyFileWalker {

	private Path rootPath;
	private List<Path> ivyFiles = new ArrayList<>();
	public IvyFileWalker(Path rootPath) {
		this.rootPath = rootPath;
		this.walk();
	}
	private void walk() {
		try {
			Files.walkFileTree(rootPath, new IvyFileVisitor(ivyFiles));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List<Path> getIvyFiles() {
		return ivyFiles;
	}
	
	
}
