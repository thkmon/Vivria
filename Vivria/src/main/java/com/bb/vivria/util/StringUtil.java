package com.bb.vivria.util;

public class StringUtil {
	
	public static String cutLeft(String str, String delimiter) {
		if (str == null || str.length() == 0) {
			return "";
		}
		
		if (delimiter == null || delimiter.length() == 0) {
			return str;
		}
		
		int delimiterIndex = str.indexOf(delimiter);
		if (delimiterIndex < 0) {
			return str;
		}
	
		return str.substring(0, delimiterIndex);
	}
	
	
	public static String cutRight(String str, String delimiter) {
		if (str == null || str.length() == 0) {
			return "";
		}
		
		if (delimiter == null || delimiter.length() == 0) {
			return str;
		}
		
		int delimiterIndex = str.indexOf(delimiter);
		if (delimiterIndex < 0) {
			return str;
		}
	
		return str.substring(delimiterIndex + delimiter.length());
	}
	
	
	public static int parseInt(String str) {
		if (str == null || str.length() == 0) {
			return 0;
		}
		
		int result = 0;
		
		try {
			result = Integer.parseInt(str);
		} catch (Exception e) {
			result = 0;
		}
		
		return result;
	}
	
	
	public static String parseString(Object obj) {
		if (obj == null) {
			return "";
		}
		
		String str = "";
		
		try {
			str = String.valueOf(obj);
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	public static String nullToEmpty(String str) {
		if (str == null) {
			return "";
		}
		
		return str;
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
}