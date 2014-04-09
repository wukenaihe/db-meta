package com.cgs.db.meta.schema;

import java.io.Serializable;
import java.util.Map;

/**
 * In SQL-92 ,this Schema class means catalog and schema.So, SchemaInfo has 
 * catalog name and schema name.
 * 
 *  <p>Oracle does not support catalog,so Schema class = schema
 *  <p>My Sql does not support schema, so Schema class= catalog
 *  <p>Sql Server support all of them, so Schema class= catalog.schema
 * 
 * @author xumh
 *
 */
public class Schema implements Serializable{

	private static final long serialVersionUID = 7247506286961678313L;

	private SchemaInfo schemaInfo;
	
	private Map<String, Table> tables;

	public SchemaInfo getSchemaInfo() {
		return schemaInfo;
	}

	public void setSchemaInfo(SchemaInfo schemaInfo) {
		this.schemaInfo = schemaInfo;
	}

	public Map<String, Table> getTables() {
		return tables;
	}

	public void setTables(Map<String, Table> tables) {
		this.tables = tables;
	}

	@Override
	public String toString() {
		return "Schema [schemaInfo=" + schemaInfo + ", tables=" + tables + "]";
	}
	
}
