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
}
