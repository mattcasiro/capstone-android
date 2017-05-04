package com.telmediq.docstorage.model;

/**
 * Created by sean on 2017-05-03.
 */

public class AuthorizationResponse {
	private String access_key_id;
	private String secret_access_key;

	public AuthorizationResponse() {
	}

	public AuthorizationResponse(String access_key_id, String secret_access_key) {
		this.access_key_id = access_key_id;
		this.secret_access_key = secret_access_key;
	}

	//<editor-fold desc="Getter and Setters">
	public String getAccess_key_id() {
		return access_key_id;
	}

	public void setAccess_key_id(String access_key_id) {
		this.access_key_id = access_key_id;
	}

	public String getSecret_access_key() {
		return secret_access_key;
	}

	public void setSecret_access_key(String secret_access_key) {
		this.secret_access_key = secret_access_key;
	}
	//</editor-fold>
}
