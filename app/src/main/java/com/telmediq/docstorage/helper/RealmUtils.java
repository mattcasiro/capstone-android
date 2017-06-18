package com.telmediq.docstorage.helper;

import io.realm.RealmObject;

/**
 * Created by sean on 2017-06-12.
 */

public class RealmUtils {
	public static boolean isManaged(RealmObject object) {
		if (object == null) {
			return false;
		}

		return object.isValid() && object.isManaged();
	}
}
