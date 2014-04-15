package com.cgs.db.meta.schema;

import java.io.Serializable;

public class Trigger extends Routine implements Serializable{

	private static final long serialVersionUID = -1867385892512564099L;
	
	private String tableName;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "Trigger [tableName=" + tableName + ", name=" + name + ", definition=" + getDefinition()  + "]";
	}
	
	

}
