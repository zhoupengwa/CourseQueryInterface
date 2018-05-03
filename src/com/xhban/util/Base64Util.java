package com.xhban.util;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

	private static Base64 base64=new Base64();

	public static String encodeBufferBase64(byte[] buffer) {
		return buffer == null ? null : base64.encodeToString(buffer);
	}

	public static String decodeBufferBase64(byte[] buffer) {
		return buffer == null ? null : new String(base64.decode(buffer));
	}
}
