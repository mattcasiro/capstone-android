package com.telmediq.docstorage.model;

/**
 * Created by sean on 2017-05-03.
 */

public class AuthorizationResponse {
	private String status;
	private String token;
	private Integer root_id;

	public AuthorizationResponse() {

	}

	public AuthorizationResponse(String status, String token) {
		this.status = status;
		this.token = token;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getRootId() {
		return root_id;
	}
}
