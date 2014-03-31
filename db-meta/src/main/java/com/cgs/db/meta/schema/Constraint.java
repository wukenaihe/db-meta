package com.cgs.db.meta.schema;

/**
 * Represents a table constraint.
 * 
 * @author xumh
 *
 */
public class Constraint {
	
	private TableConstraintType tableConstraintType;
	private boolean deferrable;
	private boolean initiallyDeferred;
	private StringBuilder definition;//such as "D1 IS NOT NULL"
	
	
	public TableConstraintType getTableConstraintType() {
		return tableConstraintType;
	}
	public void setTableConstraintType(TableConstraintType tableConstraintType) {
		this.tableConstraintType = tableConstraintType;
	}
	public boolean isDeferrable() {
		return deferrable;
	}
	public void setDeferrable(boolean deferrable) {
		this.deferrable = deferrable;
	}
	public boolean isInitiallyDeferred() {
		return initiallyDeferred;
	}
	public void setInitiallyDeferred(boolean initiallyDeferred) {
		this.initiallyDeferred = initiallyDeferred;
	}
	public StringBuilder getDefinition() {
		return definition;
	}
	public void setDefinition(StringBuilder definition) {
		this.definition = definition;
	}
	
	
}
