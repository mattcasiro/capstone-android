package com.telmediq.docstorage.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;


public class Folder extends RealmObject {
	@PrimaryKey
	private Integer id;
	private String name;
	private Date created;
	private Date modified;
	private Integer owner;
	private Integer parent;

	public static RealmResults<Folder> getAllFolders(Realm realm) {
		return realm.where(Folder.class).findAllSorted("name");
	}

	public static Folder getRootFolder(Realm realm) {
		return realm.where(Folder.class).isNull("parent").findFirst();
	}

	// TODO: fix this to be better. How to get empty list?
	public static RealmResults<Folder> getEmptyFolderList(Realm realm) {
		return realm.where(Folder.class).equalTo("id", new Integer(-1)).findAll();
	}

	public static RealmResults<Folder> getFoldersByParent(Realm realm, Integer parent) {
		return realm.where(Folder.class).equalTo("parent", parent).findAllSorted("name");
	}

	public static Folder getFolder(Realm realm, Integer folderId) {
		return realm.where(Folder.class).equalTo("id", folderId).findFirst();
	}


	public Integer getId() {
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

	public Integer getOwner() {
		if (owner == null) {
			return -1;
		}
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

}
