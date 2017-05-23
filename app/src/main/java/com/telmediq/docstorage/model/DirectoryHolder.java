package com.telmediq.docstorage.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea on 2017-05-18.
 */

public class DirectoryHolder {
	public static final int HEADER = 0;
	public static final int FOLDER = 1;
	public static final int FILE = 2;

	private String header;
	private Folder folder;
	private File file;

	//Constructors
	public DirectoryHolder(String header){
		this.header = header;
	}
	public DirectoryHolder(Folder folder){
		this.folder = folder;
	}
	public DirectoryHolder(File file){
		this.file = file;
	}

	public static List<DirectoryHolder> generateDirectoryHolder(List<Folder> folders, List<File> files){
		List<DirectoryHolder> holders = new ArrayList<>();

		if(folders.size() > 0) {
			holders.add(new DirectoryHolder("Folders"));
		}
		for(Folder folder : folders){
			holders.add(new DirectoryHolder(folder));
		}

		if (files.size() > 0) {
			holders.add(new DirectoryHolder("Files"));
		}
		for(File file : files){
			holders.add(new DirectoryHolder(file));
		}
		return holders;
	}

	public int getType(){
		if(folder != null){
			return FOLDER;
		} else if (file != null){
			return FILE;
		} else {
			return HEADER;
		}
	}


	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}


}
