package com.cgs.db.meta.schema;

import java.io.Serializable;
import java.util.List;

public class PrimaryKey implements Serializable{

	private static final long serialVersionUID = 6913288546311329557L;
	
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
