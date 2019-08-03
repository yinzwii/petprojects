package com.ngsl.util;
/**
 * 命名工具类
 * @author yinzw
 *
 */
public class NamingUtil {
	
	public static final String UNDERLINE = "_";

	/**
	 * 含下划线的字符串转化为大驼峰字符串
	 * @param str
	 * @return
	 */
	public static String toUpperCamelCase(String str) {
		String[] strArr = str.split(UNDERLINE);
		for(int i = 0; i < strArr.length; i++) {
			strArr[i] = capitalize(strArr[i]);
		}
		return String.join("", strArr);
	}
	/**
	 * 转化为小驼峰字符串
	 * @param str
	 * @return
	 */
	public static String toLowerCamelCase(String str) {
		String[] strArr = str.split(UNDERLINE);
		if(str != null && str.length() > 0) {
			strArr[0] = strArr[0].toLowerCase();
		}
		for(int i = 1; i < strArr.length; i++) {
			strArr[i] = capitalize(strArr[i]);
		}
		return String.join("", strArr);
	}
	
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
		String fChar = str.charAt(0) + ""; 
		return fChar.toUpperCase() + str.substring(1).toLowerCase();
	}
	
	/**
	 * 判断字符串是否含有前缀
	 * @param str
	 * @return
	 */
	public static boolean hasPrefix(String str,String prefix) {
		int inxL = str.indexOf(prefix.toLowerCase());
		int inxU = str.indexOf(prefix.toUpperCase());
		if(inxL != -1 || inxU != -1 ) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 删除前缀
	 * @param str
	 * @return
	 */
	public static String removePrefix(String str,String prefix) {
		int inx = str.toLowerCase().indexOf(prefix.toLowerCase());
		if(inx != -1) {
			return str.substring(inx + prefix.length());
		}else {
			return str;
		}
	}
	
	/**
	 * 添加后缀
	 * @param str
	 * @param suffix
	 * @return
	 */
	public static String addSuffix(String str,String suffix) {
		if(str.endsWith(suffix)) {
			return str;
		}else {
			return str + suffix;
		}
	}
	
	
}
