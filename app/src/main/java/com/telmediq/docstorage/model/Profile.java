package com.telmediq.docstorage.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jared on 22/05/17.
 */

public class Profile extends RealmObject {
	@PrimaryKey
	private Integer id;
	private String first_name;
	private String last_name;
	private String email;
	private Date date_joined;

	public static Profile getProfile(Realm realm) {
		return realm.where(Profile.class).findFirst();
	}

	//<editor-fold desc="Getter and Setters">
	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public Date getDateJoined() {
		return date_joined;
	}
	//</editor-fold>

}