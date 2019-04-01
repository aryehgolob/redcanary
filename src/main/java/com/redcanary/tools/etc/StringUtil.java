package com.redcanary.tools.etc;

import java.nio.charset.Charset;

    /*
     * Generic String methods            				
     */

import java.util.Random;

public class StringUtil {
	public static String generateRandomString(String size) {
		int sizeInt = Integer.parseInt(size);
		byte[] array = new byte[sizeInt]; 
		new Random().nextBytes(array);
		return new String(array, Charset.forName("UTF-8"));
	}
}
