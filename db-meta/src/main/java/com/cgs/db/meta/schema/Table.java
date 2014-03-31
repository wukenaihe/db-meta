package com.cgs.db.meta.schema;

import java.util.List;
import java.util.Map;

public class Table {
	private TableType tableType = TableType.unknown; // Default value
	private String name;
	private String comment;
	
	private Map<String, Column> columns;
	private PrimaryKey primaryKey;
	private List<ForeignKey> foreignkeys;
	private List<Index> indexs;
	private List<Constraint> constraints;
	private List<Trigger> triggers;
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
	public TableType getTableType() {
		return tableType;
	}
	public void setTableType(TableType tableType) {
		this.tableType = tableType;
	}
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
	public Map<String, Column> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, Column> columns) {
		this.columns = columns;
	}
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	public List<ForeignKey> getForeignkeys() {
		return foreignkeys;
	}
	public void setForeignkeys(List<ForeignKey> foreignkeys) {
		this.foreignkeys = foreignkeys;
	}
	public List<Index> getIndexs() {
		return indexs;
	}
	public void setIndexs(List<Index> indexs) {
		this.indexs = indexs;
	}
	public List<Trigger> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
	@Override
	public String toString() {
		return "Table [tableType=" + tableType + ", name=" + name + ", comment=" + comment + ", columns=" + columns + ", primaryKey=" + primaryKey
				+ ", foreignkeys=" + foreignkeys + ", indexs=" + indexs + ", constraints=" + constraints + ", triggers=" + triggers + "]";
	}

	
}
