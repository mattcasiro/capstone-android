package com.telmediq.docstorage.helper;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sean on 2017-05-03.
 */

public class SecurityUtils {
	public static class HmacSha1Signature {
		private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

		public static String calculateRFC2104HMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			return encodeToBase64(mac.doFinal(data.getBytes()));
		}

		public static String encodeToBase64(byte[] data) {
			return Base64.encodeToString(data, Base64.NO_WRAP);
		}
	}
}
