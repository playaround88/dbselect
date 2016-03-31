package com.ai.dbselect.common;


public class SqlTypeUtil {
	
	public static String type(String sql){
		sql=sql.toLowerCase().trim();
//		String type=sql.substring(0, sql.indexOf(" "));
//		return type.trim();
		if(sql.startsWith("select")){
			return "select";
		}else{
			return "update";
		}
	}

}
