package com.cgs.db.meta.schema;

import java.util.List;

public class PrimaryKey {
	private String name;
	private List<String> columns;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	@Override
	public String toString() {
		return "PrimaryKey [name=" + name + ", columns=" + columns + "]";
	}
	
	
}
