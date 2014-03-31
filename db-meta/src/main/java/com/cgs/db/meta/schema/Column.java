package com.cgs.db.meta.schema;

import java.awt.List;

public class Column {
	private String name;
	private String comment;
	private boolean unique;
	private boolean nullable;
	private int type;//just the java.sql.Type
	private String typeName;//column type name
	private int length;
	private int precision;
	private int scale;
	private String defaultValue;
	 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Override
	public String toString() {
		return "Column [name=" + name + ", comment=" + comment + ", unique=" + unique + ", nullable=" + nullable + ", type=" + type + ", typeName=" + typeName
				+ ", length=" + length + ", precision=" + precision + ", scale=" + scale + ", defaultValue=" + defaultValue + "]";
	}
	
	
	
}
