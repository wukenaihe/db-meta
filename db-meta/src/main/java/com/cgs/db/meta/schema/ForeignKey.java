package com.cgs.db.meta.schema;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

public class ForeignKey implements Serializable{

	private static final long serialVersionUID = 3273554634724597699L;

	private String name;

	private SortedSet<ForeignKeyColumnReference> columnReferences = new TreeSet<ForeignKeyColumnReference>();

	private ForeignKeyUpdateRule updateRule;
	private ForeignKeyUpdateRule deleteRule;
	private ForeignKeyDeferrability deferrability;
	public SortedSet<ForeignKeyColumnReference> getColumnReferences() {
		return columnReferences;
	}
	public void setColumnReferences(SortedSet<ForeignKeyColumnReference> columnReferences) {
		this.columnReferences = columnReferences;
	}
	public ForeignKeyUpdateRule getUpdateRule() {
		return updateRule;
	}
	public void setUpdateRule(ForeignKeyUpdateRule updateRule) {
		this.updateRule = updateRule;
	}
	public ForeignKeyUpdateRule getDeleteRule() {
		return deleteRule;
	}
	public void setDeleteRule(ForeignKeyUpdateRule deleteRule) {
		this.deleteRule = deleteRule;
	}
	public ForeignKeyDeferrability getDeferrability() {
		return deferrability;
	}
	public void setDeferrability(ForeignKeyDeferrability deferrability) {
		this.deferrability = deferrability;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ForeignKey [name=" + name + ", columnReferences=" + columnReferences + ", updateRule=" + updateRule + ", deleteRule=" + deleteRule
				+ ", deferrability=" + deferrability + "]";
	}
	
	
}
