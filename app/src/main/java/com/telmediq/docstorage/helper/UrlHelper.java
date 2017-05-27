package com.telmediq.docstorage.helper;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.telmediq.docstorage.model.File;

import timber.log.Timber;

/**
 * Created by Andrea on 2017-05-13.
 */

public class UrlHelper {

	public static GlideUrl getAuthenticatedUrl(String url) {
		Timber.d("Access token: " + AppValues.getAccessToken());
		return new GlideUrl(Constants.SERVER_URL + url, new LazyHeaders.Builder()
				.addHeader("Authorization", String.format("token %s", AppValues.getAccessToken()))
				.build());
	}
}
