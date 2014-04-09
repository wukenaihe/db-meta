package com.cgs.db.meta.schema;

import java.io.Serializable;

import com.cgs.db.util.Utility;

/**
 * 
 * 
 * @author xumh
 * 
 */
public class SchemaInfo implements Serializable {

	private static final long serialVersionUID = 6989202966373640091L;
	
	private String catalogName;
	private String schemaName;
	private transient String fullName;

	public SchemaInfo() {
		this(null, null);
	}

	public SchemaInfo(String catalogName, String schemaName) {
		this.catalogName = catalogName;
		this.schemaName = schemaName;
	}

	private void buildFullName() {
		if (fullName == null) {
			final boolean hasCatalogName = !Utility.isBlank(catalogName);
			final boolean hasSchemaName = !Utility.isBlank(schemaName);
			fullName = (hasCatalogName ? catalogName : "") + (hasCatalogName && hasSchemaName ? "." : "") + (hasSchemaName ? schemaName : "");
		}
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getFullName() {
		buildFullName();
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String toString(){
		buildFullName();
		return getFullName();
	}
}
