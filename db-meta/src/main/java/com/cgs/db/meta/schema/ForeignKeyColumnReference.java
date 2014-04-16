package com.cgs.db.meta.schema;

import java.io.Serializable;

public class ForeignKeyColumnReference implements Comparable<ForeignKeyColumnReference>,Serializable {

	private static final long serialVersionUID = 7085822899635585614L;
	
	public static class ColumnReference {
		private String catalog;
		private String schema;
		private String table;
		private String column;
		public ColumnReference(){
			
		}
		
		public ColumnReference(String catalog,String schema,String table,String column){
			this.catalog=catalog;
			this.schema=schema;
			this.table=table;
			this.column=column;
		}
		
		public String getSchema() {
			return schema;
		}
		public void setSchema(String schema) {
			this.schema = schema;
		}
		public String getTable() {
			return table;
		}
		public void setTable(String table) {
			this.table = table;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		public String getCatalog() {
			return catalog;
		}
		public void setCatalog(String catalog) {
			this.catalog = catalog;
		}

		
		public String toString() {
			return "ColumnReference [catalog=" + catalog + ", schema=" + schema + ", table=" + table + ", column=" + column + "]";
		}
		
	}
	
	private ColumnReference foreignColumn;
	private ColumnReference primaryColumn;
	private int keySequence;
	public ColumnReference getForeignColumn() {
		return foreignColumn;
	}
	public void setForeignColumn(ColumnReference foreignColumn) {
		this.foreignColumn = foreignColumn;
	}
	public ColumnReference getPrimaryColumn() {
		return primaryColumn;
	}
	public void setPrimaryColumn(ColumnReference primaryColumn) {
		this.primaryColumn = primaryColumn;
	}
	public int getKeySequence() {
		return keySequence;
	}
	public void setKeySequence(int keySequence) {
		this.keySequence = keySequence;
	}
	
	public int compareTo(ForeignKeyColumnReference o) {
		return keySequence-o.getKeySequence();
	}
	@Override
	public String toString() {
		return "ForeignKeyColumnReference [foreignColumn=" + foreignColumn + ", primaryColumn=" + primaryColumn + ", keySequence=" + keySequence + "]";
	}
	
	
}
