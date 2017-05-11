package com.telmediq.docstorage.model;

import java.util.Date;

/**
 * Created by Andrea on 2017-05-06.
 */

public class File {
	private String name;
	private Date created;
	private Date modified;
	private long size;

	public File(String name, Date created, Date modified, long size){
		this.name = name;
		this.created = created;
		this.modified = modified;
		this.size = size;
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
}
