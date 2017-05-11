package com.telmediq.docstorage.model;

import java.util.Date;

/**
 * Created by Andrea on 2017-05-06.
 */

public class File {
	private String id;
	private String name;
	private Date created;
	private Date modified;
	private long size;

	public File(String id, String name, Date created, Date modified, long size){
		this.id = id;
		this.name = name;
		this.created = created;
		this.modified = modified;
		this.size = size;
	}

	//<editor-fold desc="Getter and Setters">
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public Date getModified() {
		return modified;
	}

	public long getSize() {
		return size;
	}
	//</editor-fold>
}
