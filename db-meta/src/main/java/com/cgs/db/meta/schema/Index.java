package com.cgs.db.meta.schema;

import java.io.Serializable;
import java.util.List;

public class Index implements Serializable{

	private static final long serialVersionUID = -4194272041788122320L;
	
	private String name;
	private boolean isUnique;
	private IndexType indexType;
	private int pages;
	private StringBuilder definition;
	private List<String> columnNames;
	
	public boolean isUnique() {
		return isUnique;
	}
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	public IndexType getIndexType() {
		return indexType;
	}
	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;
	}
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public StringBuilder getDefinition() {
		return definition;
	}
	public void setDefinition(StringBuilder definition) {
		this.definition = definition;
	}
	public List<String> getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		return "Index [isUnique=" + isUnique + ", indexType=" + indexType + ", pages=" + pages + ", definition=" + definition + ", columnNames=" + columnNames
				+ "]";
	}
	
	
	
}
