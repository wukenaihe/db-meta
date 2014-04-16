package com.cgs.db.meta.core;

/**
 * 
 * Descriptor for level of schema detail.
 * 
 * @author xumh
 *
 */
public final class SchemaInfoLevel {

	  private boolean retrieveJdbcDriverInfo = true;
	  private boolean retrieveDatabaseInfo = true;
	  private boolean retrieveTable;
	  private boolean retrievePrimaryKey;
	  private boolean retrieveTableConstraintInformation;
	  private boolean retrieveViewInformation;
	  private boolean retrieveIndexInformation;
	  private boolean retrieveForeignKeys;
//	  private boolean retrieveIndices;
	  private boolean retrieveTablePrivileges;
//	  private boolean retrieveTableColumnPrivileges;
	  private boolean retrieveTriggerInformation;
//	  private boolean retrieveSynonymInformation;
	  private boolean retrieveTableColumns;
//	  private boolean retrieveAdditionalTableAttributes;
//	  private boolean retrieveAdditionalColumnAttributes;
	  private String tag;
	  
	  public static SchemaInfoLevel min(){
		  SchemaInfoLevel min=new SchemaInfoLevel();
		  min.setTag("MinLevel");
		  min.setRetrieveTable(true);
		  min.setRetrieveTableColumns(true);
		  return min;
	  }
	  
	  public static SchemaInfoLevel standard(){
		  SchemaInfoLevel standard=new SchemaInfoLevel();
		  standard.setTag("standard");
		  standard.setRetrieveTable(true);
		  standard.setRetrieveTableColumns(true);
		  standard.setRetrieveForeignKeys(true);
		  standard.setRetrievePrimaryKey(true);
		  standard.setRetrieveIndexInformation(true);
		  return standard;
	  }
	  
	  public static SchemaInfoLevel max(){
		  
		  
		  SchemaInfoLevel max=new SchemaInfoLevel();
		  max.setTag("Standard");
		  max.setRetrieveTable(true);
		  max.setRetrieveTableColumns(true);
		  max.setRetrieveTableConstraintInformation(true);
		  max.setRetrieveViewInformation(true);
		  max.setRetrieveForeignKeys(true);
		  max.setRetrievePrimaryKey(true);
		  max.setRetrieveTablePrivileges(true);
		  max.setRetrieveTriggerInformation(true);
		  max.setRetrieveIndexInformation(true);
		  return max;
	  }

	public boolean isRetrieveJdbcDriverInfo() {
		return retrieveJdbcDriverInfo;
	}

	public void setRetrieveJdbcDriverInfo(boolean retrieveJdbcDriverInfo) {
		this.retrieveJdbcDriverInfo = retrieveJdbcDriverInfo;
	}

	public boolean isRetrieveDatabaseInfo() {
		return retrieveDatabaseInfo;
	}

	public void setRetrieveDatabaseInfo(boolean retrieveDatabaseInfo) {
		this.retrieveDatabaseInfo = retrieveDatabaseInfo;
	}


	public boolean isRetrieveTable() {
		return retrieveTable;
	}

	public void setRetrieveTable(boolean retrieveTable) {
		this.retrieveTable = retrieveTable;
	}

	public boolean isRetrieveTableConstraintInformation() {
		return retrieveTableConstraintInformation;
	}

	public void setRetrieveTableConstraintInformation(boolean retrieveTableConstraintInformation) {
		this.retrieveTableConstraintInformation = retrieveTableConstraintInformation;
	}

	public boolean isRetrieveViewInformation() {
		return retrieveViewInformation;
	}

	public void setRetrieveViewInformation(boolean retrieveViewInformation) {
		this.retrieveViewInformation = retrieveViewInformation;
	}

	public boolean isRetrieveIndexInformation() {
		return retrieveIndexInformation;
	}

	public void setRetrieveIndexInformation(boolean retrieveIndexInformation) {
		this.retrieveIndexInformation = retrieveIndexInformation;
	}

	public boolean isRetrieveForeignKeys() {
		return retrieveForeignKeys;
	}

	public void setRetrieveForeignKeys(boolean retrieveForeignKeys) {
		this.retrieveForeignKeys = retrieveForeignKeys;
	}

	public boolean isRetrieveTablePrivileges() {
		return retrieveTablePrivileges;
	}

	public void setRetrieveTablePrivileges(boolean retrieveTablePrivileges) {
		this.retrieveTablePrivileges = retrieveTablePrivileges;
	}


	public boolean isRetrieveTriggerInformation() {
		return retrieveTriggerInformation;
	}

	public void setRetrieveTriggerInformation(boolean retrieveTriggerInformation) {
		this.retrieveTriggerInformation = retrieveTriggerInformation;
	}

	public boolean isRetrieveTableColumns() {
		return retrieveTableColumns;
	}

	public void setRetrieveTableColumns(boolean retrieveTableColumns) {
		this.retrieveTableColumns = retrieveTableColumns;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isRetrievePrimaryKey() {
		return retrievePrimaryKey;
	}

	public void setRetrievePrimaryKey(boolean retrievePrimaryKey) {
		this.retrievePrimaryKey = retrievePrimaryKey;
	}
	  
	  
}
