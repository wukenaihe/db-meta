package com.cgs.db.meta.schema;

import java.util.List;

public class Index {
	private boolean isUnique;
	private IndexType indexType;
	private int pages;
	private StringBuilder definition;
	private List<Column> columns;
	
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
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	
}
