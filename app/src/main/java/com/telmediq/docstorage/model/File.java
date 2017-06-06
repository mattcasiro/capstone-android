package com.telmediq.docstorage.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Andrea on 2017-05-06.
 */

public class File extends RealmObject {
	@PrimaryKey
	private Integer id;
	private String name;
	private String original_name;
	private Long size;
	private String mime_type;
	private Date created;
	private Date modified;
	private Integer folder;
	private String file;
	private Integer owner;

	public static RealmResults<File> getFiles(Realm realm) {
		return realm.where(File.class).findAllSorted("name");
	}

	public static RealmResults<File> getFilesByFolder(Realm realm, String folderId) {
		// TODO: convert ID to UUID and remove casting (stretch goal)
		return realm.where(File.class).equalTo("folder", new Integer(folderId)).findAllSorted("name");
	}

	public static File getFile(Realm realm, String fileId) {
		return realm.where(File.class).equalTo("id", new Integer(fileId)).findFirst();
	}

	public void delete(Realm realm){
		this.deleteFromRealm();
	}

	//<editor-fold desc="Getter and Setters">
	public Integer getId() {
		if (id == null) {
			return -1;
		}
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginal_name() {
		return original_name;
	}

	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}

	public Long getSize() {
		if (size == null) {
			return -1L;
		}
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Integer getFolder() {
		if (folder == null) {
			return -1;
		}
		return folder;
	}

	public void setFolder(Integer folder) {
		this.folder = folder;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Integer getOwner() {
		if (owner == null) {
			return -1;
		}
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public String getUrl() {
		return String.format("/api/folders/%d/files/%d/file/stream/", folder, id);
	}

	//</editor-fold>
}
