package com.cgs.db.util;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Map;
import java.util.Set;

import com.cgs.db.meta.schema.Column;
import com.cgs.db.meta.schema.Table;

public class PrintUtils {
	static String newLine=System.getProperty("line.separator");
	static String space="                                        ";
	  
	public static String getTableInfo(Table table){
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("================================================================="+newLine);
		stringBuilder.append(table.getName()+newLine);
		stringBuilder.append("-----------------------------------------------------------------"+newLine);
		String title="列名%s类型%snull%s注释";
		title=String.format(title, space.substring(0, 30),space.substring(0, 30),space.substring(0, 30));
		stringBuilder.append(title+newLine);
		Map<String, Column> columns=table.getColumns();
		
		Set<String> keys=columns.keySet();
		for (String string : keys) {
			Column column=columns.get(string);
			String columnStr=getColumnInfo(column);
			stringBuilder.append(columnStr+newLine);
		}
		return stringBuilder.toString();
	}
	
	public static void printTable(Table table){
		String tableInfo=getTableInfo(table);
		System.out.print(tableInfo);
	}
	
	public static void printColumn(Column column){
		String columnInfo=getColumnInfo(column);
		System.out.println(columnInfo);
	}
	
	
	public static String getColumnInfo(Column column){
		String name=column.getName();//列名固定长为32，超过用...
		String sqlType=column.getTypeName();//类型固定长为10
		boolean isNull=column.isNullable();//固定一个是或者否
		String comment=column.getComment();//剩余其他
		
		
		
		if(name.length()<32){
			String spaceNum=space.substring(0, 32-name.length());
			name=name+spaceNum;
		}else{
			name=name.substring(0,29)+"...";
		}
		
		if(sqlType.length()<10){
			String spaceNum=space.substring(0, 10-sqlType.length());
			sqlType+=spaceNum;
		}else{
			sqlType=name.substring(0, 7)+"...";
		}
		
		String nullString="%s     ";
		if(isNull){
			nullString=String.format(nullString, "是");
		}else{
			nullString=String.format(nullString, "否");
		}
		
		String resultString=name+sqlType+nullString+comment;
		return resultString;
	}
	
	/**
	 * @param type java.sql.Types.类型
	 * @return sql数据类型名称
	 * @throws Exception
	 */
	public static String sqlType2String(int type) throws Exception{
		Class cl=Types.class;
		Field[] fields=cl.getFields();
		for (Field field : fields) {
			int value=field.getInt(null);
			if(type==value){
				return field.getName();
			}
		}
		return "";
	}
}
